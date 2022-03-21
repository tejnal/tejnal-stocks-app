package com.tejnal.stockexchange.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tejnal.stockexchange.converter.ToUpperCaseConverter;
import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.util.CurrencyConstraint;
import com.tejnal.stockexchange.util.EnumNamePattern;
import com.tejnal.stockexchange.util.TickerConstraint;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonPropertyOrder({"ticker", "order_side", "volume", "price", "currency"})
public class NewOrderRequest {

  @NotBlank
  @Size(max = 20)
  @JsonProperty("ticker")
  @TickerConstraint
  @JsonDeserialize(converter = ToUpperCaseConverter.class)
  private String ticker;

  @NotNull
  @EnumNamePattern(regexp = "BUY|SELL")
  @JsonProperty("order_side")
  private EOrderSide orderSide;

  @NotNull
  @Min(value = 1)
  @JsonProperty("volume")
  private Long volume;

  @NotNull
  @DecimalMin("0.01")
  @Digits(integer = 10, fraction = 2)
  @JsonProperty("price")
  private Double price;

  @NotBlank
  @Size(max = 30)
  @JsonProperty("currency")
  @CurrencyConstraint
  @JsonDeserialize(converter = ToUpperCaseConverter.class)
  private String currency;

  public NewOrderRequest() {
    super();
  }

  public NewOrderRequest(
      String ticker, EOrderSide orderSide, Long volume, Double price, String currency) {
    super();
    this.ticker = ticker;
    this.orderSide = orderSide;
    this.volume = volume;
    this.price = price;
    this.currency = currency;
  }

  public String getTicker() {
    return ticker;
  }

  public EOrderSide getOrderSide() {
    return orderSide;
  }

  public Long getVolume() {
    return volume;
  }

  public Double getPrice() {
    return price;
  }

  public String getCurrency() {
    return currency;
  }
}
