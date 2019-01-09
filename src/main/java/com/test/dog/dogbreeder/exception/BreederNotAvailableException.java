package com.test.dog.dogbreeder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason="Breeder service is not available")
public class BreederNotAvailableException extends RuntimeException {
}