package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class LogService {
    private final LogDAO logDAO;

    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public List<Log> getLogs() {
        return logDAO.getObjectsByObjectTypeId(BigInteger.valueOf(5), Log.class);
    }

    public List<Log> filtrateByDateLogs(Date... dateFrom) {
        List<Log> logs = new ArrayList<>();
        return logs;
    }

    public List<Log> filtrateByLevelLogs(String... levels) {
        List<Log> logs = new ArrayList<>();
        return logs;
    }

    public Log findById(BigInteger id) {
        return logDAO.getById(id);
    }

    public List<Log> findLogs(String value) {
        return Collections.emptyList();
    }

    public List<Log> sortByDate(Date date) {
        List<Log> logs = new ArrayList<>();
        return logs;
    }

    public List<Log> sortByLevel(String level) {
        List<Log> logs = new ArrayList<>();
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