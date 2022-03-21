package com.tejnal.stockexchange.service;

import com.tejnal.stockexchange.data.entity.Orders;
import com.tejnal.stockexchange.data.entity.Role;
import com.tejnal.stockexchange.data.entity.TickerMap;
import com.tejnal.stockexchange.data.entity.User;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.data.enums.ERole;
import com.tejnal.stockexchange.data.repository.OrderRepository;
import com.tejnal.stockexchange.data.repository.TickerMapRepository;
import com.tejnal.stockexchange.data.repository.UserRepository;
import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.request.OrderSummaryRequest;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.service.Impl.OrderBookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookServiceTest {

  @InjectMocks OrderBookService orderBookService;
  @Mock private OrderRepository ordersRepository;
  @Mock private UserRepository userRepository;
  @Mock private TickerMapRepository tickerMapRepository;
  private Orders dummyOrders;

  @Before
  public void setup() {
    User dummyUser = new User();
    dummyUser.setEmail("DUMMY_USR@gmail.com");
    dummyUser.setFirstName("Dummy");
    dummyUser.setLastName("User");
    dummyUser.setPassword("password.1");
    dummyUser.setUsername("DUMMY_USR");
    dummyUser.setId(0L);
    Set<Role> roleSet = new HashSet<Role>();
    roleSet.add(new Role(ERole.ROLE_USER));
    dummyUser.setRoles(roleSet);
    Mockito.when(userRepository.findByUsername(Mockito.anyString()))
        .thenReturn(Optional.of(dummyUser));

    // Dummy Order Response
    dummyOrders = new Orders("DUMMY_TCKR", EOrderSide.BUY, 2500L, 3400.0, "INR");
    dummyOrders.setCreatedDate(new Date());
    dummyOrders.setCreatedBy(0L);
    Mockito.when(ordersRepository.save(Mockito.any())).thenReturn(dummyOrders);
    Mockito.when(ordersRepository.fetchOrderById(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(Optional.of(dummyOrders));

    OrderSummaryResponse dummyOrderSummaryResponse =
        new OrderSummaryResponse(new Date(), 10.0, 5.0, 15.0, 2L);
    Mockito.when(
            ordersRepository.fetchOrderSummary(
                Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(dummyOrderSummaryResponse));

    TickerMap dummyTickerMap = new TickerMap("DUMMY_TCKR", "Description of dummy ticker");
    Mockito.when(tickerMapRepository.findByTickerCode("DUMMY_TCKR"))
        .thenReturn(Optional.of(dummyTickerMap));
    Mockito.when(tickerMapRepository.findByTickerCode("DUMMY_TCKR_INVALID"))
        .thenThrow(new NoSuchElementException());
  }

  @Test
  public void when_create_order_it_should_return_saved_order_with_same_details() throws Exception {
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("DUMMY_TCKR", EOrderSide.BUY, 2500L, 3400.0, "INR");
    OrderResponse savedOrderResponse = orderBookService.createOrder("DUMMY_USR", newOrderRequest);
    assertEquals(savedOrderResponse.getCurrency(), newOrderRequest.getCurrency());
    assertEquals(savedOrderResponse.getOrderSide(), newOrderRequest.getOrderSide());
    assertEquals(savedOrderResponse.getPrice(), newOrderRequest.getPrice());
    assertEquals(savedOrderResponse.getTicker(), newOrderRequest.getTicker());
    assertEquals(savedOrderResponse.getVolume(), newOrderRequest.getVolume());
    assertNotNull(savedOrderResponse.getOrderDate());
  }

  @Test
  public void when_fetch_order_it_should_return_order_details() throws Exception {
    OrderResponse savedOrderResponse = orderBookService.fetchOrder("DUMMY_USR", 0L);
    assertEquals(savedOrderResponse.getCurrency(), dummyOrders.getCurrency());
    assertEquals(savedOrderResponse.getOrderSide(), dummyOrders.getOrderSide());
    assertEquals(savedOrderResponse.getPrice(), dummyOrders.getPrice());
    assertEquals(savedOrderResponse.getTicker(), dummyOrders.getTicker());
    assertEquals(savedOrderResponse.getVolume(), dummyOrders.getVolume());
  }

  @Test
  public void when_fetch_order_summary_it_should_return_valid_order_summary() throws Exception {
    OrderSummaryRequest dummyOrderSummaryRequest =
        new OrderSummaryRequest("DUMMY_TCKR", EOrderSide.BUY, new Date());
    OrderSummaryResponse orderSummaryResponse =
        orderBookService.fetchOrderSummary("DUMMY_USR", dummyOrderSummaryRequest);
    assertNotNull(orderSummaryResponse.getAverage());
    assertNotNull(orderSummaryResponse.getMinimum());
    assertNotNull(orderSummaryResponse.getMaximum());
    assertNotNull(orderSummaryResponse.getOrdersCount());
  }

  @Test
  public void when_valid_ticker_check_it_should_return_true() throws Exception {
    boolean isTickerValid = orderBookService.isTickerValid("DUMMY_TCKR");
    assertTrue(isTickerValid);
  }

  @Test
  public void when_valid_ticker_check_it_should_return_false() throws Exception {
    boolean isTickerValid = orderBookService.isTickerValid("DUMMY_TCKR_INVALID");
    assertFalse(isTickerValid);
  }
}
