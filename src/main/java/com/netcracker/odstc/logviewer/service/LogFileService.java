package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.containers.DTO.DirectoryWithExtensionsDTO;
import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.service.exceptions.LogFileServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogFileService extends AbstractService {

    private final Logger logger = LogManager.getLogger(LogFileService.class.getName());

    private final EAVObjectDAO eavObjectDAO;

    private final ServerConnectionService serverConnectionService;

    public LogFileService(EAVObjectDAO eavObjectDAO) {
        this.serverConnectionService = ServerConnectionService.getInstance();
        this.eavObjectDAO = eavObjectDAO;
    }

    public List<LogFile> getLogFileList(DirectoryWithExtensionsDTO directoryWithExtensionsDTO) {
        if (directoryWithExtensionsDTO.getDirectory()!=null) {
            throwLogFilesServiceExceptionWithMessage("Got invalid directory. Cant check invalid directory");
        }
        Directory directory = directoryWithExtensionsDTO.getDirectory();
        if (directory.getParentId() == null) {
            throwLogFilesServiceExceptionWithMessage("Cant check connection with directory without parentId");
        }
        String[] extensions;
        if (directoryWithExtensionsDTO.getExtensions() == null) {
            extensions = new String[]{""};
        } else {
            extensions = directoryWithExtensionsDTO.getExtensions();
        }
        Server server = eavObjectDAO.getObjectById(directory.getParentId(), Server.class);
        return serverConnectionService.getLogFilesFromDirectory(server, directory, extensions);
    }

    private void throwLogFilesServiceExceptionWithMessage(String message) {
        LogFileServiceException logFileServiceException = new LogFileServiceException(message);
        logger.error(message, logFileServiceException);
        throw logFileServiceException;
    }

    public void addLogFileList(List<LogFile> logFiles) {
        if (logFiles == null) {
            throwLogFilesServiceExceptionWithMessage("List of log files cant be equals null");
        }
        for (LogFile logFile : logFiles) {
            if (isFileValid(logFile)) {
                logFile.setLastUpdate(new Date());
                logFile.setLastRow(0);
                validateObjectType(logFile);
                eavObjectDAO.saveObject(logFile);
            } else {
                logger.error("Skipping non valid file");
            }
        }
    }

    private boolean isFileValid(LogFile logFile) {
        if (logFile != null) {
            return logFile.getName() != null;
        }
        return false;
    }


}
