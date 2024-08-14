package com.mashreq.authentication.results;

import com.mashreq.users.User;
import java.util.Map;

public record LoginResult(
    User user,
    Map<String, String> tokens
)  {
}