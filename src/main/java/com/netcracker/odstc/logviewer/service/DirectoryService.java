package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class DirectoryService {
    private final EAVObjectDAO eavObjectDAO;
    private final Class<Directory> directoryClass = Directory.class;

    public DirectoryService(@Qualifier("EAVObjectDAO") EAVObjectDAO eavObjectDAO) {
        this.eavObjectDAO = eavObjectDAO;
    }

    public Directory findById(BigInteger id) {
        if (isIdValid(id)) {
            return eavObjectDAO.getObjectById(id, directoryClass);
        }
        return new Directory();
    }

    public void save(Directory directory) {
        if (isDirectoryValid(directory)) {
            eavObjectDAO.saveObject(directory);
        }
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            eavObjectDAO.deleteById(id);
        }
    }

    public List<Directory> getDirectories() {
        return eavObjectDAO.getObjectsByObjectTypeId(BigInteger.valueOf(4), Directory.class);
    }

    private boolean isIdValid(BigInteger id) {
        return id != null && !id.equals(BigInteger.valueOf(0));
    }

    private boolean isDirectoryValid(Directory directory) {
        if (directory != null) {
            return directory.getPath() != null && directory.getPath().trim().length() != 0 && directory.isEnabled();
        } else
            return false;
    }
}
