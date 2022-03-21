package com.tejnal.stockexchange.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TICKER_MAP")
public class TickerMap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ticker_cd")
  private String tickerCode;

  @Column(name = "ticker_desc")
  private String tickerDescription;

  public TickerMap() {
    super();
  }

  public TickerMap(String tickerCode, String tickerDescription) {
    super();
    this.tickerCode = tickerCode;
    this.tickerDescription = tickerDescription;
  }

  public Long getId() {
    return id;
  }

  public String getTickerCode() {
    return tickerCode;
  }

  public String getTickerDescription() {
    return tickerDescription;
  }

  @Override
  public String toString() {
    return "TickerMap{"
        + "id="
        + id
        + ", tickerCode='"
        + tickerCode
        + '\''
        + ", tickerDescription='"
        + tickerDescription
        + '\''
        + '}';
  }
}
