package com.tejnal.stockexchange.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

public class ToUpperCaseConverter extends StdConverter<String, String> {

  @Override
  public String convert(String value) {
    if (null != value) return value.toUpperCase();
    return value;
  }
}
