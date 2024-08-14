package com.mashreq.common.exceptions;

/**
 * An implementation of AuthenticationException.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(final String message) {
        super(message);
    }

    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
