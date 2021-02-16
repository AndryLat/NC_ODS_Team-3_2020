package com.netcracker.odstc.logviewer.serverconnection.services;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.FTPServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.SSHServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import com.netcracker.odstc.logviewer.serverconnection.services.exceptions.ServerConnectionServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnectionService {
    private static final Pattern[] SEARCH_PATTERNS = new Pattern[]{
            Pattern.compile("(\\d+\\.\\d+\\.\\d{4}\\s\\d+:\\d+:\\d+\\.\\d+)\\s([A-Z]+)?.*$")
    };
    private static ServerConnectionService instance;
    private final Logger logger = LogManager.getLogger(ServerConnectionService.class.getName());

    private ServerConnectionService() {
    }

    public static ServerConnectionService getInstance() {
        if (instance == null) {
            instance = new ServerConnectionService();
        }
        return instance;
    }

    public Date formatDate(String date) {
        Date logCreationDate = null;
        try {
            logCreationDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return logCreationDate;
    }

    public LogLevel formatLogLevel(String level) {
        if (level == null) {
            return LogLevel.NO_LEVEL;
        }
        if (LogLevel.contains(level)) {
            return LogLevel.valueOf(level);
        } else {
            return LogLevel.NO_LEVEL;
        }
    }

    public Matcher getLogMatcher(String line) {
        Matcher matcher;
        for (Pattern pattern :
                SEARCH_PATTERNS) {
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher;
            }
        }
        return null;
    }

    /**
     * @param server Server which will be checked
     * @return true - if server can be accessed<br/>
     * false - if an error occurs
     */
    public boolean isServerAvailable(Server server) {
        ServerConnection serverConnection = wrapServerIntoConnection(server);
        boolean isServerAvailable = false;
        try {
            isServerAvailable = serverConnection.connect();
        } catch (ServerConnectionException e) {
            logger.error("Error with check connection", e);
        }
        if (isServerAvailable) {
            serverConnection.disconnect();
        }
        return isServerAvailable;
    }

    /**
     * @param server    Server where directory located
     * @param directory Folder which will be checked
     * @return true - if directory can be accessed<br/>
     * false - if an error occurs with server or directory
     */
    public boolean isDirectoryAvailable(Server server, Directory directory) {
        ServerConnection serverConnection = wrapServerIntoConnection(server);
        boolean isDirectoryAvailable = false;
        boolean isServerAvailable = false;
        try {
            isServerAvailable = serverConnection.connect();
            if (isServerAvailable) {
                isDirectoryAvailable = serverConnection.isDirectoryValid(directory);
            }
        } catch (ServerConnectionException e) {
            logger.error("Error with check connection", e);
        }
        if (isServerAvailable) {
            serverConnection.disconnect();
        }
        return isDirectoryAvailable;
    }

    /**
     * @param server    Server where directory located
     * @param directory Folder for listing files
     * @return list of files as LogFile
     * @throws ServerConnectionServiceException when list of files cant be received due to server error
     * @see com.netcracker.odstc.logviewer.models.LogFile
     */
    public List<LogFile> getLogFilesFromDirectory(Server server, Directory directory) throws ServerConnectionServiceException {
        ServerConnection serverConnection = wrapServerIntoConnection(server);
        List<LogFile> logFiles;
        try {
            logFiles = serverConnection.getLogFilesFromDirectory(directory);
            serverConnection.disconnect();
        } catch (ServerConnectionException exception) {
            throw new ServerConnectionServiceException("Connection error with " + server.getIp() + ". When getting file list from directory: " + directory.getPath());
        }
        return logFiles;
    }

    public ServerConnection wrapServerIntoConnection(HierarchyContainer serverContainer) {
        Server server = (Server) serverContainer.getOriginal();
        return wrapServerIntoConnection(server);
    }

    public ServerConnection wrapServerIntoConnection(Server server) {
        ServerConnection serverConnection;
        if (server.getProtocol() == Protocol.FTP) {
            serverConnection = new FTPServerConnection(server);
        } else if (server.getProtocol() == Protocol.SSH) {
            serverConnection = new SSHServerConnection(server);
        } else {
            logger.error("Cant wrap server with unknown protocol");
            return null;
        }
        return serverConnection;
    }
}
