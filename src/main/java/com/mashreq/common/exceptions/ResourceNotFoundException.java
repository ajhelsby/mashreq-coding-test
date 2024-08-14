package com.mashreq.common.exceptions;

/** Exception thrown when resource can not be found in the system. */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {}

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
