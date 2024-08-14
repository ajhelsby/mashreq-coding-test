package com.mashreq.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/** Static class to handle Internationalization for the application. */
@Slf4j
public final class I18n {

  private static MessageSource messageSource;
  private static LocalValidatorFactoryBean validator;

  /** Constructor */
  public I18n(MessageSource messageSource, LocalValidatorFactoryBean validator) {
    I18n.messageSource = messageSource;
    I18n.validator = validator;
    log.info("I18n Created");
  }

  /** Gets a message from messages.properties */
  public static String getMessage(String messageKey, Object... args) {

    if (messageSource == null) { // When ApplicationContext unavailable, eg unit test
      return messageKey;
    }

    return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
  }
}
