package com.tejnal.stockexchange.controller;

import com.tejnal.stockexchange.TejnalStockExchangeApplication;
import com.tejnal.stockexchange.common.GenericTestProperty;
import com.tejnal.stockexchange.config.SpringTestConfig;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.model.request.LoginRequest;
import com.tejnal.stockexchange.model.request.NewOrderRequest;
import com.tejnal.stockexchange.model.request.OrderSummaryRequest;
import com.tejnal.stockexchange.model.request.SignupRequest;
import com.tejnal.stockexchange.model.response.ExecutionResponse;
import com.tejnal.stockexchange.model.response.JwtResponse;
import com.tejnal.stockexchange.model.response.OrderResponse;
import com.tejnal.stockexchange.model.response.OrderSummaryResponse;
import com.tejnal.stockexchange.security.WebConfig;
import com.tejnal.stockexchange.security.WebSecurityConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {
      TejnalStockExchangeApplication.class,
      SpringTestConfig.class,
      WebSecurityConfig.class,
      WebConfig.class
    })
@TestPropertySource(
    locations = {"classpath:application-test.properties", "classpath:application.properties"})
public class OrderBookControllerTest {

  private final Logger logger = LoggerFactory.getLogger(OrderBookControllerTest.class);

  public static final String AUTHORIZATION_HEADER = "Authorization";
  private static OrderResponse latestCreatedOrderResponse;
  private static boolean isUserSignedUp = false;
  private static long dynamicUserId;
  @Autowired private GenericTestProperty genericTestProperty;
  private String token;

  @PostConstruct
  public void signUpUser() {
    System.out.println("Entering signUpUser ....");
    // SignUp
    if (!isUserSignedUp) {
      dynamicUserId = System.currentTimeMillis();
      SignupRequest signUpRequest = new SignupRequest();
      signUpRequest.setEmail("DU_" + dynamicUserId + "@gmail.com");
      signUpRequest.setFirstName("Dummy");
      signUpRequest.setLastName("User");
      Set<String> roleSet = new HashSet<String>();
      roleSet.add("USER");
      signUpRequest.setRole(roleSet);
      signUpRequest.setPassword("password.1");

      signUpRequest.setUsername("DU_" + dynamicUserId);
      System.out.println("Servlet context is " + genericTestProperty.getServletContext());
      given()
          .basePath(genericTestProperty.getServletContext() + "/api/auth/signup")
          .port(genericTestProperty.getPortNumber())
          .contentType("application/json")
          .body(signUpRequest)
          .when()
          .post();

      isUserSignedUp = true;
    }
    System.out.println("Signed up value " + isUserSignedUp);
  }

  @Before
  public void authorization() {
    System.out.println("Entering authorization ....");

    // Login
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("DU_" + dynamicUserId);
    loginRequest.setPassword("password.1");
    token =
        given()
            .basePath(genericTestProperty.getServletContext() + "/api/auth/signin")
            .port(genericTestProperty.getPortNumber())
            .contentType("application/json")
            .body(loginRequest)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(JwtResponse.class)
            .getAccessToken();
    System.out.println("Token is " + token);
  }

  @Test
  public void
      when_create_user_with_invalid_token_it_should_respond_with_401_http_codeas_unaouthorized()
          throws Exception {
    RequestSpecification specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + "weruijdfjksdfm,sdfklsdfskldfklsdfsdf")
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("TSLA", EOrderSide.BUY, 2500L, 3400.0, "SEK");
    given()
        .spec(specification)
        .contentType("application/json")
        .body(newOrderRequest)
        .when()
        .post()
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  public void when_create_user_it_should_respond_with_200_http_code() throws Exception {
    logger.info("Using token -> " + this.token);
    RequestSpecification specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("TSLA", EOrderSide.BUY, 2500L, 3400.0, "SEK");
    OrderBookControllerTest.latestCreatedOrderResponse =
        given()
            .spec(specification)
            .contentType("application/json")
            .body(newOrderRequest)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(OrderResponse.class);
    System.out.println("ID -> " + latestCreatedOrderResponse.getOrderId());
    System.out.println("Ticker -> " + latestCreatedOrderResponse.getTicker());

    assertNotNull(latestCreatedOrderResponse.getOrderId());
    assertEquals(latestCreatedOrderResponse.getTicker(), newOrderRequest.getTicker());
    assertEquals(latestCreatedOrderResponse.getCurrency(), newOrderRequest.getCurrency());
    assertEquals(latestCreatedOrderResponse.getOrderSide(), newOrderRequest.getOrderSide());
    assertEquals(latestCreatedOrderResponse.getPrice(), newOrderRequest.getPrice());
    assertEquals(latestCreatedOrderResponse.getVolume(), newOrderRequest.getVolume());
  }

