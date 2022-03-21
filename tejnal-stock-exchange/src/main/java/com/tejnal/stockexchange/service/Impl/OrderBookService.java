package com.tejnal.stockexchange.service.Impl;

import com.tejnal.stockexchange.data.entity.Orders;
import com.tejnal.stockexchange.data.entity.User;
import com.tejnal.stockexchange.data.repository.OrderRepository;
import com.tejnal.stockexchange.data.repository.TickerMapRepository;
import com.tejnal.stockexchange.data.repository.UserRepository;
import com.tejnal.stockexchange.mapper.IOrderEntityToOrderResponseMapper;
import com.tejnal.stockexchange.mapper.IOrderRequestToOrderEntityMapper;
import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.request.OrderSummaryRequest;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import com.tejnal.stockexchange.service.IOrderBookService;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderBookService implements IOrderBookService {

  private static final Logger logger = LoggerFactory.getLogger(OrderBookService.class);

  private IOrderRequestToOrderEntityMapper orderRequestToOrderEntityMapper =
      Mappers.getMapper(IOrderRequestToOrderEntityMapper.class);
  private IOrderEntityToOrderResponseMapper orderEntityToOrderReponseMapper =
      Mappers.getMapper(IOrderEntityToOrderResponseMapper.class);

  @Autowired private OrderRepository orderRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TickerMapRepository tickerMapRepository;

  @Override
  public OrderResponse createOrder(String loggedInUserName, NewOrderRequest newOrderRequest)
      throws CustomInputException, Exception {
    // Fetch User details
    User userDetails = fetchUserDetailsFromDb(loggedInUserName);

    Orders newOrder = orderRequestToOrderEntityMapper.mapOrderRequestToOrderEntity(newOrderRequest);
    newOrder.setCreatedBy(userDetails.getId());
    newOrder.setCreatedDate(new Date());

    Orders savedOrder = orderRepository.save(newOrder);
    OrderResponse orderResponse =
        orderEntityToOrderReponseMapper.mapSavedOrderEntityToOrderResponse(savedOrder);

    return orderResponse;
  }

  @Override
  public OrderResponse fetchOrder(String loggedInUserName, Long orderId) {
    // Fetch User details
    User userDetails = fetchUserDetailsFromDb(loggedInUserName);
    OrderResponse orderResponse;
    try {
      Orders existingOrder = orderRepository.fetchOrderById(userDetails.getId(), orderId).get();
      orderResponse =
          orderEntityToOrderReponseMapper.mapSavedOrderEntityToOrderResponse(existingOrder);
    } catch (NoSuchElementException nse) {
      throw new CustomInputException("INVALID INPUT", "No order was found for given input.");
    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomInputException(
          "TECHNICAL ERROR", "An internal error occured while pulling order details.");
    }
    return orderResponse;
  }

  @Override
  public OrderSummaryResponse fetchOrderSummary(
      String loggedInUserName, OrderSummaryRequest orderSummaryRequest) {
    OrderSummaryResponse orderSummaryResponse = null;
    try {
      // Fetch User details
      User userDetails = fetchUserDetailsFromDb(loggedInUserName);

      orderSummaryResponse =
          orderRepository
              .fetchOrderSummary(
                  userDetails.getId(),
                  orderSummaryRequest.getTicker(),
                  orderSummaryRequest.getOrderSide(),
                  orderSummaryRequest.getOrderDate())
              .get();
    } catch (NoSuchElementException nse) {
      throw new CustomInputException("INVALID INPUT", "No orders were found for given input.");
    } catch (Exception e) {
      throw new CustomInputException(
          "TECHNICAL ERROR", "An internal error occured while pulling order summary.");
    }
    return orderSummaryResponse;
  }

  @Override
  public boolean isTickerValid(String tickerCode) {
    boolean isTickerValid = false;
    try {
      tickerMapRepository.findByTickerCode(tickerCode).get();
      isTickerValid = true;
    } catch (NoSuchElementException nse) {
      logger.info("No element found for - " + tickerCode);
    }
    return isTickerValid;
  }

  @Override
  public boolean isCurrencyValid(String currency) {
    boolean isCurrencyValid = true;
    User userDetailsfromDB = fetchLoggedInUserDetailsFromDB();
    try {
      String dbCurrencyForUser =
          orderRepository.fetchDistinctOrderCurrByUserOnSameDay(userDetailsfromDB.getId()).get();
      if (null != dbCurrencyForUser && !dbCurrencyForUser.equalsIgnoreCase(currency)) {
        isCurrencyValid = false;
      }
    } catch (NoSuchElementException nse) {
      logger.info("No record found for currency - " + isCurrencyValid);
    }
    return isCurrencyValid;
  }

  @Override
  public boolean truncateAllOrdersForTest() {
    boolean isDeletionSuccessful = false;
    try {
      orderRepository.truncateAllOrdersForTest();
      isDeletionSuccessful = true;
    } catch (Exception nse) {
      nse.printStackTrace();
      logger.info("Exception while deleting orders for test - Assertions may go wrong.");
    }
    return isDeletionSuccessful;

  }

  private User fetchUserDetailsFromDb(String loggedInUserName) {
    return userRepository.findByUsername(loggedInUserName).get();
  }

  protected User fetchLoggedInUserDetailsFromDB() {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // Fetch User details
    User userDetailsfromDB = fetchUserDetailsFromDb(userDetails.getUsername());
    return userDetailsfromDB;
  }
}
