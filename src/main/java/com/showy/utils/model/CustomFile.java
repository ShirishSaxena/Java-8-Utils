package com.showy.utils.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.showy.utils.ObjectUtil.notNull;
import static com.showy.utils.ObjectUtil.notNullOrEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomFile {
    private String fileName;
    private byte[] data;
    private String path;

    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    public boolean hasContent() {
        return notNullOrEmpty(fileName) && notNull(data) && data.length > 0;
    }
}
