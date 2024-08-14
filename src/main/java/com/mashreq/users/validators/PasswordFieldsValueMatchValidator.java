package com.mashreq.users.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Custom passwords validator.
 */
public class PasswordFieldsValueMatchValidator
    implements ConstraintValidator<PasswordValueMatch, Object> {

  private String field;
  private String fieldMatch;
  private String message;

  /**
   * Initialize password validator.
   *
   * @param constraintAnnotation a constrain
   */
  public void initialize(PasswordValueMatch constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.fieldMatch = constraintAnnotation.fieldMatch();
    this.message = constraintAnnotation.message();
  }

  /**
   * Check if given value is valid.
   *
   * @param value   a value to validate
   * @param context a context
   * @return true if given value passes checks, false otherwise
   */
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
    Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

    boolean isValid = false;
    if (fieldValue != null) {
      isValid = fieldValue.equals(fieldMatchValue);
    }

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(message)
          .addPropertyNode(field)
          .addConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(message)
          .addPropertyNode(fieldMatch)
          .addConstraintViolation();
    }

    return isValid;
  }
}
