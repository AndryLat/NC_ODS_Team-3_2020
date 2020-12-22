package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.dao.ContainerDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.lists.Protocol;
import com.netcracker.odstc.logviewer.serverconnection.FTPServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.SSHServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOChangeListener;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import com.netcracker.odstc.logviewer.serverconnection.publishers.ObjectChangeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ServerManager implements DAOChangeListener {
    private final ContainerDAO containerDAO;
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());
    private final Map<BigInteger, List<BigInteger>> iterationRemove;
    private final Map<BigInteger, ServerConnection> serverConnections;

    private final ServerPollManager serverPollManager;

    @SuppressWarnings({"squid:S1144"})//Suppress unused private constructor
    private ServerManager(ContainerDAO containerDAO) {
        serverPollManager = ServerPollManager.getInstance();
        DAOPublisher.getInstance().addListener(this);
        serverConnections = Collections.synchronizedMap(new HashMap<>());
        this.containerDAO = containerDAO;
        iterationRemove = new HashMap<>();
    }

    public void getLogsFromAllServers() {
        savePollResults();

        updateActiveServersFromDB();

        startPoll();
    }

    public void revalidateServers() {
        List<HierarchyContainer> servers = containerDAO.getNonactiveServers();
        List<Server> serversToSave = new ArrayList<>();
        for (HierarchyContainer serverContainer : servers) {
            ServerConnection serverConnection = wrapServerIntoConnection(serverContainer);
            if (serverConnection == null) continue;
            serverConnection.connect();
            serverConnection.disconnect();
            serversToSave.add(serverConnection.getServer());
        }
        containerDAO.saveObjects(serversToSave);
        revalidateActiveServersDirectories();
    }

    private void revalidateActiveServersDirectories() {
        List<HierarchyContainer> servers = containerDAO.getActiveServersWithNonactiveDirectories();
        List<Directory> directories = new ArrayList<>();

        for (HierarchyContainer serverContainer : servers) {
            ServerConnection serverConnection = wrapServerIntoConnection(serverContainer);
            if (serverConnection == null) continue;
            serverConnection.setDirectories(serverContainer.getChildren());
            serverConnection.revalidateDirectories();
            for (HierarchyContainer directoryContainer : serverConnection.getDirectories()) {
                directories.add((Directory) directoryContainer.getOriginal());
            }
        }
        containerDAO.saveObjects(directories);
    }

    @Override
    public void objectChanged(ObjectChangeEvent objectChangeEvent) {
        if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.DELETE) {
            BigInteger objectTypeId = (BigInteger) objectChangeEvent.getArgument();
            BigInteger objectId = (BigInteger) objectChangeEvent.getObject();
            iterationRemove.get(objectTypeId).add(objectId);
        }
        if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.UPDATE) {
            if (Server.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                serverChanged(objectChangeEvent);
            }
            if (Directory.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                directoryChanged(objectChangeEvent);
            }
        }
    }

    private void directoryChanged(ObjectChangeEvent objectChangeEvent) {
        Directory directory = (Directory) objectChangeEvent.getObject();
        ServerConnection serverConnection = serverConnections.get(directory.getParentId());
        if (!directory.isEnabled()) {
            serverConnection.removeDirectory(directory);
        } else {
            serverConnection.updateDirectory(directory);
        }
    }

    private void serverChanged(ObjectChangeEvent objectChangeEvent) {
        Server server = (Server) objectChangeEvent.getObject();
        if (!server.isEnabled()) {
            serverConnections.remove(server.getObjectId());
        } else {
            ServerConnection serverConnection = serverConnections.get(server.getObjectId());
            if (serverConnection != null)
                serverConnection.setServer(server);
        }
    }

    private void startPoll() {
        Iterator<ServerConnection> serverConnectionIterator = serverConnections.values().iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (serverConnection.getServer().isCanConnect()) {
                serverPollManager.executeExtractingLogs(serverConnection);
            } else {
                serverConnectionIterator.remove();
            }
        }
    }

    private void updateActiveServersFromDB() {
        List<HierarchyContainer> serverContainers = containerDAO.getActiveServersWithChildren();
        logger.info("Active Servers: {}", serverContainers.size());
        for (HierarchyContainer serverContainer : serverContainers) {
            Server server = (Server) serverContainer.getOriginal();
            if (serverConnections.containsKey(server.getObjectId())) {
                serverConnections.get(server.getObjectId()).setServer(server);
                serverConnections.get(server.getObjectId()).setDirectories(serverContainer.getChildren());
            } else {
                ServerConnection serverConnection = wrapServerIntoConnection(serverContainer);
                if (serverConnection == null) continue;
                serverConnection.setDirectories(serverContainer.getChildren());
                serverConnections.put(server.getObjectId(), serverConnection);
            }
        }
    }

    private void savePollResults() {
        List<Log> result = new ArrayList<>(serverPollManager.getLogsFromThreads());
        List<Server> servers = new ArrayList<>(serverConnections.size());
        List<Directory> directories = new ArrayList<>();
        List<LogFile> logFiles = new ArrayList<>();
        for (ServerConnection serverConnection : serverConnections.values()) {
            if (iterationRemove.get(BigInteger.valueOf(2)).contains(serverConnection.getServer().getObjectId())) {
                continue;
            }
            servers.add(serverConnection.getServer());
            for (HierarchyContainer directoryContainer : serverConnection.getDirectories()) {
                if (iterationRemove.get(BigInteger.valueOf(3)).contains(directoryContainer.getOriginal().getObjectId())) {
                    continue;
                }
                directories.add((Directory) directoryContainer.getOriginal());
                for (HierarchyContainer logFileContainer : directoryContainer.getChildren()) {
                    if (!iterationRemove.get(BigInteger.valueOf(4)).contains(logFileContainer.getOriginal().getObjectId())) {
                        continue;
                    }
                    logFiles.add((LogFile) logFileContainer.getOriginal());
                }
            }
        }
        clearIterationInfo();
        containerDAO.saveObjectsAttributesReferences(result);
        containerDAO.saveObjectsAttributesReferences(servers);
        containerDAO.saveObjectsAttributesReferences(directories);
        containerDAO.saveObjectsAttributesReferences(logFiles);
    }

    private void clearIterationInfo() {
        iterationRemove.get(BigInteger.valueOf(2)).clear();
        iterationRemove.get(BigInteger.valueOf(3)).clear();
        iterationRemove.get(BigInteger.valueOf(4)).clear();
    }

    private ServerConnection wrapServerIntoConnection(HierarchyContainer serverContainer) {
        Server server = (Server) serverContainer.getOriginal();
        ServerConnection serverConnection;
        if (server.getProtocol() == Protocol.FTP) {
            serverConnection = new FTPServerConnection(server);
        } else if (server.getProtocol() == Protocol.SSH) {
            serverConnection = new SSHServerConnection(server);
        } else {
            logger.error("Cant wrap server with unknown protocol");
            return null;
        }
        return serverConnection;
    }
}
