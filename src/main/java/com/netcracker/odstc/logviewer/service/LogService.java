package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.containers.RuleContainer;
import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import com.netcracker.odstc.logviewer.dao.LogDAO;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import com.netcracker.odstc.logviewer.service.exceptions.LogServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Service
public class LogService extends AbstractService {
    private static final Logger logger = LogManager.getLogger(LogService.class.getName());
    private static final String LOG_NULL_MESSAGE = "Log shouldn't be 0 or null";
    private static final String LOG_ID_NULL_MESSAGE = "Log shouldn't be 0 or null";
    private final LogDAO logDAO;

    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public Page<LogDTO> getLogsByDirectoryId(BigInteger directoryId, RuleContainer ruleContainer, Pageable pageable) {
        if (!isIdValid(directoryId) || ruleContainer == null || pageable == null) {
            throw buildLogServiceExceptionWithMessage("Values сan't be null");
        }
        if (ruleContainer.getLevels() != null) {
            if (ruleContainer.getLevels().isEmpty()) {
                ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
            }
        } else {
            ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
        }
        return logDAO.getLogsByDirectoryId(directoryId, ruleContainer, pageable);
    }

    public Page<LogDTO> getLogsByFileId(BigInteger fileId, RuleContainer ruleContainer, Pageable pageable) {
        if (!isIdValid(fileId) || ruleContainer == null || pageable == null) {
            throw buildLogServiceExceptionWithMessage("Values сan't be null");
        }
        if (ruleContainer.getLevels() != null) {
            if (ruleContainer.getLevels().isEmpty()) {
                ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
            }
        } else {
            ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
        }
        return logDAO.getLogsByFileId(fileId, ruleContainer, pageable);
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            logDAO.deleteById(id);
        }
        throw buildLogServiceExceptionWithMessage(LOG_ID_NULL_MESSAGE);
    }

    public Log findById(BigInteger id) {
        if (id == null || id.equals(BigInteger.valueOf(0))) {
            throw buildLogServiceExceptionWithMessage(LOG_ID_NULL_MESSAGE);
        }
        return logDAO.getObjectById(id, Log.class);
    }

    public void save(Log log) {
        if (isLogValid(log)) {
            logDAO.saveObjectAttributesReferences(log);
        }
        throw buildLogServiceExceptionWithMessage(LOG_NULL_MESSAGE);
    }

    public void deleteByIds(List<BigInteger> ids) {
        for (BigInteger id : ids) {
            if (id == null || id.equals(BigInteger.valueOf(0))) {
                logDAO.deleteById(id);
                throw buildLogServiceExceptionWithMessage(LOG_ID_NULL_MESSAGE);
            }
        }
    }

    public BigInteger getCountByDirectory(BigInteger directoryId, RuleContainer ruleContainer) {
        if (ruleContainer.getLevels() != null) {
            if (ruleContainer.getLevels().isEmpty()) {
                ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
            }
        } else {
            ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
        }
        return logDAO.getLogsCountByDirectory(directoryId, ruleContainer);
    }

    public BigInteger getCountByLogFile(BigInteger fileId, RuleContainer ruleContainer) {
        if (ruleContainer.getLevels() != null) {
            if (ruleContainer.getLevels().isEmpty()) {
                ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
            }
        } else {
            ruleContainer.setLevels(Arrays.asList(LogLevel.values()));
        }
        return logDAO.getLogsCountByFile(fileId, ruleContainer);
    }

    private LogServiceException buildLogServiceExceptionWithMessage(String message) {
        LogServiceException logServiceException = new LogServiceException(message);
        logger.error(message, logServiceException);
        return logServiceException;
    }

    private boolean isLogValid(Log log) {
        return log != null && log.getText() != null;
    }
}
