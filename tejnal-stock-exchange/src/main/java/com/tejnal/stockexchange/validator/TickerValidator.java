package com.tejnal.stockexchange.validator;

import com.tejnal.stockexchange.service.Impl.OrderBookService;
import com.tejnal.stockexchange.util.TickerConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TickerValidator implements ConstraintValidator<TickerConstraint, String> {

  @Autowired private OrderBookService orderBookService;

  @Override
  public void initialize(TickerConstraint tickerConstraint) {}

  @Override
  public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
    if (null != contactField && orderBookService.isTickerValid(contactField.toUpperCase())) {
      return true;
    }

    return false;
  }

  // Used only for Stand-alone testing
  public void setOrderBookService(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }
}
