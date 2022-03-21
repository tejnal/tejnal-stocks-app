package com.tejnal.stockexchange.data.entity;

import com.tejnal.stockexchange.data.enums.EOrderSide;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "ORDERS")
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String ticker;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_side")
  private EOrderSide orderSide;

  private Long volume;
  private Double price;
  private String currency;

  @Column(name = "crtd_dt")
  private Date createdDate;

  @Column(name = "crtd_by")
  private Long createdBy;

  public Orders() {}

  public Orders(String ticker, EOrderSide orderSide, Long volume, Double price, String currency) {
    super();
    this.ticker = ticker;
    this.orderSide = orderSide;
    this.volume = volume;
    this.price = price;
    this.currency = currency;
  }

  public Long getId() {
    return id;
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

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }
}
