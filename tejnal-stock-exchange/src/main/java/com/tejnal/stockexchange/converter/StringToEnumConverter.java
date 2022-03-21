package com.tejnal.stockexchange.converter;

import com.tejnal.stockexchange.data.enums.EOrderSide;
import com.tejnal.stockexchange.security.exception.CustomInputException;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, EOrderSide> {

  @Override
  public EOrderSide convert(String source) {
    try {
      return EOrderSide.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomInputException(
          "REQUEST PARAM VALIDATION FAILED!",
          "Invalid parameter value passed: '" + source + "' | Accepted values - [SELL|BUY]");
    }
  }
}
