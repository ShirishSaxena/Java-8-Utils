package com.showy.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationFailedException extends Exception {

    private String message = "";

    public ValidationFailedException(String message) {
        super(message);
        this.message = message;
    }

    public ValidationFailedException() {
        super();
    }
}