package com.kienast.ansparen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Not Authorized")
public class NotAuthorizedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String id;

  public NotAuthorizedException(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

}
