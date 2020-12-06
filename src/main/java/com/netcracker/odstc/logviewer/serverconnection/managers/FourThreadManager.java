package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FourThreadManager {//TODO: Требует доработки и тщательного разбора
    private static FourThreadManager instance;
    private final Logger logger = LogManager.getLogger(FourThreadManager.class.getName());
    ExecutorService service = Executors.newFixedThreadPool(4);

    private HashMap<ServerConnection, Future<List<Log>>> callables;

    private FourThreadManager() {
        callables = new HashMap<>();
    }

    public static FourThreadManager getInstance() {
        if (instance == null) {
            instance = new FourThreadManager();
        }
        return instance;
    }

    public void executeExtractingLogs(ServerConnection serverConnection) {
        callables.put(serverConnection, service.submit(serverConnection));
    }

    public List<Log> getAsyncLogs() {
        List<Log> logs = new LinkedList<>();
        for (Map.Entry<ServerConnection, Future<List<Log>>> future : callables.entrySet()) {
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
            }
        }
        callables.clear();
        return logs;
    }
}
