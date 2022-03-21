package com.tejnal.stockexchange.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"order_id", "ticker", "order_side", "volume", "price", "currency"})
public class OrderSummaryResponse {

  @JsonProperty("order_date")
  private Date orderDate;

  @JsonProperty("avg")
  private Double average;

  @JsonProperty("min")
  private Double minimum;

  @JsonProperty("max")
  private Double maximum;

  @JsonProperty("orders_count")
  private Long ordersCount;

  public OrderSummaryResponse() {
    super();
  }

  public OrderSummaryResponse(
      Date orderDate, Double average, Double minimum, Double maximum, Long ordersCount) {
    super();
    this.orderDate = orderDate;
    this.average = average;
    this.minimum = minimum;
    this.maximum = maximum;
    this.ordersCount = ordersCount;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public Double getAverage() {
    return average;
  }

  public Double getMinimum() {
    return minimum;
  }

  public Double getMaximum() {
    return maximum;
  }

  public Long getOrdersCount() {
    return ordersCount;
  }
}
