package com.mashreq.users.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashreq.users.User;
import com.mashreq.users.validators.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * POJO for adding User.
 */
public record UserAddPayload(
    @Email(message = "{error.email.invalid}")
    @Size(max = 155, message = "{error.email.maxLength}")
    @NotNull(message = "{error.email.required}")
    String email,
    @ValidPassword
    @NotBlank(message = "{error.password.required}")
    String password,
    @Size(min = 1, max = 45, message = "{error.firstName.length}")
    @NotNull(message = "{error.firstName.required}")
    String firstName,
    @Size(min = 1, max = 45, message = "{error.lastName.length}")
    @NotNull(message = "{error.lastName.required}")
    String lastName
) {

  public User toEntity() {
    return User.builder()
        .email(email)
        .password(password)
        .firstName(firstName)
        .lastName(lastName)
        .build();
  }
}
