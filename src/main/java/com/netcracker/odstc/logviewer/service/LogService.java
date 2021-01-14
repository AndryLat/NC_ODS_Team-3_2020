package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class LogService extends AbstractService {
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

    public Page<LogDTO> getAllLogsByAllValues(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {
        return new PageImpl<>(logDAO.getLogByAll(directoryId, ruleContainer, pageable));
    }

    public void save(Log log) {
        if (isLogValid(log)) {
            logDAO.saveObjectAttributesReferences(log);
        }
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            logDAO.deleteById(id);
        }
    }

    private boolean isLogValid(Log log) {
        return log != null && log.getText() != null;
    }
}
