package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;

abstract class AbstractServerConnection implements ServerConnection {
    protected static final int CONNECT_TIMEOUT = 500;
    protected Server server;
    protected ServerConnectionService serverConnectionService;
    protected List<HierarchyContainer> directories;
    protected boolean isConnected;
    protected Config appConfiguration;

    protected AbstractServerConnection(Server server) {
        isConnected = false;
        serverConnectionService = ServerConnectionService.getInstance();
        this.server = server;
        appConfiguration = Config.getInstance();
    }

    @Override
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public List<HierarchyContainer> getDirectories() {
        return directories;
    }

    @Override
    public void setDirectories(List<HierarchyContainer> directories) {
        this.directories = directories;
    }

    @Override
    public void removeDirectory(Directory directory) {
        for (HierarchyContainer directoryContainer : directories) {
            if (directoryContainer.getOriginal().getObjectId().equals(directory.getObjectId())) {
                directories.remove(directoryContainer);
                return;
            }
        }
    }

    @Override
    public void updateDirectory(Directory directory) {
        for (HierarchyContainer directoryContainer : directories) {
            if (directoryContainer.getOriginal().getObjectId().equals(directory.getObjectId())) {
                directoryContainer.setOriginal(directory);
                return;
            }
        }
    }

    @Override
    public void revalidateDirectories() {
        validateConnection();
        for (HierarchyContainer directoryContainer : directories) {
            Directory directory = (Directory) directoryContainer.getOriginal();
            if (!isDirectoryValid(directory)) {
                directory.setConnectable(false);
            }
        }
    }

    @Override
    public void disconnect() {
        isConnected = false;
    }

    public boolean isDirectoryValid(Directory directory) {
        validateConnection();
        directory.setLastExistenceCheck(new Date());
        return !new Date(directory.getLastAccessByUser().getTime() + appConfiguration.getDirectoryActivityPeriod().getTime()).before(new Date());
    }

    @Override
    public List<Log> getNewLogs() {
        validateConnection();
        List<Log> result = collectNewLogs();
        if (!result.isEmpty()) {
            server.setLastAccessByJob(new Date());
        } else {
            if (new Date(server.getLastAccessByJob().getTime() + appConfiguration.getServerActivityPeriod().getTime()).before(new Date())) {
                server.setEnabled(false);
            }
        }
        return result;
    }

    @Override
    public List<Log> call() {
        return getNewLogs();
    }

    protected List<Log> extractLogsFromStream(InputStream inputStream, LogFile logFile) {
        List<Log> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(inputStream)) {

            int count = logFile.getLastRow();
            int localCount = 0;
            Log lastLog = null;
            while (scanner.hasNextLine()) {
                if (localCount < count) {
                    scanner.nextLine();
                } else {
                    String line = scanner.nextLine();

                    Log log = convertLineToLog(logFile, lastLog, line);
                    if (log == null) continue;

                    lastLog = log;
                    result.add(log);
                    count++;
                }
                localCount++;
            }
            logFile.setLastRow(count);
        }
        return result;
    }

    protected void validateConnection() {
        if (isConnected || connect()) {
            return;
        }
        throw new ServerConnectionException("Cant establish connection with " + server.getIp());
    }

    protected abstract List<Log> collectNewLogs();

    private Log convertLineToLog(LogFile logFile, Log lastLog, String line) {
        Matcher matcher = checkMatcher(lastLog, line);
        if (matcher == null) return null;

        LogLevel logLevel = serverConnectionService.formatLogLevel(matcher.group(2));
        Date creationDate = serverConnectionService.formatDate(matcher.group(1));

        return new Log(line, logLevel, creationDate, logFile.getObjectId());
    }

    private Matcher checkMatcher(Log lastLog, String line) {
        Matcher matcher = serverConnectionService.getLogMatcher(line);

        boolean isLogEntry = matcher != null;
        if (!isLogEntry) {
            if (lastLog != null) {
                lastLog.setText(lastLog.getText() + "\n" + line);
            }
            return null;
        }
        return matcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractServerConnection that = (AbstractServerConnection) o;
        return Objects.equals(server.getObjectId(), that.server.getObjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(server.getObjectId());
    }
}
