package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FTPServerConnection extends AbstractServerConnection {
    private final Logger logger = LogManager.getLogger(FTPServerConnection.class.getName());
    private final FTPClient ftpClient;

    public FTPServerConnection(Server server) {
        super(server);
        ftpClient = new FTPClient();
    }

    @Override
    public List<LogFile> getLogFilesFromDirectory(Directory directory) {
        validateConnection();
        List<LogFile> logFiles = new ArrayList<>();
        try {
            for (FTPFile ftpFile : ftpClient.listFiles(directory.getPath())) {
                if (ftpFile.isFile()) {
                    LogFile logFile = new LogFile(ftpFile.getName(), 0, directory.getObjectId());
                    logFiles.add(logFile);
                }
            }
        } catch (IOException e) {
            logger.error("Exception when trying get list of files from {} at {}", directory.getPath(), server.getIp(), e);
            throw new ServerConnectionException("Can't list files from FTP due to error", e);
        }
        return logFiles;
    }

    @Override
    public boolean connect() {
        logger.debug("Making connection to {}", server.getIp());
        try {
            ftpClient.setConnectTimeout(CONNECT_TIMEOUT);
            ftpClient.setDataTimeout(CONNECT_TIMEOUT);
            ftpClient.connect(server.getIp(), server.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("Error with connect into {}", server.getIp());
                server.setConnectable(false);
            }
            server.setConnectable(ftpClient.login(server.getLogin(), server.getPassword()));
        } catch (IOException e) {
            server.setConnectable(false);
            logger.error("Error with connect into {}", server.getIp(), e);
        }
        isConnected = server.isConnectable();
        return server.isConnectable();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.error("Error with disconnect {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isDirectoryValid(Directory directory) {
        if (!super.isDirectoryValid(directory))
            return false;
        try {
            boolean isAccessible = ftpClient.changeWorkingDirectory(directory.getPath());
            if (isAccessible) {
                ftpClient.changeToParentDirectory();
            }
            return isAccessible;
        } catch (IOException e) {
            logger.error("Error with connection to {} during directory checking", server.getIp());
            server.setConnectable(false);
            return false;
        }
    }

    protected List<Log> collectNewLogs() {
        List<Log> result = new ArrayList<>();
        if (directories.isEmpty()) {
            return result;
        }
        for (int i = 0; i < directories.size(); i++) {
            HierarchyContainer directory = directories.get(i);
            result.addAll(extractLogsFromDirectory(directory));
        }
        return result;
    }

    private List<Log> extractLogsFromDirectory(HierarchyContainer directoryContainer) {
        List<Log> result = new ArrayList<>();
        Directory directory = (Directory) directoryContainer.getOriginal();
        try {
            if (!ftpClient.changeWorkingDirectory(directory.getPath())) {
                logger.error("Can't change working directory to {} on {}", directory.getPath(), server.getIp());
                directory.setConnectable(false);
                return result;
            }
            for (int i = 0; i < directoryContainer.getChildren().size(); i++) {
                LogFile logFile = (LogFile) directoryContainer.getChildren().get(i).getOriginal();
                result.addAll(extractLogsFromFile(logFile));
            }
            ftpClient.changeWorkingDirectory("/");
        } catch (IOException e) {
            logger.error("Marking directory {} from {} as unavailable", directory.getPath(), server.getIp(), e);
            directory.setConnectable(false);
        }
        validateDirectoryByLogCollectionResult(result, directory);
        return result;
    }

    private List<Log> extractLogsFromFile(LogFile logFile) {
        logFile.setLastUpdate(new Date());
        List<Log> result = new ArrayList<>();
        try {
            try (InputStream inputStream = ftpClient.retrieveFileStream(logFile.getFileName())) {
                if (inputStream == null) {
                    logger.error("Can't reach file {} from {}", logFile.getFileName(), server.getIp());
                } else {
                    result.addAll(extractLogsFromStream(inputStream, logFile));
                    ftpClient.completePendingCommand();
                }
            }
        } catch (IOException e) {
            logger.error("Error with read file {} from {}", logFile.getFileName(), server.getIp(), e);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
