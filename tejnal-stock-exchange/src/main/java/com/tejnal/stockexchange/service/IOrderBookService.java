package com.tejnal.stockexchange.service;

import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.request.OrderSummaryRequest;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import org.springframework.context.annotation.Profile;

public interface IOrderBookService {

  OrderResponse createOrder(String loggedInUserName, NewOrderRequest newOrderRequest)
      throws CustomInputException, Exception;

  OrderResponse fetchOrder(String loggedInUserName, Long orderId);

  OrderSummaryResponse fetchOrderSummary(
      String loggedInUserName, OrderSummaryRequest orderSummaryRequest);

  boolean isTickerValid(String tickerCode);

  boolean isCurrencyValid(String currency);

  @Profile("test")
  boolean truncateAllOrdersForTest();
}
