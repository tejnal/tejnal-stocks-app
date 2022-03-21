package com.tejnal.stockexchange.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.util.EnumNamePattern;
import com.tejnal.stockexchange.util.TickerConstraint;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@JsonPropertyOrder({"ticker", "order_side", "orderDate"})
public class OrderSummaryRequest {

  @NotBlank
  @Size(max = 20)
  @JsonProperty("ticker")
  @TickerConstraint
  private String ticker;

  @NotNull
  @EnumNamePattern(regexp = "BUY|SELL")
  @JsonProperty("order_side")
  private EOrderSide orderSide;

  @NotNull
  @JsonProperty("order_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private Date orderDate;

  public OrderSummaryRequest() {
    super();
  }

  public OrderSummaryRequest(String ticker, EOrderSide orderSide, Date orderDate) {
    super();
    this.ticker = ticker;
    this.orderSide = orderSide;
    this.orderDate = orderDate;
  }

  public String getTicker() {
    return ticker;
  }

  public EOrderSide getOrderSide() {
    return orderSide;
  }

  public Date getOrderDate() {
    return orderDate;
  }
}