  @Test
  public void when_create_user_with_invalid_order_side_it_should_respond_with_bad_request_error()
      throws Exception {
    logger.info("Using token -> " + this.token);
    RequestSpecification specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest = new NewOrderRequest("TSLA", null, 2500L, 3400.0, "SEK");
    ExecutionResponse executionResponse =
        given()
            .spec(specification)
            .contentType("application/json")
            .body(newOrderRequest)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .body()
            .as(ExecutionResponse.class);

    assertNotNull(executionResponse.getDescription());
    assertEquals(executionResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void when_create_user_with_invalid_ticker_it_should_respond_with_bad_request_error()
      throws Exception {
    logger.info("Using token -> " + this.token);
    RequestSpecification specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest =
        new NewOrderRequest("GGFH", EOrderSide.BUY, 2500L, 3400.0, "SEK");
    ExecutionResponse executionResponse =
        given()
            .spec(specification)
            .contentType("application/json")
            .body(newOrderRequest)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .body()
            .as(ExecutionResponse.class);

    assertNotNull(executionResponse.getDescription());
    assertEquals(executionResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void when_order_created_it_should_respond_with_complete_order_data() throws Exception {
    logger.info("Using token -> " + this.token);
    logger.info(
        "Order ID is -> " + OrderBookControllerTest.latestCreatedOrderResponse.getOrderId());
    RequestSpecification specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(
                genericTestProperty.getServletContext()
                    + "/api/users/orders/"
                    + latestCreatedOrderResponse.getOrderId())
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    OrderResponse fetchedOrderResponse =
        given()
            .spec(specification)
            .contentType("application/json")
            .when()
            .get()
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(OrderResponse.class);

    assertEquals(fetchedOrderResponse.getOrderId(), latestCreatedOrderResponse.getOrderId());
    assertEquals(fetchedOrderResponse.getCurrency(), latestCreatedOrderResponse.getCurrency());
    assertEquals(fetchedOrderResponse.getOrderDate(), latestCreatedOrderResponse.getOrderDate());
    assertEquals(fetchedOrderResponse.getOrderSide(), latestCreatedOrderResponse.getOrderSide());
    assertEquals(fetchedOrderResponse.getPrice(), latestCreatedOrderResponse.getPrice());
    assertEquals(fetchedOrderResponse.getTicker(), latestCreatedOrderResponse.getTicker());
    assertEquals(fetchedOrderResponse.getVolume(), latestCreatedOrderResponse.getVolume());
  }

  @Test
  public void
      when_multiple_orders_created_it_should_respond_with_proper_min_max_and_avg_data_in_order_summary()
          throws Exception {
    logger.info("Using token -> " + this.token);
    logger.info(
        "Order ID is -> " + OrderBookControllerTest.latestCreatedOrderResponse.getOrderId());
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

    // Clear the existing orders in test DB
    RequestSpecification deleteSpecification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/test/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    given()
        .spec(deleteSpecification)
        .contentType("application/json")
        .when()
        .delete()
        .then()
        .statusCode(HttpStatus.SC_OK);

    // Create Order Set 1
    RequestSpecification orderSet1Specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest1 =
        new NewOrderRequest("TSLA", EOrderSide.BUY, 2500L, 3000.0, "SEK");
    given()
        .spec(orderSet1Specification)
        .contentType("application/json")
        .body(newOrderRequest1)
        .when()
        .post()
        .then()
        .statusCode(HttpStatus.SC_OK);

    // Create Order Set 2
    RequestSpecification orderSet2Specification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    NewOrderRequest newOrderRequest2 =
        new NewOrderRequest("TSLA", EOrderSide.BUY, 1000L, 5000.0, "SEK");
    given()
        .spec(orderSet2Specification)
        .contentType("application/json")
        .body(newOrderRequest2)
        .when()
        .post()
        .then()
        .statusCode(HttpStatus.SC_OK);

    // Fetch the Order Summary
    RequestSpecification orderSummarySpecification =
        new RequestSpecBuilder()
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + token)
            .setBasePath(genericTestProperty.getServletContext() + "/api/users/orders/summary")
            .setPort(genericTestProperty.getPortNumber())
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    System.out.println("New Date is -> " + new Date());
    OrderSummaryRequest orderSummaryRequest =
        new OrderSummaryRequest("TSLA", EOrderSide.BUY, new Date());
    OrderSummaryResponse orderSummaryResponse =
        given()
            .spec(orderSummarySpecification)
            .contentType("application/json")
            .body(orderSummaryRequest)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(OrderSummaryResponse.class);

    // Validate the AVG, MIN, MAX
    assertEquals(orderSummaryResponse.getAverage(), 3571.4285714285716);
    assertEquals(orderSummaryResponse.getMinimum(), 3000L);
    assertEquals(orderSummaryResponse.getMaximum(), 5000L);
    assertEquals(orderSummaryResponse.getOrdersCount(), 2L);
  }
}
