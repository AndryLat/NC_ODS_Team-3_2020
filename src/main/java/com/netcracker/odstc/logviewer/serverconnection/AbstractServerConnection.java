package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.*;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractServerConnection implements ServerConnection {
    protected Server server;
    private final Logger logger = LogManager.getLogger(AbstractServerConnection.class.getName());

    @Override
    public Server getServer() {
        return server;
    }
    public boolean isDirectoryValid(Directory directory){
        Config appConfiguration = Config.getInstance();
        directory.setLastExistenceCheck(new Date());
        return !new Date(directory.getLastAccessByUser().getTime() + appConfiguration.getDirectoryActivityPeriod().getTime()).before(new Date());
    }

    protected AbstractServerConnection(Server server){
        this.server = server;
    }

    protected List<Log> extractLogsFromStream(InputStream inputStream, LogFile logFile){
        List<Log> result = new LinkedList<>();
        assert inputStream != null;
        Scanner sc = new Scanner(inputStream);

        int count = logFile.getLastRow();
        int localCount = 0;
        Log lastLog = null;
        while (sc.hasNextLine()) {
            if(localCount<count){
                sc.nextLine();
            }else {
                String line = sc.nextLine();

                Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d{4} \\d+:\\d+:\\d+\\.\\d+) ([A-Z]+)?.*$");
                Matcher matcher = pattern.matcher(line);

                boolean isLogEntry = matcher.find();
                if(!isLogEntry && lastLog==null){
                    continue;//TODO: Исправить на обнаружение StackTrace
                }else if(!isLogEntry){
                    lastLog.setText(lastLog.getText()+"\n"+line);
                    continue;
                }

                Log log = new Log(line, formatLogLevel(matcher.group(2)), formatDate(matcher.group(1)), logFile);
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
            logger.error(e.getMessage(),e);
        }
        sc.close();
        return result;
    }

    @Override
    public List<Log> call() {
        return getNewLogs();
    }

    private Date formatDate(String date){
        Date logCreationDate = null;
        try {
            logCreationDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS").parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(),e);
        }
        return logCreationDate;
    }
    private LogLevel formatLogLevel(String level){
        if(level==null){
            return null;
        }
        if(LogLevel.contains(level)){
            return LogLevel.valueOf(level);
        }else{
            return null;
        }
    }
}
