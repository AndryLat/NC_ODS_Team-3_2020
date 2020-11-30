package com.netcracker.odstc.logviewer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogClass {
    private static LogClass logClass;
    private Logger logger;

    private LogClass() {
        logger = LogManager.getLogger(LogClass.class.getName());
    }

    private static LogClass getInstance(){
        if(logClass == null){
            logClass = new LogClass();
        }
        return logClass;
    }

    public static void log(Level level, String message){
        getInstance().logger.log(level, message);
    }
}
