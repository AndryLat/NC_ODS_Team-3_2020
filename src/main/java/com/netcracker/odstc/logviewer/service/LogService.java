package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogService {
    private final LogDAO logDAO;

    public LogService(@Qualifier("LogDAO") LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public List<Log> getLogs() {
        return logDAO.getObjectsByObjectTypeId(BigInteger.valueOf(5), Log.class);
    }

    public List<Log> filtrateByDateLogs(Date... dateFrom) {
        List<Log> logs = new ArrayList<>();
        for (Date date : dateFrom) {
            for (Log log : logDAO.getLogByCreationDate(date))
                logs.add(log);
        }
        return logs;
    }

    public List<Log> filtrateByLevelLogs(String... levels) {
        List<Log> logs = new ArrayList<>();
        for (String level : levels) {
            for (Log log : logDAO.getLogByLevel(level))
                logs.add(log);
        }
        return logs;
    }

    public List<Log> findLogs(String value) {
        return logDAO.getLogByText(value);
    }

    public List<Log> sortByDate(Date date) {
        List<Log> logs = new ArrayList<>();
        logs.add((Log) logDAO.getLogByCreationDate(date));
        return logs;
    }

    public List<Log> sortByLevel(String level) {
        List<Log> logs = new ArrayList<>();
        logs.add((Log) logDAO.getLogByLevel(level));
        return logs;
    }

    public void save(Log log) {
        if (isLogValid(log)) {
            logDAO.saveObjectAttributesReferences(log);
        }
    }

    public void deleteById(Log log) {
        if (isLogValid(log)) {
            logDAO.deleteById(log.getObjectId());
        }
    }

    private boolean isLogValid(Log log) {
        if (log != null && log.getText() != null) {
            return true;
        }
        return false;
    }
}