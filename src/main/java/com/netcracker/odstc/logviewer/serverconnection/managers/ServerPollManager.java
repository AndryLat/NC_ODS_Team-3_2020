package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerPollManager {
    private static ServerPollManager instance;
    private final Logger logger = LogManager.getLogger(ServerPollManager.class.getName());
    ExecutorService service = Executors.newFixedThreadPool(4);
    
    private HashMap<ServerConnection, Future<List<Log>>> serverConnectionsResults;

    private ServerPollManager() {
        serverConnectionsResults = new HashMap<>();
    }

    public static ServerPollManager getInstance() {
        if (instance == null) {
            instance = new ServerPollManager();
        }
        return instance;
    }

    public void executeExtractingLogs(ServerConnection serverConnection) {
        serverConnectionsResults.put(serverConnection, service.submit(serverConnection));
    }

    public List<Log> getLogsFromThreads() {
        List<Log> logs = new ArrayList<>();
        Iterator<Map.Entry<ServerConnection, Future<List<Log>>>> resultIterator = serverConnectionsResults.entrySet().iterator();
        while (resultIterator.hasNext()) {
            Map.Entry<ServerConnection, Future<List<Log>>> future = resultIterator.next();
            if (future.getValue().isDone()) {
                try {
                    logs.addAll(future.getValue().get());
                } catch (InterruptedException e) {
                    future.getKey().getServer().setActive(false);
                    Thread.currentThread().interrupt();// Так точно правильно?
                    logger.error("Thread is interrupted ", e);
                } catch (ExecutionException e) {
                    future.getKey().getServer().setActive(false);
                    logger.error("Thread execution error ", e);
                }
                resultIterator.remove();
            }
        }
        return logs;
    }
}
