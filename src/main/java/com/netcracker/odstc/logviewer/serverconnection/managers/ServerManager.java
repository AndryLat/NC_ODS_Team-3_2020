package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.dao.ContainerDAO;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.containers.RemovedObjectsCollector;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOChangeListener;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import com.netcracker.odstc.logviewer.serverconnection.publishers.ObjectChangeEvent;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerManager implements DAOChangeListener {
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());

    private final ContainerDAO containerDAO;

    private final RemovedObjectsCollector removedObjectsCollector;
    private final Map<BigInteger, ServerConnection> serverConnections;

    private final ServerPollManager serverPollManager;
    private final ServerConnectionService serverConnectionService;

    @SuppressWarnings({"squid:S1144"})
//Suppress unused private constructor: Spring will use this constructor, even if it private
    private ServerManager(ContainerDAO containerDAO) {
        this.containerDAO = containerDAO;
        DAOPublisher.getInstance().addListener(this, ObjectTypes.DIRECTORY, ObjectTypes.SERVER, ObjectTypes.LOGFILE);

        serverConnectionService = ServerConnectionService.getInstance();
        serverPollManager = ServerPollManager.getInstance();
        removedObjectsCollector = new RemovedObjectsCollector();

        serverConnections = new ConcurrentHashMap<>();
    }

    @Override
    public void objectChanged(ObjectChangeEvent objectChangeEvent) {
        if (ContainerDAO.class.isAssignableFrom(objectChangeEvent.getSource().getClass())) {
            return;
        }
        if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.DELETE) {
            BigInteger objectTypeId = (BigInteger) objectChangeEvent.getArgument();
            BigInteger objectId = (BigInteger) objectChangeEvent.getObject();
            removedObjectsCollector.addRemovedObjectId(ObjectTypes.getObjectTypesByObjectTypeId(objectTypeId), objectId);
        } else if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.UPDATE) {
            if (Server.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                serverChanged(objectChangeEvent);
            } else if (Directory.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                directoryChanged(objectChangeEvent);
            }
        }
    }

    public void getLogsFromAllServers() {
        List<Log> result = new ArrayList<>(serverPollManager.getLogsFromThreads());
        if (logger.isInfoEnabled() && !result.isEmpty()) {
            logger.info("Gathered {} new logs from poll.", result.size());
        }
        containerDAO.saveObjectsAttributesReferences(result);
        if (!serverPollManager.getActiveServerConnections().isEmpty()) {
            logger.warn("Skipping job due to previous is not finished");
            return;
        }
        savePollResults();
        updateActiveServersFromDB();
        startPoll();
    }

    public void revalidateServers() {
        logger.info("Starting check non-active servers");
        List<HierarchyContainer> servers = containerDAO.getNonactiveServers();
        List<Server> serversToSave = new ArrayList<>();
        for (HierarchyContainer serverContainer : servers) {
            ServerConnection serverConnection = serverConnectionService.wrapServerIntoConnection(serverContainer);
            if (serverConnection == null) continue;
            try {
                serverConnection.connect();
                serverConnection.disconnect();
            } catch (ServerConnectionException e) {
                logger.error(e);
            }
            serversToSave.add(serverConnection.getServer());
        }
        containerDAO.saveObjectsAttributesReferences(serversToSave);
        revalidateActiveServersDirectories();
    }

    private void revalidateActiveServersDirectories() {
        logger.info("Starting check non-active directories in active servers");
        List<HierarchyContainer> servers = containerDAO.getActiveServersWithNonactiveDirectories();
        List<Directory> directories = new ArrayList<>();

        for (HierarchyContainer serverContainer : servers) {
            ServerConnection serverConnection = serverConnectionService.wrapServerIntoConnection(serverContainer);
            if (serverConnection == null) continue;
            serverConnection.setDirectories(serverContainer.getChildren());
            try {
                serverConnection.revalidateDirectories();
            } catch (ServerConnectionException e) {
                logger.error(e);
            }
            for (HierarchyContainer directoryContainer : serverConnection.getDirectories()) {
                directories.add((Directory) directoryContainer.getOriginal());
            }
        }
        containerDAO.saveObjectsAttributesReferences(directories);
    }

    private void directoryChanged(ObjectChangeEvent objectChangeEvent) {
        Directory directory = (Directory) objectChangeEvent.getObject();
        if(!directory.getAttributes().containsKey(Attributes.PATH_OT_DIRECTORY.getAttrId())){
            return;
        }
        if (!serverConnections.containsKey(directory.getParentId()))
            return;
        ServerConnection serverConnection = serverConnections.get(directory.getParentId());
        if (directory.isEnabled()) {
            serverConnection.updateDirectory(directory);
        } else {
            serverConnection.removeDirectory(directory);
        }
    }

    private void serverChanged(ObjectChangeEvent objectChangeEvent) {
        Server server = (Server) objectChangeEvent.getObject();
        if(!server.getAttributes().containsKey(Attributes.IP_ADDRESS_OT_SERVER.getAttrId())){
            return;
        }
        if (!server.isEnabled() && serverConnections.containsKey(server.getObjectId())) {
            serverConnections.remove(server.getObjectId());
            removedObjectsCollector.addRemovedObjectId(ObjectTypes.SERVER, server.getObjectId());
        } else if (serverConnections.containsKey(server.getObjectId())) {
            ServerConnection serverConnection = serverConnections.get(server.getObjectId());
            serverConnection.setServer(server);
            serverConnection.disconnect();
        }
    }

    private void savePollResults() {
        removedObjectsCollector.excludeRemovedFromConnections(serverConnections.values());
        List<Server> servers = new ArrayList<>(serverConnections.size());
        List<Directory> directories = new ArrayList<>();
        List<LogFile> logFiles = new ArrayList<>();
        for (ServerConnection serverConnection : serverConnections.values()) {
            servers.add(serverConnection.getServer());
            for (HierarchyContainer directoryContainer : serverConnection.getDirectories()) {
                directories.add((Directory) directoryContainer.getOriginal());
                for (HierarchyContainer logFileContainer : directoryContainer.getChildren()) {
                    logFiles.add((LogFile) logFileContainer.getOriginal());
                }
            }
        }
        containerDAO.saveObjectsAttributesReferences(servers);
        containerDAO.saveObjectsAttributesReferences(directories);
        containerDAO.saveObjectsAttributesReferences(logFiles);
    }

    private void updateActiveServersFromDB() {
        List<HierarchyContainer> serverContainers = containerDAO.getActiveServersWithChildren();
        for (HierarchyContainer serverContainer : serverContainers) {
            Server server = (Server) serverContainer.getOriginal();
            if (serverConnections.containsKey(server.getObjectId())) {
                serverConnections.get(server.getObjectId()).setServer(server);
                serverConnections.get(server.getObjectId()).setDirectories(serverContainer.getChildren());
            } else {
                logger.info("Adding new server to poll: {}", server.getIp());
                ServerConnection serverConnection = serverConnectionService.wrapServerIntoConnection(serverContainer);
                if (serverConnection == null) continue;
                serverConnection.setDirectories(serverContainer.getChildren());
                serverConnections.put(server.getObjectId(), serverConnection);
                logger.info("Server added. Now have {} in a poll", serverConnections.size());
            }
        }
    }

    private void startPoll() {
        Iterator<ServerConnection> serverConnectionIterator = serverConnections.values().iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (serverConnection.getServer().isConnectable() && serverConnection.getServer().isEnabled() && !serverConnection.getDirectories().isEmpty()) {
                serverPollManager.addServerToPoll(serverConnection);
            } else {
                logger.info("Removing server: {} from the poll", serverConnection.getServer().getIp());
                serverConnectionIterator.remove();
                logger.info("Server removed. Now have {} in a poll", serverConnections.size());
            }
        }
    }
}
