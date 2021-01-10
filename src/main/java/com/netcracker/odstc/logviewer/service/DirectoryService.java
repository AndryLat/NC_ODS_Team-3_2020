package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.containers.DTO.DirectoryWithExtensionsDTO;
import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import com.netcracker.odstc.logviewer.service.exceptions.DirectoryServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class DirectoryService {
    private static final Class<Directory> directoryClass = Directory.class;
    private final Logger logger = LogManager.getLogger(DirectoryService.class.getName());
    private final EAVObjectDAO eavObjectDAO;
    private final ServerConnectionService serverConnectionService;

    public DirectoryService(EAVObjectDAO eavObjectDAO) {
        this.serverConnectionService = ServerConnectionService.getInstance();
        this.eavObjectDAO = eavObjectDAO;
    }

    public List<Directory> findByParentId(BigInteger id) {
        if (!isIdValid(id)) {
            throwDirectoryServiceExceptionWithMessage("Id is not valid. Cant get directories by parentId");
        }
        return eavObjectDAO.getObjectsByParentId(id, directoryClass);
    }

    public Directory findById(BigInteger id) {
        if (!isIdValid(id)) {
            throwDirectoryServiceExceptionWithMessage("Id is not valid. Cant get directory");
        }
        return eavObjectDAO.getObjectById(id, directoryClass);
    }

    public void add(Directory directory) {
        if (!isDirectoryValid(directory)) {
            throwDirectoryServiceExceptionWithMessage("Got invalid directory. Cant save directory");
        }
        directory.setLastExistenceCheck(new Date());
        directory.setLastAccessByUser(new Date());
        directory.setEnabled(true);
        directory.setConnectable(true);
        validateObjectType(directory);
        eavObjectDAO.saveObjectAttributesReferences(directory);
    }

    public void update(Directory directory) {
        if (!isDirectoryValid(directory)) {
            throwDirectoryServiceExceptionWithMessage("Got invalid directory. Cant save directory");
        }
        eavObjectDAO.saveObjectAttributesReferences(directory);
    }

    public void deleteById(BigInteger id) {
        if (!isIdValid(id)) {
            throwDirectoryServiceExceptionWithMessage("Got invalid id. Cant delete directory");
        }
        eavObjectDAO.deleteById(id);
    }

    public boolean testConnection(Directory directory) {
        if (!isDirectoryValid(directory) || directory.getParentId() == null) {
            throwDirectoryServiceExceptionWithMessage("Got invalid directory. Cant check invalid directory or without parentId");
        }
        Server server = eavObjectDAO.getObjectById(directory.getParentId(), Server.class);
        return serverConnectionService.isDirectoryAvailable(server, directory);
    }

    public List<LogFile> getLogFileList(DirectoryWithExtensionsDTO directoryWithExtensionsDTO) {
        if (!isDirectoryValid(directoryWithExtensionsDTO.getDirectory())) {
            throwDirectoryServiceExceptionWithMessage("Got invalid directory. Cant check invalid directory");
        }
        Directory directory = directoryWithExtensionsDTO.getDirectory();
        if (directory.getParentId() == null) {
            throwDirectoryServiceExceptionWithMessage("Cant check connection with directory without parentId");
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

    public void addLogFileList(List<LogFile> logFiles) {
        if (logFiles == null) {
            throwDirectoryServiceExceptionWithMessage("List of log files cant be equals null");
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

    private void validateObjectType(EAVObject eavObject) {
        if (eavObject instanceof Directory) {
            eavObject.setObjectTypeId(ObjectTypes.DIRECTORY.getObjectTypeID());
        }
        if (eavObject instanceof LogFile) {
            eavObject.setObjectTypeId(ObjectTypes.LOGFILE.getObjectTypeID());
        }
        logger.warn("Get EAVObject that not listed in validations");
    }

    private boolean isFileValid(LogFile logFile) {
        if (logFile != null) {
            return logFile.getName() != null;
        }
        return false;
    }

    private boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    private boolean isDirectoryValid(Directory directory) {
        if (directory != null) {
            return directory.getPath() != null && directory.getPath().trim().length() != 0;
        } else
            return false;
    }

    private void throwDirectoryServiceExceptionWithMessage(String message) {
        DirectoryServiceException directoryServiceException = new DirectoryServiceException(message);
        //logger.error(message,directoryServiceException);
        throw directoryServiceException;
    }

    private void throwDirectoryServiceExceptionWithMessage(String message, Throwable throwable) {
        DirectoryServiceException directoryServiceException = new DirectoryServiceException(message, throwable);
        //logger.error(message,directoryServiceException);
        throw directoryServiceException;
    }
}
