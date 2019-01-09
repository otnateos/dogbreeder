package com.test.dog.dogbreeder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Dog is not available for deletion")
public class DogNotAvailableForDeletionException extends RuntimeException {
}
