package com.tejnal.stockexchange.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tejnal.stockexchange.data.enums.EOrderSide;

import java.util.Date;

@JsonPropertyOrder({"order_id", "ticker", "order_side", "volume", "price", "currency"})
public class OrderResponse {

  @JsonProperty("order_id")
  private Long orderId;

  @JsonProperty("ticker")
  private String ticker;

  @JsonProperty("order_side")
  private EOrderSide orderSide;

  @JsonProperty("volume")
  private Long volume;

  @JsonProperty("price")
  private Double price;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("order_date")
  private Date orderDate;

  public OrderResponse() {
    super();
  }

  public OrderResponse(
      Long orderId,
      String ticker,
      EOrderSide orderSide,
      Long volume,
      Double price,
      String currency,
      Date orderDate) {
    super();
    this.orderId = orderId;
    this.ticker = ticker;
    this.orderSide = orderSide;
    this.volume = volume;
    this.price = price;
    this.currency = currency;
    this.orderDate = orderDate;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public EOrderSide getOrderSide() {
    return orderSide;
  }

  public void setOrderSide(EOrderSide orderSide) {
    this.orderSide = orderSide;
  }

  public Long getVolume() {
    return volume;
  }

  public void setVolume(Long volume) {
    this.volume = volume;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }
}
