package com.test.dog.dogbreeder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason="Breeder service failed to produce new breed")
public class FailToBreedException extends RuntimeException {
}
