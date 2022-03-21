package com.tejnal.stockexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejnal.stockexchange.TejnalStockExchangeApplication;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import com.tejnal.stockexchange.service.Impl.OrderBookService;
import com.tejnal.stockexchange.validation.TestConstraintValidationFactory;
import com.tejnal.stockexchange.validator.CurrencyValidator;
import com.tejnal.stockexchange.validator.TickerValidator;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.validation.Validator;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TejnalStockExchangeApplication.class, MockServletContext.class})
public class OrderBookControllerUnitTest {

  @InjectMocks private OrderBookController orderBookController;

  @Mock private OrderBookService orderBookService;

  private MockMvc mvc;

  @Autowired private MockServletContext servletContext;

  @Bean
  public Validator validatorFactory() {
    return new LocalValidatorFactoryBean();
  }

  @Before
  public void setup() throws CustomInputException, Exception {
    MockitoAnnotations.initMocks(this);

    LocalValidatorFactoryBean validatorFactoryBean = getCustomValidatorFactoryBean();

    this.mvc =
        MockMvcBuilders.standaloneSetup(orderBookController)
            .setValidator(validatorFactoryBean)
            .build();

    // Return dummy order response
    OrderResponse dummyOrderResponse =
        new OrderResponse(0L, "DUMMY_TCKR_1", EOrderSide.BUY, 2000L, 200.25, "INR", new Date());
    Mockito.when(orderBookService.createOrder(Mockito.anyString(), Mockito.any()))
        .thenReturn(dummyOrderResponse);
    Mockito.when(orderBookService.fetchOrder(Mockito.anyString(), Mockito.anyLong()))
        .thenReturn(dummyOrderResponse);
    Mockito.when(orderBookService.isTickerValid(Mockito.anyString())).thenReturn(true);
    Mockito.when(orderBookService.isCurrencyValid(Mockito.anyString())).thenReturn(true);
  }

  private LocalValidatorFactoryBean getCustomValidatorFactoryBean() {
    final GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
    final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

    beanFactory.registerSingleton(
        CurrencyValidator.class.getCanonicalName(), new CurrencyValidator());
    beanFactory.registerSingleton(TickerValidator.class.getCanonicalName(), new TickerValidator());

    context.refresh();

    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setApplicationContext(context);

    TestConstraintValidationFactory constraintFactory =
        new TestConstraintValidationFactory(context, this.orderBookService);

    validatorFactoryBean.setConstraintValidatorFactory(constraintFactory);
    validatorFactoryBean.setProviderClass(HibernateValidator.class);
    validatorFactoryBean.afterPropertiesSet();
    return validatorFactoryBean;
  }

  @WithMockUser("spring")
  @Test
  public void when_create_order_it_should_succeeed_with_200() throws Exception {
    NewOrderRequest newOrderRequest = prepareDummyOrderRequest();
    ObjectMapper objectMapper = new ObjectMapper();
    String requestPayload = objectMapper.writeValueAsString(newOrderRequest);

    mvc.perform(
            post("/api/users/orders")
                .content(requestPayload)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  private NewOrderRequest prepareDummyOrderRequest() {
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("DUMMY_TCKR_1", EOrderSide.BUY, 2000L, 200.25, "INR");
    return newOrderRequest;
  }

  @WithMockUser("spring")
  @Test
  public void when_create_order_it_should_save_and_return_same_value() throws Exception {
    NewOrderRequest newOrderRequest = prepareDummyOrderRequest();
    ObjectMapper objectMapper = new ObjectMapper();
    String requestPayload = objectMapper.writeValueAsString(newOrderRequest);

    mvc.perform(
            post("/api/users/orders")
                .content(requestPayload)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect((jsonPath("order_id").isNotEmpty()))
        .andExpect((jsonPath("ticker").value(newOrderRequest.getTicker())))
        .andExpect((jsonPath("currency").value(newOrderRequest.getCurrency())))
        .andExpect((jsonPath("order_side").value(newOrderRequest.getOrderSide().toString())))
        .andExpect((jsonPath("price").value(newOrderRequest.getPrice())))
        .andExpect((jsonPath("volume").value(newOrderRequest.getVolume())));
  }

  @WithMockUser("spring")
  @Test
  public void when_create_invalid_order_it_should_return_400() throws Exception {
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("DUMMY_TCKR_1", null, 2000L, 200.25, "INR");
    ObjectMapper objectMapper = new ObjectMapper();
    String requestPayload = objectMapper.writeValueAsString(newOrderRequest);

    mvc.perform(
            post("/api/users/orders")
                .content(requestPayload)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @WithMockUser("spring")
  @Test
  public void when_order_created_it_should_respond_with_given_order() throws Exception {

    mvc.perform(get("/api/users/orders/0").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect((jsonPath("order_id").value(0L)))
        .andExpect((jsonPath("ticker").value("DUMMY_TCKR_1")))
        .andExpect((jsonPath("currency").value("INR")))
        .andExpect((jsonPath("order_side").value("BUY")))
        .andExpect((jsonPath("price").value(200.25)))
        .andExpect((jsonPath("volume").value(2000L)));
  }
}
