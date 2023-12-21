package com.showy.utils.protocols.impl;

import com.showy.utils.constant.FileTypes;
import com.showy.utils.model.Credentials;
import com.showy.utils.model.CustomFile;
import com.showy.utils.protocols.FileProtocols;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class FTPHelper extends FileProtocols {
    public FTPHelper(String server, int port, String username, String password) {
        super(server, port, username, password);
    }

    public FTPHelper(Credentials credentials) {
        super(credentials);
    }

    @Override
    public List<CustomFile> downloadFilesWithPrefix(String remoteDirPath, String prefix, boolean checkExtension) {
        List<CustomFile> customFileList = new ArrayList<>();
        FTPClient conn = getConnection();
        try {
            if (conn == null || !conn.isConnected()) {
                return null;
            }

            Set<String> supportedExtensions = (checkExtension) ? FileTypes.ALLOWED_EXTENSIONS : null;
            long start = System.currentTimeMillis();
            remoteDirPath = cleanDirPath(remoteDirPath);
            conn.changeWorkingDirectory(remoteDirPath);
            FTPFile[] ftpFiles = conn.listFiles();

            for (FTPFile file : ftpFiles) {
                if (file.getName().startsWith(prefix) && file.isFile() && isValidExtension(file.getName(), supportedExtensions)) {
                    InputStream inputStream = conn.retrieveFileStream(file.getName());
                    byte[] byteArray = IOUtils.toByteArray(inputStream);
                    conn.completePendingCommand();
                    inputStream.close();
                    customFileList.add(new CustomFile(file.getName(), byteArray, remoteDirPath));
                }
            }

            double timeElapsed = (double) (System.currentTimeMillis() - start) / 1000;
            log.debug("| {} |  download time : {} | file count : {} | path : {}", prefix, String.format("%.2f", timeElapsed),
                    customFileList.size(), remoteDirPath);
        } catch (Exception e) {
            log.error("Error in (FTP) downloadFilesWithPrefix() : ", e);
            customFileList = null;
        }
        closeConnection(conn);
        return customFileList;
    }

    @Override
    public boolean moveFile(String fileName, String currentPath, String movePath) {
        FTPClient conn = getConnection();
        if (conn == null) {
            return false;
        }

        boolean moveResult = false;
        try {
            String source = cleanDirPath(currentPath) + "/" + fileName;
            String destination = cleanDirPath(movePath) + "/" + fileName;

            moveResult = conn.rename(source, destination);
            log.debug("{} | source {} | destination {} | moved {}", fileName, source, destination, moveResult);
        } catch (Exception e) {
            log.error("Exception in (FTP) moveFileHelper() : ", e);
        }

        if (!moveResult) {
            String paths = String.format("<br>Read Path : %s<br>Move Path : %s", currentPath, movePath);
            log.error(fileName + " | " + "File move error : " + paths);
        }
        closeConnection(conn);
        return moveResult;
    }

    @Override
    public boolean uploadFile(CustomFile customFile) {
        boolean result;
        FTPClient conn = getConnection();
        if (conn == null) {
            return false;
        }

        try {
            conn.changeWorkingDirectory(customFile.getPath());
            conn.storeFile(customFile.getFileName(), new ByteArrayInputStream(customFile.getData()));

            result = true;
            log.info("File uploaded : " + customFile.getFileName());
        } catch (Exception e) {
            log.error("Error in (FTP) uploadFile():", e);
            result = false;
        }
        closeConnection(conn);
        return result;
    }

    private FTPClient getConnection() {
        int replyCode = 0;
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.enterLocalPassiveMode(); // defaults to activeMode, which results in errors on some systems.
            ftpClient.login(username, password);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                closeConnection(ftpClient);
                replyCode = ftpClient.getReplyCode();
                throw new FTPConnectionClosedException("FTP server refused connection: " + ftpClient.getReplyCode());
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            replyCode = ftpClient.getReplyCode();
            return ftpClient;
        } catch (Exception e) {
            log.error("Error in (FTP [{}]) getConnection() : ", replyCode, e);
            return null;
        }
    }

    private void closeConnection(FTPClient ftpClient) {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            log.error("Error in closeConnection() : ", e);
        }
    }
}
