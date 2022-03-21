package com.tejnal.stockexchange.config;

import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.service.Impl.OrderBookService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Date;

@TestConfiguration
@TestPropertySource(
    locations = {"classpath:application-test.properties", "classpath:application.properties"})
public class SpringTestConfig {
  private RestTemplate template = new RestTemplate();

  @Bean
  public RestTemplate restTemplate() {
    return template;
  }

  @Bean
  public OrderBookService orderBookService() throws Exception {
    OrderBookService orderBookService = Mockito.mock(OrderBookService.class);
    // Return dummy order response
    OrderResponse dummyOrderResponse =
        new OrderResponse(0L, "DUMMY_TCKR_1", EOrderSide.BUY, 2000L, 200.25, "SEK", new Date());
    Mockito.when(orderBookService.createOrder(Mockito.anyString(), Mockito.any()))
        .thenReturn(dummyOrderResponse);
    Mockito.when(orderBookService.fetchOrder(Mockito.anyString(), Mockito.anyLong()))
        .thenReturn(dummyOrderResponse);

    // Return dummy order summary response
    OrderSummaryResponse dummyOrderSummaryResponse =
        new OrderSummaryResponse(new Date(), 200.0, 150.0, 250.0, 2L);
    Mockito.when(orderBookService.fetchOrderSummary(Mockito.anyString(), Mockito.any()))
        .thenReturn(dummyOrderSummaryResponse);
    Mockito.when(orderBookService.isCurrencyValid(Mockito.anyString())).thenReturn(true);
    Mockito.when(orderBookService.isTickerValid(Mockito.anyString())).thenReturn(true);
    return orderBookService;
  }

  @Bean(name = "mvcHandlerMappingIntrospector")
  public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
    return new HandlerMappingIntrospector();
  }
}
