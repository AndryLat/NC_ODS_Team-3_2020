package com.netcracker.odstc.logviewer.serverconnection.services;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.FTPServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.SSHServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnectionService {
    private static final String[] SEARCH_PATTERNS = new String[]{
            "(\\d+\\.\\d+\\.\\d{4}\\s\\d+:\\d+:\\d+\\.\\d+)\\s([A-Z]+)?.*$"//TODO: Add second pattern
    };
    private static ServerConnectionService instance;
    private final Logger logger = LogManager.getLogger(ServerConnectionService.class.getName());
    protected List<Pattern> logSearchPatterns; // For various patterns

    private ServerConnectionService() {
        logSearchPatterns = new ArrayList<>();
        for (String pattern : SEARCH_PATTERNS) {
            logSearchPatterns.add(Pattern.compile(pattern));
        }
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
            logCreationDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS").parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return logCreationDate;
    }

    public LogLevel formatLogLevel(String level) {
        if (level == null) {
            return null;
        }
        if (LogLevel.contains(level)) {
            return LogLevel.valueOf(level);
        } else {
            return null;
        }
    }

    public Matcher getLogMatcher(String line) {
        Matcher matcher;
        for (Pattern pattern :
                logSearchPatterns) {
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher;
            }
        }
        return null;
    }

    public boolean isServerAvailable(Server server) {
        ServerConnection serverConnection = wrapServerIntoConnection(server);
        boolean isServerAvailable = serverConnection.connect();
        if (isServerAvailable) {
            serverConnection.disconnect();
        }
        return isServerAvailable;
    }

    public boolean isDirectoryAvailable(Server server, Directory directory) {
        ServerConnection serverConnection = wrapServerIntoConnection(server);
        boolean isDirectoryAvailable = serverConnection.isDirectoryValid(directory);
        serverConnection.disconnect();
        return isDirectoryAvailable;
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
