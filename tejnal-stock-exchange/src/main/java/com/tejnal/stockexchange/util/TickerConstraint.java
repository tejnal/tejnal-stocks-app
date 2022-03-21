package com.tejnal.stockexchange.util;

import com.tejnal.stockexchange.validator.TickerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TickerValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TickerConstraint {
  String message() default "Input ticker is invalid.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
