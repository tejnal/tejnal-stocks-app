package com.tejnal.stockexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class TejnalStockExchangeApplication {

  public static void main(String[] args) {
    SpringApplication.run(TejnalStockExchangeApplication.class, args);
  }

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
