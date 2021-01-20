package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.service.exceptions.LogServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class LogService extends AbstractService {
    private final LogDAO logDAO;
    private final Logger logger = LogManager.getLogger(LogService.class.getName());

    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public Page<LogDTO> getAllLogsByAllValues(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {
        if (isIdValid(directoryId) && ruleContainer != null && pageable != null) {
            return logDAO.getLogByAll(directoryId, ruleContainer, pageable);
        } else
            throwLogServiceExceptionWithMessage("Values сan't be null");
        return null;
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            logDAO.deleteById(id);
        }
    }

    public Log findById(BigInteger id) {
        return logDAO.getObjectById(id, Log.class);
    }

    public void save(Log log) {
        if (isLogValid(log)) {
            logDAO.saveObjectAttributesReferences(log);
        }
    }

    public void deleteByIds(List<BigInteger> ids) {
        for (BigInteger id: ids) {
            if (isIdValid(id)) {
                logDAO.deleteById(id);
            }
        }
    }

    private void throwLogServiceExceptionWithMessage(String message) {
        LogServiceException logServiceException = new LogServiceException(message);
        logger.error(message, logServiceException);
        throw logServiceException;
    }

    private boolean isLogValid(Log log) {
        return log != null && log.getText() != null;
    }
}
