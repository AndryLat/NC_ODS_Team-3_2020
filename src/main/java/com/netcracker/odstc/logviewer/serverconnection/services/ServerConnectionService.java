package com.netcracker.odstc.logviewer.serverconnection.services;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.FTPServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.SSHServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ServerConnectionService {
    private static final Logger logger = LogManager.getLogger(ServerConnectionService.class.getName());

    private ServerConnectionService() {
    }

    public static Date formatDate(String date) {
        Date logCreationDate = null;
        try {
            logCreationDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS").parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return logCreationDate;
    }

    public static LogLevel formatLogLevel(String level) {
        if (level == null) {
            return null;
        }
        if (LogLevel.contains(level)) {
            return LogLevel.valueOf(level);
        } else {
            return null;
        }
    }

    public static List<LogFile> getFilesFromRemoteDirectory(Directory directory, String extension) {
        List<LogFile> logFiles = new LinkedList<>();
        ServerConnection serverConnection;
        if (directory.getParentServer().getProtocol().equals(Protocol.FTP)) {
            serverConnection = new FTPServerConnection(directory.getParentServer());
        } else if (directory.getParentServer().getProtocol().equals(Protocol.SSH)) {
            serverConnection = new SSHServerConnection(directory.getParentServer());
        } else {
            return logFiles;
        }
        if (serverConnection.connect() && serverConnection.isDirectoryValid(directory)) {
            logFiles.addAll(serverConnection.getLogFileList(directory, extension));
        }
        return logFiles;
    }
}
