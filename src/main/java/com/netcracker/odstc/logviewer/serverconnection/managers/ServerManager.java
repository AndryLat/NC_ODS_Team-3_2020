package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.FTPServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.SSHServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ServerManager {

    private static ServerManager instance;
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());
    private Set<ServerConnection> validServerConnections;
    private Set<ServerConnection> disabledServerConnections;
    private ServerPollManager serverPollManager = ServerPollManager.getInstance();

    private ServerManager() {
        validServerConnections = new HashSet<>();
        disabledServerConnections = new HashSet<>();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public Set<ServerConnection> getDisabledServerConnections() {
        return disabledServerConnections;
    }

    public Set<ServerConnection> getValidServerConnections() {
        return validServerConnections;
    }

    public boolean addServerConnection(ServerConnection serverConnection) {
        boolean isServerActive = serverConnection.connect();
        if (isServerActive) {
            logger.info("Got new server {} moved to valid", serverConnection.getServer().getName());
            validServerConnections.add(serverConnection);
        } else {
            serverConnection.getServer().setActive(false);
            logger.info("Got new server {} moved to non-valid", serverConnection.getServer().getName());
            disabledServerConnections.add(serverConnection);
        }
        return isServerActive;
    }

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

    public boolean removerServerConnection(ServerConnection serverConnection) {
        if (serverConnection.getServer().isActive()) {
            return validServerConnections.remove(serverConnection);
        } else {
            return disabledServerConnections.remove(serverConnection);
        }
    }

    public boolean removerServerConnection(Server server) {
        if (server.isActive()) {
            for (ServerConnection serverConnection : validServerConnections) {
                if (server.getId().equals(serverConnection.getServer().getId())) {
                    return validServerConnections.remove(serverConnection);
                }
            }
        } else {
            for (ServerConnection serverConnection : disabledServerConnections) {
                if (server.getId().equals(serverConnection.getServer().getId())) {
                    return disabledServerConnections.remove(serverConnection);
                }
            }
        }
        return false;
    }

    public List<Log> getLogsFromAllServers() {// Я точно смогу получать листы с дочерними объектами?
        List<Log> result = new LinkedList<>(serverPollManager.getAsyncLogs());
        Iterator<ServerConnection> serverConnectionIterator = validServerConnections.iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (!serverConnection.getServer().isActive()) {
                logger.debug("Got nonactive server in valid, moved to non active");
                disabledServerConnections.add(serverConnection);//TODO: Замена без итератора? Существует ли она?
                serverConnectionIterator.remove();
            } else {
                serverPollManager.executeExtractingLogs(serverConnection);
            }
        }
        return result;//TODO: Кому то отдавать или вызывать на всех сейв
    }

    public void revalidateDisabledServers() {
        Iterator<ServerConnection> serverConnectionIterator = disabledServerConnections.iterator();
        Config appConfiguration = Config.getInstance();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (new Date(serverConnection.getServer().getLastAccessByUser().getTime() + appConfiguration.getServerActivityPeriod().getTime()).before(new Date()))
                return;
            if (serverConnection.connect()) {
                logger.info("Moved server with name {} to valid connections", serverConnection.getServer().getName());
                validServerConnections.add(serverConnection);//TODO: Замена без итератора? Существует ли она?
                serverConnectionIterator.remove();
            }
        }
    }

    public void revalidateActiveDirectories() {
        for (ServerConnection disabledServerConnection : disabledServerConnections) {
            disabledServerConnection.revalidateDirectories();
        }
    }
}
