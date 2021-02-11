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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;

abstract class AbstractServerConnection implements ServerConnection {
    protected static final int CONNECT_TIMEOUT = 500;
    private static final int MAX_LOG_LENGTH = 4000;
    private static final String TEXT_OVERFLOW_MESSAGE = "[LOG IS TOO LARGE. CONTENT CLIPPED.]";
    private static final Logger logger = LogManager.getLogger(AbstractServerConnection.class.getName());
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
        boolean isValid = isLastAccessByUserValid(directory);
        if (logger.isWarnEnabled() && !isValid) {
            logger.warn("Directory {} from {} marked as invalid due to inactivity", directory.getPath(), server.getIp());
        }
        return isValid;
    }

    private boolean isLastAccessByUserValid(Directory directory) {
        Date minimalDate = new Date(new Date().getTime()-appConfiguration.getDirectoryActivityPeriod().getTime());
        return directory.getLastAccessByUser().after(minimalDate);
    }

    @Override
    public List<Log> getNewLogs() {
        validateConnection();
        List<Log> result = collectNewLogs();
        if (!result.isEmpty()) {
            server.setLastAccessByJob(new Date());
        } else {
            if (new Date(server.getLastAccessByJob().getTime() + appConfiguration.getServerActivityPeriod().getTime()).before(new Date())) {
                logger.info("Server {} exceeds allowed Server activity period. Disabling.", server.getIp());
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

                    Log log = convertLineToLogOrAppendToLast(logFile, lastLog, line);
                    if (log != null) {
                        if (lastLog != null && lastLog.getText().length() > MAX_LOG_LENGTH) {
                            lastLog.setText(lastLog.getText().substring(0, MAX_LOG_LENGTH - TEXT_OVERFLOW_MESSAGE.length()) + TEXT_OVERFLOW_MESSAGE);
                        }
                        lastLog = log;
                        result.add(log);
                    }
                    count++;
                }
                localCount++;
            }
            logFile.setLastRow(count);
        }

        //Last log will not trigger changing lastLog, so we need to check this manually.
        if (result.size() > 1) {
            Log lastResultedLog = result.get(result.size() - 1);
            if (lastResultedLog.getText().length() > MAX_LOG_LENGTH) {
                lastResultedLog.setText(lastResultedLog.getText().substring(0, MAX_LOG_LENGTH - TEXT_OVERFLOW_MESSAGE.length()) + TEXT_OVERFLOW_MESSAGE);
            }
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

    protected void validateDirectoryByLogCollectionResult(List<Log> collectedLogs, Directory directory) {
        if (!collectedLogs.isEmpty()) {
            directory.setLastExistenceCheck(new Date());
        } else {
            if (new Date(directory.getLastExistenceCheck().getTime() + appConfiguration.getDirectoryActivityPeriod().getTime()).before(new Date())) {
                logger.warn("Directory {} from {} exceeds directory activity period. Marking as unavailable", directory.getPath(), server.getIp());
                directory.setEnabled(false);
            }
        }
    }

    private Log convertLineToLogOrAppendToLast(LogFile logFile, Log lastLog, String line) {
        Matcher matcher = serverConnectionService.getLogMatcher(line);

        boolean isLogEntry = matcher != null;
        if (!isLogEntry) {
            if (lastLog != null) {
                lastLog.setText(lastLog.getText() + "\n" + line);
            }
            return null;
        }

        LogLevel logLevel = serverConnectionService.formatLogLevel(matcher.group(2));
        Date creationDate = serverConnectionService.formatDate(matcher.group(1));

        return new Log(line, logLevel, creationDate, logFile.getObjectId());
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
