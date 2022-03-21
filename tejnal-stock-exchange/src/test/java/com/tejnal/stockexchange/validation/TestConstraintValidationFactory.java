package com.tejnal.stockexchange.validation;

import com.tejnal.stockexchange.service.Impl.OrderBookService;
import com.tejnal.stockexchange.validator.CurrencyValidator;
import com.tejnal.stockexchange.validator.TickerValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintValidator;

public class TestConstraintValidationFactory extends SpringWebConstraintValidatorFactory {

  private final Log logger = LogFactory.getLog(getClass());

  private final WebApplicationContext wac;

  private OrderBookService service;

  public TestConstraintValidationFactory(WebApplicationContext wac, OrderBookService service) {
    this.wac = wac;
    this.service = service;
  }

  @Override
  public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
    logger.info("key is : " + key);
    ConstraintValidator instance = super.getInstance(key);

    if (instance instanceof TickerValidator) {
      TickerValidator tickerValidator = (TickerValidator) instance;
      tickerValidator.setOrderBookService(service);
      instance = tickerValidator;
    } else if (instance instanceof CurrencyValidator) {
      CurrencyValidator currencyValidator = (CurrencyValidator) instance;
      currencyValidator.setOrderBookService(service);
      instance = currencyValidator;
    }
    return (T) instance;
  }

  @Override
  protected WebApplicationContext getWebApplicationContext() {
    return wac;
  }
}
