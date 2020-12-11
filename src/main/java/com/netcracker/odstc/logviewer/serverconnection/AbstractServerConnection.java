package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.*;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

public abstract class AbstractServerConnection implements ServerConnection {
    private final Logger logger = LogManager.getLogger(AbstractServerConnection.class.getName());
    protected Server server;
    protected ServerConnectionService serverConnectionService;

    protected AbstractServerConnection(Server server) {
        serverConnectionService = ServerConnectionService.getInstance();
        this.server = server;
    }

    @Override
    public void disconnect() {
        server.setActive(false);
    }

    @Override
    public Server getServer() {
        return server;
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
                if (!isLogEntry && lastLog == null) {
                    continue;
                } else if (!isLogEntry) {
                    lastLog.setText(lastLog.getText() + "\n" + line);
                    continue;
                }

                Log log = new Log(line,
                        serverConnectionService.formatLogLevel(matcher.group(2)),
                        serverConnectionService.formatDate(matcher.group(1)),
                        logFile);

                result.add(log);
                logFile.addLog(log);

                lastLog = log;
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
}
