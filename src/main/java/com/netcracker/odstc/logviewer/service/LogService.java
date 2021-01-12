package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    public Log findById(BigInteger id) {
        return logDAO.getObjectById(id, Log.class);
    }

    public List<Log> getAllLogs() {
        return logDAO.getAll();
    }

    public List<Log> getAllLogsByAllValues(String text, Date dat1, Date dat2, int V_SEVERE, int V_WARNING,
                                           int V_INFO, int V_CONFIG, int V_FINE, int V_FINER, int V_FINEST, int V_DEBUG,
                                           int V_TRACE, int V_ERROR, int V_FATAL, int V_SORT) {
        return logDAO.getLogByAll(text, dat1, dat2, V_SEVERE, V_WARNING, V_INFO, V_CONFIG, V_FINE, V_FINER, V_FINEST, V_DEBUG, V_TRACE, V_ERROR, V_FATAL, V_SORT);
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
        return log != null && log.getText() != null;
    }
}