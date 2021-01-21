package com.netcracker.odstc.logviewer.serverconnection.managers;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.dao.ContainerDAO;
import com.netcracker.odstc.logviewer.models.Config;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOChangeListener;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import com.netcracker.odstc.logviewer.serverconnection.publishers.ObjectChangeEvent;
import com.netcracker.odstc.logviewer.serverconnection.services.ServerConnectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@EnableScheduling
public class ServerManager implements DAOChangeListener, SchedulingConfigurer {
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());

    private final ContainerDAO containerDAO;

    private final Map<ObjectTypes, List<BigInteger>> iterationRemove;
    private final Map<BigInteger, ServerConnection> serverConnections;

    private final ServerPollManager serverPollManager;
    private final ServerConnectionService serverConnectionService;

    private final ScheduledExecutorService runnableService;

    @SuppressWarnings({"squid:S1144"})
//Suppress unused private constructor: Spring will use this constructor, even if it private
    private ServerManager(ContainerDAO containerDAO) {
        this.containerDAO = containerDAO;
        DAOPublisher.getInstance().addListener(this, ObjectTypes.DIRECTORY, ObjectTypes.SERVER, ObjectTypes.LOGFILE);

        serverConnectionService = ServerConnectionService.getInstance();
        serverPollManager = ServerPollManager.getInstance();

        serverConnections = new ConcurrentHashMap<>();
        iterationRemove = new EnumMap<>(ObjectTypes.class);

        iterationRemove.put(ObjectTypes.SERVER, new ArrayList<>());
        iterationRemove.put(ObjectTypes.DIRECTORY, new ArrayList<>());
        iterationRemove.put(ObjectTypes.LOGFILE, new ArrayList<>());

        runnableService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void objectChanged(ObjectChangeEvent objectChangeEvent) {
        if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.DELETE) {
            BigInteger objectTypeId = (BigInteger) objectChangeEvent.getArgument();
            BigInteger objectId = (BigInteger) objectChangeEvent.getObject();
            iterationRemove.get(ObjectTypes.getObjectTypesByObjectTypeId(objectTypeId)).add(objectId);
        } else if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.UPDATE) {
            if (Server.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                serverChanged(objectChangeEvent);
            } else if (Directory.class.isAssignableFrom(objectChangeEvent.getObject().getClass())) {
                directoryChanged(objectChangeEvent);
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        logger.info("Starting job runner");
        Config configInstance = containerDAO.getObjectById(BigInteger.ZERO, Config.class);
        Config.setInstance(configInstance);

        scheduledTaskRegistrar.setScheduler(runnableService);
        logger.info("Start job creating");
        logger.info("Starting Poll job");
        scheduledTaskRegistrar.addTriggerTask(this::getLogsFromAllServers, triggerContext -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, (int) configInstance.getChangesPollingPeriod());
            return nextExecutionTime.getTime();
        });
        logger.info("Poll job Started");

        logger.info("Starting Activity validation job");
        scheduledTaskRegistrar.addTriggerTask(this::revalidateServers, triggerContext -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, (int) configInstance.getActivityPollingPeriod());
            return nextExecutionTime.getTime();
        });
        logger.info("Activity validation job started");
        logger.info("Jobs created");
    }

    private void getLogsFromAllServers() {
        List<Log> result = new ArrayList<>(serverPollManager.getLogsFromThreads());
        containerDAO.saveObjectsAttributesReferences(result);
        if (!serverPollManager.getActiveServerConnections().isEmpty()) {
            logger.warn("Skipping job due to previous is not finished");
            return;
        }
        savePollResults();
        updateActiveServersFromDB();
        startPoll();
    }

    private void revalidateServers() {
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
        if (!serverConnections.containsKey(directory.getParentId()))
            return;
        ServerConnection serverConnection = serverConnections.get(directory.getParentId());
        if (directory.isEnabled()) {
            serverConnection.updateDirectory(directory);
        }
    }

    private void serverChanged(ObjectChangeEvent objectChangeEvent) {
        Server server = (Server) objectChangeEvent.getObject();
        if (!server.isEnabled() && serverConnections.containsKey(server.getObjectId())) {
            serverConnections.remove(server.getObjectId());
            iterationRemove.get(ObjectTypes.SERVER).add(server.getObjectId());
        } else if (serverConnections.containsKey(server.getObjectId())) {
            ServerConnection serverConnection = serverConnections.get(server.getObjectId());
            serverConnection.setServer(server);
            serverConnection.disconnect();
        }
    }

    private void savePollResults() {
        excludeRemoved();
        List<Server> servers = new ArrayList<>(serverPollManager.getFinishedServers().size());
        List<Directory> directories = new ArrayList<>();
        List<LogFile> logFiles = new ArrayList<>();
        for (ServerConnection serverConnection : serverPollManager.getFinishedServers().values()) {
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

    private void excludeRemoved() {
        for (Iterator<ServerConnection> serverConnectionIterator = serverPollManager.getFinishedServers().values().iterator(); serverConnectionIterator.hasNext(); ) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (iterationRemove.get(ObjectTypes.SERVER).contains(serverConnection.getServer().getObjectId())) {
                serverConnection.disconnect();
                serverConnectionIterator.remove();
                continue;
            }
            for (Iterator<HierarchyContainer> directoryIterator = serverConnection.getDirectories().iterator(); directoryIterator.hasNext(); ) {
                HierarchyContainer directoryContainer = directoryIterator.next();
                if (iterationRemove.get(ObjectTypes.DIRECTORY).contains(directoryContainer.getOriginal().getObjectId())) {
                    directoryIterator.remove();
                    continue;
                }
                for (Iterator<HierarchyContainer> logFileIterator = directoryContainer.getChildren().iterator(); logFileIterator.hasNext(); ) {
                    HierarchyContainer logFileContainer = logFileIterator.next();
                    if (iterationRemove.get(ObjectTypes.LOGFILE).contains(logFileContainer.getOriginal().getObjectId())) {
                        logFileIterator.remove();
                    }
                }
            }
        }
        clearIterationInfo();
    }

    private void updateActiveServersFromDB() {
        List<HierarchyContainer> serverContainers = containerDAO.getActiveServersWithChildren();
        logger.info("Active Servers in DB: {}", serverContainers.size());
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
            }
        }
        logger.info("Active Servers in Poll: {}", serverConnections.size());
        serverPollManager.getFinishedServers().clear();
    }

    private void startPoll() {
        Iterator<ServerConnection> serverConnectionIterator = serverConnections.values().iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (serverConnection.getServer().isConnectable()) {
                serverPollManager.executeExtractingLogs(serverConnection);
            } else {
                serverConnectionIterator.remove();
            }
        }
    }

    private void clearIterationInfo() {
        iterationRemove.get(ObjectTypes.SERVER).clear();
        iterationRemove.get(ObjectTypes.DIRECTORY).clear();
        iterationRemove.get(ObjectTypes.LOGFILE).clear();
    }
}
