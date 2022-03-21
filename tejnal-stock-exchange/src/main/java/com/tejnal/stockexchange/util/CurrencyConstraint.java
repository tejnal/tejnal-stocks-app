package com.tejnal.stockexchange.util;

import com.tejnal.stockexchange.validator.CurrencyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CurrencyValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrencyConstraint {
  String message() default
      "Currency is invalid. User can only place single currency multiple orders for a day.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
