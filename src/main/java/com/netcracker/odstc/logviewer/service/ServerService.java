package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.service.exceptions.ServerServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

@Service
public class ServerService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ServerService.class.getName());
    private final EAVObjectDAO eavObjectDAO;
    private static final Class<Server> serverClass = Server.class;
    private static final String SERVER_NOT_NULL_MESSAGE = "Server shouldn't be null";
    private static final String ID_NOT_NULL_MESSAGE = "Id shouldn't be 0 or null";

    public ServerService(EAVObjectDAO eavObjectDAO) {
        this.eavObjectDAO = eavObjectDAO;
    }


    public Server findById(BigInteger id) {
        if (!isIdValid(id)) {
            throwServerServiceExceptionWithMessage(ID_NOT_NULL_MESSAGE);
        }
        return eavObjectDAO.getObjectById(id, serverClass);
    }

    public Page<Server> showAllServersByPagination(PageRequest pageRequest, User user) {
        return eavObjectDAO.getObjectsByParentId(pageRequest, user.getObjectId(), Server.class);
    }

    public void add(Server server, BigInteger parentId) {
        if (!isServerValid(server)) {
            throwServerServiceExceptionWithMessage(SERVER_NOT_NULL_MESSAGE);
        }
        server.setEnabled(false);
        server.setConnectable(true);
        server.setLastAccessByJob(new Date());
        server.setLastAccessByUser(new Date());
        server.setParentId(parentId);
        validateObjectType(server);
        eavObjectDAO.saveObjectAttributesReferences(server);
    }

    public void update(Server server) {
        if (!isServerValid(server)) {
            throwServerServiceExceptionWithMessage(SERVER_NOT_NULL_MESSAGE);
        }
        eavObjectDAO.saveObjectAttributesReferences(server);
    }

    public void deleteById(BigInteger id) {
        if (!isIdValid(id)) {
            throwServerServiceExceptionWithMessage(ID_NOT_NULL_MESSAGE);
        }
        eavObjectDAO.deleteById(id);
    }

    private boolean isServerValid(Server server) {
        if (server == null) {
            return false;
        }
        if (isStringHaveAnythingExceptSpacesValid(server.getIp())) {
            return false;
        }
        if (isStringHaveAnythingExceptSpacesValid(server.getLogin())) {
            return false;
        }
        if (isStringHaveAnythingExceptSpacesValid(server.getPassword())) {
            return false;
        }
        if (server.getProtocol() == null) {
            return false;
        }
        return server.getPort() != 0;
    }

    private boolean isStringHaveAnythingExceptSpacesValid(String string) {
        return string == null || string.trim().length() == 0;
    }

    private void throwServerServiceExceptionWithMessage(String message) {
        ServerServiceException serviceException = new ServerServiceException(message);
        logger.error(message, serviceException);
        throw serviceException;
    }

}
