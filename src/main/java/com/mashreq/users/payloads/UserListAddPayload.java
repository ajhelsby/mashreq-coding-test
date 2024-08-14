package com.mashreq.users.payloads;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UserListAddPayload(
    @NotEmpty(message = "{error.field.required}")
    List<@Valid UserAddPayload> users
) {

}
