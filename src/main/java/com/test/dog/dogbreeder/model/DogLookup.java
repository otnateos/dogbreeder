package com.test.dog.dogbreeder.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Model for lookup result.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DogLookup {
    private String status;
    private String message;

    public boolean success() {
        return "success".equalsIgnoreCase(status) && message != null;
    }
}
