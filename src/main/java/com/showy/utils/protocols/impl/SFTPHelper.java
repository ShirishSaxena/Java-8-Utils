package com.showy.utils.protocols.impl;

import com.showy.utils.constant.FileTypes;
import com.showy.utils.model.Credentials;
import com.showy.utils.model.CustomFile;
import com.showy.utils.protocols.FileProtocols;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class SFTPHelper extends FileProtocols {
    public SFTPHelper(String server, int port, String username, String password) {
        super(server, port, username, password);
    }

    public SFTPHelper(Credentials credentials) {
        super(credentials);
    }

    @Override
    public List<CustomFile> downloadFilesWithPrefix(String remoteDirPath, String prefix, boolean checkExtension) {
        SftpSession sftpSession = getConnection();
        if (sftpSession == null || !sftpSession.isOpen()) {
            return null;
        }

        List<CustomFile> filesList = new ArrayList<>();
        Set<String> supportedExtensions = (checkExtension) ? FileTypes.ALLOWED_EXTENSIONS : null;

        try {
            long start = System.currentTimeMillis();
            String[] fileNames = sftpSession.listNames(remoteDirPath);
            for (String fileName : fileNames) {
                if (fileName.startsWith(prefix) && isValidExtension(fileName, supportedExtensions)) {
                    InputStream inputStream = sftpSession.readRaw(remoteDirPath + "/" + fileName);
                    byte[] byteArray = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                    filesList.add(new CustomFile(fileName, byteArray, remoteDirPath));
                }
            }

            double timeElapsed = (double) (System.currentTimeMillis() - start) / 1000;
            log.debug("| {} |  download time : {} | file count : {} | path : {}", prefix, String.format("%.2f", timeElapsed),
                    filesList.size(), remoteDirPath);
        } catch (Exception e) {
            log.error("Error in (SFTP) downloadFilesWithPrefix(): ", e);
            filesList = null;
        }
        sftpSession.close();
        return filesList;
    }

    @Override
    public boolean moveFile(String fileName, String currentPath, String movePath) {
        SftpSession conn = getConnection();
        if (conn == null) {
            return false;
        }

        boolean moveResult = false;
        try {
            String source = cleanDirPath(currentPath) + "/" + fileName;
            String destination = cleanDirPath(movePath) + "/" + fileName;

            conn.rename(source, destination);
            moveResult = true;
            log.debug("{} | source {} | destination {} | moved {}", fileName, source, destination, moveResult);
        } catch (Exception e) {
            log.error("Exception in (FTP) moveFileHelper() : ", e);
        }

        if (!moveResult) {
            String paths = String.format("<br>Read Path : %s<br>Move Path : %s", currentPath, movePath);
            log.error(fileName + " | file move error -> " + paths);
        }
        conn.close();
        return moveResult;
    }

    @Override
    public boolean uploadFile(CustomFile customFile) {
        boolean result = false;
        SftpSession sftpSession = getConnection();
        if (sftpSession != null && sftpSession.isOpen()) {
            try (InputStream inputStream = new ByteArrayInputStream(customFile.getData())) {
                sftpSession.write(inputStream, customFile.getPath() + "/" + customFile.getFileName());
                sftpSession.close();
                result = true;
                log.warn("File uploaded: " + customFile.getFileName());
            } catch (IOException e) {
                log.error("Error in (SFTP) uploadFile() : ", e);
                result = false;
            }
            sftpSession.close();
        }

        return result;
    }

    private SftpSession getConnection() {
        SftpSession sftpSession = null;
        try {
            DefaultSftpSessionFactory sftpSessionFactory = new DefaultSftpSessionFactory();
            sftpSessionFactory.setHost(server);
            sftpSessionFactory.setPort(port);
            sftpSessionFactory.setUser(username);
            sftpSessionFactory.setAllowUnknownKeys(true);
            if (StringUtils.isBlank(credentials.getKey())) {
                sftpSessionFactory.setPassword(password);
            } else {
                sftpSessionFactory.setPrivateKey(new ByteArrayResource(credentials.getKey().getBytes()));
            }
            sftpSession = sftpSessionFactory.getSession();
        } catch (Exception e) {
            log.error("Error in (SFTP) getConnection(): ", e);
        }
        return sftpSession;
    }
}
