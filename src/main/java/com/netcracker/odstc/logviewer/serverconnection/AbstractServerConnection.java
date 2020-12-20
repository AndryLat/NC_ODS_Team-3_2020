package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;

public abstract class AbstractServerConnection implements ServerConnection {
    private final Logger logger = LogManager.getLogger(AbstractServerConnection.class.getName());
    protected Server server;
    protected ServerConnectionService serverConnectionService;
    protected List<HierarchyContainer> directories;
    protected boolean isConnected;

    protected AbstractServerConnection(Server server) {
        isConnected = false;
        serverConnectionService = ServerConnectionService.getInstance();
        this.server = server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public void removeDirectory(Directory directory) {
        for (HierarchyContainer directoryContainer :
                directories) {
            if(directoryContainer.getOriginal().getObjectId().equals(directory.getObjectId())){
                directories.remove(directoryContainer);
                return;
            }
        }
    }

    @Override
    public void updateDirectory(Directory directory) {
        for (HierarchyContainer directoryContainer :
                directories) {
            if(directoryContainer.getOriginal().getObjectId().equals(directory.getObjectId())){
                directoryContainer.setOriginal(directory);
                return;
            }
        }
    }

    @Override
    public List<Log> getNewLogs() {
        server.setLastAccessByJob(new Date());
        validateConnection();
        return collectNewLogs();
    }

    protected abstract void validateConnection();
    protected abstract List<Log> collectNewLogs();

    @Override
    public void disconnect() {
        server.setActive(false);
        isConnected = false;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public List<HierarchyContainer> getDirectories() {
        return directories;
    }

    @Override
    public void setDirectories(List<HierarchyContainer> directories) {
        this.directories = directories;
    }

    public boolean isDirectoryValid(Directory directory) {
        Config appConfiguration = Config.getInstance();
        directory.setLastExistenceCheck(new Date());
        return !new Date(directory.getLastAccessByUser().getTime() + appConfiguration.getDirectoryActivityPeriod().getTime()).before(new Date());
    }

    @Override
    public List<Log> call() {
        return getNewLogs();
    }

    protected List<Log> extractLogsFromStream(InputStream inputStream, LogFile logFile) {
        List<Log> result = new ArrayList<>();
        Scanner scanner = new Scanner(inputStream);

        int count = logFile.getLastRow();
        int localCount = 0;
        Log lastLog = null;
        while (scanner.hasNextLine()) {
            if (localCount < count) {
                scanner.nextLine();
            } else {
                String line = scanner.nextLine();

                Matcher matcher = serverConnectionService.getLogMatcher(line);//TODO: Запоминать если логер подошел

                boolean isLogEntry = matcher != null;
                if (!isLogEntry) {
                    if (lastLog != null) {
                        lastLog.setText(lastLog.getText() + "\n" + line);
                    }
                    continue;
                }

                Log log = new Log(line,
                        serverConnectionService.formatLogLevel(matcher.group(2)),
                        serverConnectionService.formatDate(matcher.group(1)),
                        logFile.getObjectId());


                lastLog = log;
                result.add(log);
                count++;
            }
            localCount++;
        }
        logFile.setLastRow(count);
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        scanner.close();
        return result;
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
