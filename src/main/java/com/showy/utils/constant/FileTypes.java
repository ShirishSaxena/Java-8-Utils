package com.showy.utils.constant;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum FileTypes {
    XLSX("xlsx"),
    XLS("xls"),
    CSV("csv");

    private final String extension;

    FileTypes(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static final Set<String> ALLOWED_EXTENSIONS;

    static {
        Set<String> collect =  Arrays.stream(FileTypes.values()).map(FileTypes::getExtension).collect(Collectors.toSet());
        ALLOWED_EXTENSIONS = Collections.unmodifiableSet(collect);
    }
}