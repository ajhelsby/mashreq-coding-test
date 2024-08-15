package com.mashreq.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashreq.users.User;
import com.mashreq.users.validators.OrderOne;
import com.mashreq.users.validators.OrderTwo;
import com.mashreq.users.validators.PasswordValueMatch;
import com.mashreq.users.validators.ValidPassword;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * POJO used for user registration.
 */
@PasswordValueMatch.List({
    @PasswordValueMatch(
        field = "password",
        fieldMatch = "confirmPassword",
        message = "{error.passwords.dontMatch}",
        groups = OrderTwo.class)
})
@GroupSequence({SignupPayload.class, OrderOne.class, OrderTwo.class})
public record SignupPayload(
    @Email(message = "{error.email.invalid}")
    @Size(max = 155, message = "{error.email.maxLength}")
    @NotNull(message = "{error.email.required}")
    String email,

    @NotBlank(message = "{error.password.required}")
    @ValidPassword(groups = OrderOne.class)
    String password,

    @NotBlank(message = "{error.confirmPassword.required}")
    @ValidPassword(groups = OrderOne.class)
    String confirmPassword,

    @Size(min = 1, max = 45, message = "{error.firstName.length}")
    @NotNull(message = "{error.firstName.required}")
    String firstName,

    @Size(min = 1, max = 45, message = "{error.lastName.length}")
    @NotNull(message = "{error.lastName.required}")
    String lastName
) {

  /**
   * Converts payload to user entity.
   *
   * @return a User
   */
  public User toEntity() {
    return User.builder()
               .email(this.email)
               .password(this.password)
               .firstName(this.firstName)
               .lastName(this.lastName)
               .build();
  }
}
