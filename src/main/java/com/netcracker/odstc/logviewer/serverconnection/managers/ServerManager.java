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
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component//Я так понимаю он станет синглтоном, если у него есть анотация бина?
public class ServerManager implements PropertyChangeListener {
    private final ContainerDAO containerDAO;
    private Map<BigInteger,List<BigInteger>> iterationRemove;//ObjectType,ObjectId
    private final Logger logger = LogManager.getLogger(ServerManager.class.getName());

    private Map<BigInteger, ServerConnection> serverConnections;

    private ServerPollManager serverPollManager = ServerPollManager.getInstance();

    private ServerManager(ContainerDAO containerDAO) {//СонарЛинт ругается, но этот конструктор используется Спрингом.
        DAOPublisher.getInstance().addListener(this);
        serverConnections = new HashMap<>();
        this.containerDAO = containerDAO;
        iterationRemove = new HashMap<>();
    }

    //TODO: Проблемы: Невозможно изменить что либо если оно в активной выборке.---Publisher-Listener
    //TODO: Проблемы: Невозможно узнать какие сервера были на прошлом этапе, приходится всегда подключатся с нуля.---Запоминать сервера
    //TODO: Вопрос: Что если, сервер не успел предоставить логи за одну итерацию.---Забить---Или остановить.

    //Publisher-Listener.

    public void getLogsFromAllServers() {
        // Сохраняю результат итерации
        saveResults();
        // Запрашиваю из базы текущее состояние... Сам же записав его на предыдущем этапе. Интересно.
        revalidateCondition();
        // Запуск итерации
        startPoll();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("DELETE")){
            BigInteger objectTypeId = (BigInteger) evt.getNewValue();
            BigInteger objectId = (BigInteger) evt.getOldValue();
            iterationRemove.get(objectTypeId).add(objectId);
        }
        if(evt.getPropertyName().equals("UPDATE")){
            if(Server.class.isAssignableFrom(evt.getNewValue().getClass())){
                Server server = (Server) evt.getNewValue();
                serverConnections.get(server.getObjectId()).setServer(server);
            }
        }
    }

    private void startPoll() {
        Iterator<ServerConnection> serverConnectionIterator = serverConnections.values().iterator();
        while (serverConnectionIterator.hasNext()) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (serverConnection.getServer().isActive()) {
                serverPollManager.executeExtractingLogs(serverConnection);
            } else {
                serverConnectionIterator.remove();
            }
        }
    }

    private void revalidateCondition() {
        List<HierarchyContainer> serverContainers = containerDAO.getActiveElements();
        logger.info("Active Servers: {}", serverContainers.size());
        //Планирую новый опрос
        for (HierarchyContainer serverHierarchyContainer : serverContainers) {
            Server server = (Server) serverHierarchyContainer.getOriginal();
            if (serverConnections.containsKey(server.getObjectId())) {
                serverConnections.get(server.getObjectId()).setServer(server);// Если сервер уже есть, обновить ему данные о сервере.
                serverConnections.get(server.getObjectId()).setDirectories(serverHierarchyContainer.getChildren());//Если сервер уже есть то обновить ему директории(файлы заодно)
                continue;
            }
            ServerConnection serverConnection;
            if (server.getProtocol() == Protocol.FTP) {
                serverConnection = new FTPServerConnection(server);
            } else if (server.getProtocol() == Protocol.SSH) {
                serverConnection = new SSHServerConnection(server);
            } else {
                logger.error("Cant wrap server with unknown protocol");
                continue;
            }
            serverConnection.setDirectories(serverHierarchyContainer.getChildren());
            serverConnections.put(server.getObjectId(), serverConnection);
        }
    }

    private void saveResults() {
        //Получаю предыдущие результаты.
        List<Log> result = new ArrayList<>(serverPollManager.getLogsFromThreads());
        serverPollManager.getFinishedServers();//TODO: Зачистить менеджер потоков
        //Сохраняю новые логи
        //Сохряняю состояния серверов,директорий, файлов.
        List<Server> servers = new ArrayList<>(serverConnections.size());
        List<Directory> directories = new ArrayList<>();
        List<LogFile> logFiles = new ArrayList<>();
        //Сервера
        for (ServerConnection serverConnection : serverConnections.values()) {
            if(iterationRemove.get(BigInteger.valueOf(2)).contains(serverConnection.getServer().getObjectId())) {
                continue;
            }
            servers.add(serverConnection.getServer());
            //Директории
            for (HierarchyContainer directoryContainer : serverConnection.getDirectories()) {
                if(iterationRemove.get(BigInteger.valueOf(3)).contains(directoryContainer.getOriginal().getObjectId())) {
                    continue;
                }
                directories.add((Directory) directoryContainer.getOriginal());
                //Файлы
                for (HierarchyContainer logFileContainer : directoryContainer.getChildren()) {
                    if(!iterationRemove.get(BigInteger.valueOf(4)).contains(logFileContainer.getOriginal().getObjectId())) {
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
        iterationRemove.clear();
        iterationRemove.put(BigInteger.valueOf(2),new ArrayList<>());
        iterationRemove.put(BigInteger.valueOf(3),new ArrayList<>());
        iterationRemove.put(BigInteger.valueOf(4),new ArrayList<>());
    }
}
