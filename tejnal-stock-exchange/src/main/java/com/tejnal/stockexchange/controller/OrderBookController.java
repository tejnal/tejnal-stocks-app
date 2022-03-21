package com.tejnal.stockexchange.controller;

import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.request.OrderSummaryRequest;
import com.tejnal.stockexchange.model.response.ExecutionResponse;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import com.tejnal.stockexchange.service.IOrderBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class OrderBookController {

  private final Logger logger = LoggerFactory.getLogger(OrderBookController.class);

  @Autowired private IOrderBookService orderBookService;

  /**
   * @param newOrderRequest
   * @return
   */
  @PostMapping("/orders")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createOrder(@Valid @RequestBody NewOrderRequest newOrderRequest) {
    UserDetails userDetails = fetchLoggedInUserDetails();
    OrderResponse orderResponse;
    try {
      orderResponse = orderBookService.createOrder(userDetails.getUsername(), newOrderRequest);
      logger.info("order response ======", orderResponse.toString());
    } catch (CustomInputException ex) {
      ex.printStackTrace();
      logger.error("====================================Error number 1");
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<ExecutionResponse>(eMessage, HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      ex.printStackTrace();
      logger.error("====================================Error number 2");
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<ExecutionResponse>(eMessage, HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(orderResponse);
  }

  /**
   * @param orderId
   * @return
   */
  @GetMapping("/orders/{orderId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> fetchOrder(@Valid @NotNull @PathVariable("orderId") Long orderId) {
    UserDetails userDetails = fetchLoggedInUserDetails();
    OrderResponse orderResponse;
    try {
      orderResponse = orderBookService.fetchOrder(userDetails.getUsername(), orderId);
    } catch (CustomInputException ex) {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(orderResponse);
  }

  /**
   * @param orderSummaryRequest
   * @return
   */
  @PostMapping("/orders/summary")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> fetchOrderSummary(
      @Valid @RequestBody OrderSummaryRequest orderSummaryRequest) {
    UserDetails userDetails = fetchLoggedInUserDetails();
    OrderSummaryResponse orderSummaryResponse;
    try {
      orderSummaryResponse =
          orderBookService.fetchOrderSummary(userDetails.getUsername(), orderSummaryRequest);
    } catch (CustomInputException ex) {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    } catch (Exception ex) {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "REQUEST VALIDATION FAILED!",
              ex.getMessage());
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok(orderSummaryResponse);
  }

  /** @return */
  @DeleteMapping("/test/orders")
  @PreAuthorize("hasRole('USER')")
  @Profile("test")
  public ResponseEntity<ExecutionResponse> deleteOrders() {
    boolean isDeletionCompleted;
    try {
      isDeletionCompleted = orderBookService.truncateAllOrdersForTest();
    } catch (Exception ex) {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(),
              new Date(),
              "INTERNAL SERVER ERROR OCCURED!",
              ex.getMessage());
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    }

    if (isDeletionCompleted) {
      ExecutionResponse eMessage =
          new ExecutionResponse(HttpStatus.OK.value(), new Date(), "SUCCESS", null);
      return new ResponseEntity<>(eMessage, HttpStatus.OK);
    } else {
      ExecutionResponse eMessage =
          new ExecutionResponse(
              HttpStatus.BAD_REQUEST.value(), new Date(), "REQUEST VALIDATION FAILED!", null);
      return new ResponseEntity<>(eMessage, HttpStatus.BAD_REQUEST);
    }
  }

  private UserDetails fetchLoggedInUserDetails() {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    logger.info("Logged in user is -> {}", userDetails.getUsername());
    return userDetails;
  }
}
