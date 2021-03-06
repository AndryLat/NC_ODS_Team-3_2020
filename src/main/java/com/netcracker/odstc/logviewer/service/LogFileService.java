package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.serverconnection.services.exceptions.ServerConnectionServiceException;
import com.netcracker.odstc.logviewer.service.exceptions.LogFileServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class LogFileService extends AbstractService {
    private final Logger logger = LogManager.getLogger(LogFileService.class.getName());
    private static final String INVALID_DIRECTORY_MESSAGE = "Got invalid directory. Can't check invalid directory";
    private static final String PARENT_NULL_MESSAGE = "Can't check connection with directory without parentId";
    private final EAVObjectDAO eavObjectDAO;

    private final ServerConnectionService serverConnectionService;

    public LogFileService(EAVObjectDAO eavObjectDAO) {
        this.serverConnectionService = ServerConnectionService.getInstance();
        this.eavObjectDAO = eavObjectDAO;
    }

    public List<LogFile> getLogFileList(Directory directory) {
        if (directory == null) {
            throw buildLogFilesServiceExceptionWithMessage(INVALID_DIRECTORY_MESSAGE);
        }
        if (directory.getParentId() == null) {
            throw buildLogFilesServiceExceptionWithMessage(PARENT_NULL_MESSAGE);
        }
        Server server = eavObjectDAO.getObjectById(directory.getParentId(), Server.class);
        try {
            return serverConnectionService.getLogFilesFromDirectory(server, directory);
        } catch (ServerConnectionServiceException e) {
            throw buildLogFilesServiceExceptionWithMessage(e.getMessage(), e);
        }
    }

    public List<LogFile> getLogFileListFromDB(BigInteger objectId) {
        return eavObjectDAO.getObjectsByParentId(objectId, LogFile.class);
    }

    public Page<LogFile> getLogFileListByPage(PageRequest pageRequest, Directory directory) {
        if (directory == null) {
            throw buildLogFilesServiceExceptionWithMessage(INVALID_DIRECTORY_MESSAGE);
        }
        if (directory.getParentId() == null) {
            throw buildLogFilesServiceExceptionWithMessage(PARENT_NULL_MESSAGE);
        }
        return eavObjectDAO.getObjectsByParentId(pageRequest, directory.getObjectId(), LogFile.class);
    }

    public LogFile findById(BigInteger id) {
        if (!isIdValid(id)) {
            throw buildLogFilesServiceExceptionWithMessage("Id is not valid. Can't get file of logs");
        }
        return eavObjectDAO.getObjectById(id, LogFile.class);
    }

    public void addLogFile(LogFile logFile) {
        if (logFile == null) {
            throw buildLogFilesServiceExceptionWithMessage("List of log files cant be equals null");
        } else {
            if (isFileValid(logFile)) {
                logFile.setLastUpdate(new Date());
                logFile.setLastRow(0);
                validateObjectType(logFile);
                eavObjectDAO.saveObjectAttributesReferences(logFile);
            } else {
                logger.error("Skipping non valid file");
            }
        }
    }

    public void addLogFileList(List<LogFile> logFiles) {
        if (logFiles == null) {
            throw buildLogFilesServiceExceptionWithMessage("List of log files cant be equals null");
        } else {
            for (LogFile logFile : logFiles) {
                if (isFileValid(logFile)) {
                    logFile.setLastUpdate(new Date());
                    logFile.setLastRow(0);
                    validateObjectType(logFile);
                    eavObjectDAO.saveObjectAttributesReferences(logFile);
                } else {
                    logger.error("Skipping non valid file");
                }
            }
        }
    }

    public void deleteById(BigInteger id) {
        if (!isIdValid(id)) {
            throw buildLogFilesServiceExceptionWithMessage("Id shouldn't be 0 or null");
        }
        eavObjectDAO.deleteById(id);
    }

    private boolean isFileValid(LogFile logFile) {
        if (logFile != null) {
            return logFile.getFileName() != null;
        }
        return false;
    }

    private LogFileServiceException buildLogFilesServiceExceptionWithMessage(String message) {
        LogFileServiceException logFileServiceException = new LogFileServiceException(message);
        logger.error(message, logFileServiceException);
        return logFileServiceException;
    }

    private LogFileServiceException buildLogFilesServiceExceptionWithMessage(String message, Throwable throwable) {
        LogFileServiceException logFileServiceException = new LogFileServiceException(message, throwable);
        logger.error(message, logFileServiceException);
        return logFileServiceException;
    }
}
