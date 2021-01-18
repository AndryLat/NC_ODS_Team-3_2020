package com.netcracker.odstc.logviewer.service;

import com.netcracker.odstc.logviewer.dao.EAVObjectDAO;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class ServerService extends AbstractService {

    private final EAVObjectDAO eavObjectDAO;
    private final Class<Server> serverClass = Server.class;

    public ServerService(EAVObjectDAO eavObjectDAO) {
        this.eavObjectDAO = eavObjectDAO;
    }


    public Server findById(BigInteger id) {
        if (isIdValid(id)) {
            return eavObjectDAO.getObjectById(id, serverClass);
        }
        return new Server();
    }

    public List<Server> showAllServersByPagination(PageRequest pageRequest, User user) {
        return eavObjectDAO.getObjectsByParentId(pageRequest, user.getObjectId(), Server.class);
    }

    public void add(Server server, BigInteger parentId) {
        if (isServerValid(server)) {
            server.setEnabled(true);
            server.setConnectable(true);
            server.setLastAccessByJob(new Date());
            server.setLastAccessByUser(new Date());
            server.setParentId(parentId);
            validateObjectType(server);

            eavObjectDAO.saveObjectAttributesReferences(server);
        }
    }

    public void update(Server server) {
        if (isServerValid(server)) {
            eavObjectDAO.saveObjectAttributesReferences(server);
        }
    }

    public void deleteById(BigInteger id) {
        if (isIdValid(id)) {
            eavObjectDAO.deleteById(id);
        }
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
