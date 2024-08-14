package com.mashreq.common;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path="/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public abstract class BaseApiV1Controller {
    // Common methods or properties for all controllers
}