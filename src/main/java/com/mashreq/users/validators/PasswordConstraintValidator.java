package com.mashreq.users.validators;

import com.mashreq.common.I18n;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

/**
 * Custom password validator.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public void initialize(ValidPassword arg0) {
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    PasswordValidator validator =
        new PasswordValidator(
            Arrays.asList(
                // length between 8 and 24 characters
                new LengthRule(8, 24),

                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),

                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // no whitespace
                new WhitespaceRule(),

                // rejects passwords that contain a sequence of >= 4 characters alphabetical  (e.g.
                // abcde)
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 4, false),

                // rejects passwords that contain a sequence of >= 4 characters numerical   (e.g.
                // 1234)
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 4, false)));

    RuleResult result = validator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }
    context.disableDefaultConstraintViolation();
    context
        // .buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result)))
        .buildConstraintViolationWithTemplate(I18n.getMessage("error.password.notComplex"))
        .addConstraintViolation()
        .disableDefaultConstraintViolation();

    return false;
  }
}
