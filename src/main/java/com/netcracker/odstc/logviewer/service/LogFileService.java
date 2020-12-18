package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.LogFile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class LogFileService {

    private final EAVObjectDAO eavObjectDAO;

    public LogFileService(@Qualifier("EAVObjectDAO") EAVObjectDAO eavObjectDAO) {
        this.eavObjectDAO = eavObjectDAO;
    }


    public LogFile findById(BigInteger id) {
        if (isIdValid(id)) {
            return eavObjectDAO.getObjectById(id, LogFile.class);
        }
        return null;
    }

    public List<LogFile> getFiles() {
        return eavObjectDAO.getObjectsByObjectTypeId(BigInteger.valueOf(4), LogFile.class);
    }

    public void save(LogFile logFile) {
        if (isLogFileValid(logFile)) {
            eavObjectDAO.saveObject(logFile);
        }
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            eavObjectDAO.deleteById(id);
        }
    }

    private boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    private boolean isLogFileValid(LogFile logFile) {
        return logFile != null
                && logFile.getName() != null
                && logFile.getName().trim().length() != 0
                && logFile.getLastRow() > 0
                && logFile.getLastUpdate() != null;
    }
}
