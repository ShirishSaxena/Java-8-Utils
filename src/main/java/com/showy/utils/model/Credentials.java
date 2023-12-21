package com.showy.utils.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Credentials {
    public enum CredentialsTypes {
        FTP,
        SFTP,
        WEB
    }

    private String server;
    private int port;
    private String username;
    private String password;

    private String key;
    private CredentialsTypes type;
}
