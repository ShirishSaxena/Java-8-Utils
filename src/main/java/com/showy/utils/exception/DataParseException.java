package com.showy.utils.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class DataParseException extends IOException {
    private String message = "";

    public DataParseException(String message) {
        super(message);
        this.message = message;
    }

    public DataParseException() {
        super();
    }
}
