package com.mashreq.authentication.payloads;

import com.mashreq.users.validators.OrderOne;
import com.mashreq.users.validators.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * POJO used for user login.
 */
public record LoginPayload(
    @Email(message = "{error.email.invalid}")
    @Size(max = 155, message = "{error.email.maxLength}")
    @NotNull(message = "{error.email.required}")
    String email,

    @NotBlank(message = "{error.password.required}")
    @ValidPassword(groups = OrderOne.class)
    String password
) {
}
