package com.showy.utils.exception;

import com.showy.utils.model.CustomFile;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class FileProcessingException extends IOException {

    private String message = "";
    private CustomFile customFile;

    public FileProcessingException(String message) {
        super(message);
        this.message = message;
    }

    public FileProcessingException(String message, CustomFile customFile) {
        super(message);
        this.message = message;
        this.customFile = customFile;
    }

    public FileProcessingException() {
        super();
    }
}