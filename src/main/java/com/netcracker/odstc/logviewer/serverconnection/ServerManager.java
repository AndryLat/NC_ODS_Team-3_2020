package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ServerManager {

    private static ServerManager instance;
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());
    //TODO: Разбить на 4 потока
    private List<ServerConnection> validServerConnections;
    private List<ServerConnection> disabledServerConnections;
    private ServerManager() {
        validServerConnections = new LinkedList<>();
        disabledServerConnections = new LinkedList<>();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public List<ServerConnection> getDisabledServerConnections() {
        return disabledServerConnections;
    }

    public List<ServerConnection> getValidServerConnections() {
        return validServerConnections;
    }

    public boolean addServerConnection(ServerConnection serverConnection) {
        boolean isServerActive = serverConnection.connect();
        if (isServerActive) {
            logger.info("Got new server {} moved to valid",serverConnection.getServer().getName());
            validServerConnections.add(serverConnection);
        } else {
            serverConnection.getServer().setActive(false);
            logger.info("Got new server {} moved to non-valid",serverConnection.getServer().getName());
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

    public boolean removerServerConnection(ServerConnection serverConnection){
        if(serverConnection.getServer().isActive()){
            return validServerConnections.remove(serverConnection);
        }else {
            return disabledServerConnections.remove(serverConnection);
        }
    }
    public boolean removerServerConnection(Server server){
        //Реализация?
        return false;
    }

    public List<Log> getLogsFromAllServers() {// Я точно смогу получать листы?
        List<Log> result = new LinkedList<>();
        Iterator<ServerConnection> serverConnectionIterator = validServerConnections.iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            try {
                result.addAll(serverConnection.getNewLogs());
                serverConnection.getServer().setLastAccessByJob(new Date());
            } catch (ServerLogProcessingException e) {
                logger.info("Get Error in polling time {} to non-valid connections",serverConnection.getServer().getName(),e);
                serverConnection.getServer().setActive(false);
                disabledServerConnections.add(serverConnection);//TODO: Замена без итератора?
                serverConnectionIterator.remove();
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
                logger.info("Moved server with name {}} to valid connections",serverConnection.getServer().getName());
                serverConnection.getServer().setActive(true);
                validServerConnections.add(serverConnection);
                serverConnectionIterator.remove();
            }
        }
    }

    public void revalidateActiveDirectories() {
        Iterator<ServerConnection> serverConnectionIterator = disabledServerConnections.iterator();
        while (serverConnectionIterator.hasNext()) {
            serverConnectionIterator.next().revalidateDirectories();
        }
    }
}
