package com.tejnal.stockexchange.validator;

import com.tejnal.stockexchange.service.Impl.OrderBookService;
import com.tejnal.stockexchange.util.CurrencyConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CurrencyConstraint, String> {

  @Autowired private OrderBookService orderBookService;

  @Override
  public void initialize(CurrencyConstraint currencyConstraint) {}

  @Override
  public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
    if (null != contactField && orderBookService.isCurrencyValid(contactField.toUpperCase())) {
      return true;
    }

    return false;
  }

  // Used only for standalone testing
  public void setOrderBookService(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }
}
