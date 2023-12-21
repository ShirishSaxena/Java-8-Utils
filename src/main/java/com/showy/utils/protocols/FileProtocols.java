package com.showy.utils.protocols;

import com.showy.utils.ObjectUtil;
import com.showy.utils.model.Credentials;
import com.showy.utils.model.CustomFile;
import com.showy.utils.protocols.impl.FTPHelper;
import com.showy.utils.protocols.impl.SFTPHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public abstract class FileProtocols {

    protected final String server;
    protected final int port;
    protected final String username;
    protected final String password;
    protected Credentials credentials;

    public FileProtocols(Credentials credentials) {
        this(credentials.getServer(), credentials.getPort(), credentials.getUsername(), credentials.getPassword());
        this.credentials = credentials;
    }

    public abstract List<CustomFile> downloadFilesWithPrefix(String remoteDirPath, String prefix, boolean checkExtension);

    public abstract boolean moveFile(String fileName, String currentPath, String movePath);

    public abstract boolean uploadFile(CustomFile customFTPFile);

    protected boolean isValidExtension(String fileName, Set<String> extensions) {
        if (ObjectUtil.isNullOrEmpty(extensions)) {
            // all extensions are allowed if set is null.
            return true;
        }

        String extension = FilenameUtils.getExtension(fileName);
        return StringUtils.isNotBlank(extension) && extensions.contains(extension.toLowerCase());
    }

    protected String cleanDirPath(String dirPath) {
        if (dirPath.endsWith("/") || dirPath.endsWith("\\")) {
            return dirPath.substring(0, dirPath.length() - 1);
        }
        return dirPath;
    }

    public static FileProtocols getProtocol(Credentials credentials) {
        if (credentials.getType() == Credentials.CredentialsTypes.SFTP) {
            return new SFTPHelper(credentials);
        } else {
            // falling back to ftp in case nothing is defined.
            return new FTPHelper(credentials);
        }
    }
}
