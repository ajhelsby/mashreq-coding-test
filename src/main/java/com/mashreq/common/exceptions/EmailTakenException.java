package com.mashreq.common.exceptions;

import com.mashreq.common.I18n;

/** An exception to be thrown when email already exist for user. */
public class EmailTakenException extends RuntimeException {
  public EmailTakenException() {
    super(I18n.getMessage("error.user.email.exists"));
  }
}
