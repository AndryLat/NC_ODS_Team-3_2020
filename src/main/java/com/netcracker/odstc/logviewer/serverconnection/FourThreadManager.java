package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FourThreadManager {//TODO: Требует доработки и тщательного разбора
    private final Logger logger = LogManager.getLogger(FourThreadManager.class.getName());
    private static FourThreadManager instance;
    ExecutorService service = Executors.newFixedThreadPool(4);

    private HashMap<ServerConnection,Future<List<Log>>> callables;

    private FourThreadManager(){
        callables = new HashMap<>();
    }

    public static FourThreadManager getInstance() {
        if(instance==null){
            instance = new FourThreadManager();
        }
        return instance;
    }

    public void executeExtractingLogs(ServerConnection serverConnection){
        callables.put(serverConnection,service.submit(serverConnection));
    }
    public List<Log> getAsyncLogs(){
        List<Log> logs = new LinkedList<>();
        for (Map.Entry<ServerConnection,Future<List<Log>>> future : callables.entrySet()) {
            if (future.getValue().isDone()) {
                try {
                    logs.addAll(future.getValue().get());
                } catch (InterruptedException e) {//TODO: Разобраться с ругательством
                    future.getKey().getServer().setActive(false);
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
