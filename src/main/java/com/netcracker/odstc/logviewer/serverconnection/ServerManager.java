package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.LogClass;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 *
 * @author Aleksanid
 * created 01.12.2020
 */
public class ServerManager {
    private Logger logger = LogManager.getLogger(ServerManager.class.getName());
    private static ServerManager instance;
    //TODO: Разбить на 4 потока
    private List<ServerConnection> serverConnectionList;
    private List<ServerConnection> nonConnectedServers;//TODO: Пометки директорий?

    private ServerManager() {
        serverConnectionList = new LinkedList<>();
        nonConnectedServers = new LinkedList<>();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public List<ServerConnection> getServerConnectionList() {
        return serverConnectionList;
    }

    public boolean addServerConnection(ServerConnection serverConnection) {
        boolean isServerActive = serverConnection.connect();
        if (isServerActive) {
            LogClass.log(Level.INFO, "Got new server " + serverConnection.getServer().getName() + " moved to valid");
            serverConnectionList.add(serverConnection);
        } else {
            serverConnection.getServer().setActive(false);
            LogClass.log(Level.INFO, "Got new server " + serverConnection.getServer().getName() + " moved to non-valid");
            nonConnectedServers.add(serverConnection);
        }
        return isServerActive;
    }
//TODO: Конекшены тайм-аутятся.
    public boolean addServerConnection(Server server) {
        ServerConnection serverConnection;
        if (server.getProtocol() == Protocol.FTP) {
            serverConnection = new FTPServerConnection(server);
        } else if (server.getProtocol() == Protocol.SSH) {
            serverConnection = new SSHServerConnection(server);
        } else {
            throw new IllegalArgumentException("Cant wrap server with unknown protocol");
        }
        return addServerConnection(serverConnection);
    }

    public List<Log> accessAllServers() {
        List<Log> result = new LinkedList<>();
        Iterator<ServerConnection> serverConnectionIterator = serverConnectionList.iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            try {
                result.addAll(serverConnection.getNewLogs());
            } catch (ServerLogProcessingException e) {
                logger.info( "Get Error in polling time " + serverConnection.getServer().getName() + " to non-valid connections");
                serverConnection.getServer().setActive(false);
                nonConnectedServers.add(serverConnection);//TODO: Замена без итератора?
                serverConnectionIterator.remove();
            }
        }
        return result;//TODO: Кому то отдавать
    }

    // TODO: Отключить если давно не использовался?
    public void revalidateServers() {
        Iterator<ServerConnection> serverConnectionIterator = nonConnectedServers.iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (serverConnection.connect()) {
                LogClass.log(Level.INFO, "Moved server with name " + serverConnection.getServer().getName() + " to valid connections");
                serverConnection.getServer().setActive(true);
                serverConnectionList.add(serverConnection);
                serverConnectionIterator.remove();
            }
        }
    }
}
