package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class LogService {
    private final LogDAO logDAO;

    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public Log findById(BigInteger id) {
        return logDAO.getObjectById(id, Log.class);
    }

    public List<Log> getAllLogs() {
        return logDAO.getAll();
    }

    public List<Log> getAllLogsByAllValues(RuleContainer allValuesForLogs) {
        return logDAO.getLogByAll(allValuesForLogs.getText(), allValuesForLogs.getDat1(), allValuesForLogs.getDat2(),
                allValuesForLogs.getSevere(), allValuesForLogs.getWarning(), allValuesForLogs.getInfo(),
                allValuesForLogs.getConfig(), allValuesForLogs.getFine(), allValuesForLogs.getFiner(),
                allValuesForLogs.getFinest(), allValuesForLogs.getDebug(), allValuesForLogs.getTrace(),
                allValuesForLogs.getError(), allValuesForLogs.getFatal(), allValuesForLogs.getSort());
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