package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Server;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigInteger;


@Component
public class ServerService {

    private final EAVObjectDAO eavObjectDAO;
    private final Class<Server> serverClass = Server.class;

    public ServerService(@Qualifier("EAVObjectDAO") EAVObjectDAO eavObjectDAO) {
        this.eavObjectDAO = eavObjectDAO;
    }


    public Server findById(BigInteger id) {
        if (isIdValid(id)) {
            return eavObjectDAO.getObjectById(id, serverClass);
        }
        return new Server();
    }

    public void save(Server server) {
        if (isServerValid(server)) {
            eavObjectDAO.saveObjectAttributesReferences(server);
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

    private boolean isServerValid(Server server) {
        if (server == null) {
            return false;
        }
        if (server.getIp() == null || server.getIp().trim().length() == 0) {
            return false;
        }
        if (server.getLogin() == null || server.getLogin().trim().length() == 0) {
            return false;
        }
        if (server.getPassword() == null || server.getPassword().trim().length() == 0) {
            return false;
        }
        if (server.getProtocol() == null) {
            return false;
        }
        return server.getPort() != 0;
    }
}
