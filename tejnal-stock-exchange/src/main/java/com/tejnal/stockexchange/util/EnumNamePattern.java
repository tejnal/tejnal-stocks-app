package com.tejnal.stockexchange.util;

import com.tejnal.stockexchange.validator.EnumNamePatternValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
  ElementType.METHOD,
  ElementType.FIELD,
  ElementType.ANNOTATION_TYPE,
  ElementType.CONSTRUCTOR,
  ElementType.PARAMETER,
  ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumNamePatternValidator.class)
public @interface EnumNamePattern {
  String regexp();

  String message() default "must match \"{regexp}\"";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
