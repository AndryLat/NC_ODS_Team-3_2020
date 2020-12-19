package com.netcracker.odstc.logviewer.serverconnection;

import com.jcraft.jsch.*;
import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;

public class SSHServerConnection extends AbstractServerConnection {
    private final Logger logger = LogManager.getLogger(SSHServerConnection.class.getName());
    private JSch jSchClient;
    private Session session;

    public SSHServerConnection(Server server) {
        super(server);
        jSchClient = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        JSch.setConfig(config);
    }

    @Override
    public boolean connect() {
        logger.debug("Making connection to {}", server.getName());
        try {
            session = jSchClient.getSession(server.getLogin(), server.getIp(), server.getPort());
            session.setPassword(server.getPassword());
            session.setTimeout(500);
            session.connect();
            server.setActive(session.isConnected());
        } catch (JSchException e) {
            logger.error("Error with connection to {}", server.getName(), e);
            server.setActive(false);
            throw new ServerConnectionException(e.getMessage(),e);
        }
        isConnected = server.isActive();
        return server.isActive();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        session.disconnect();
    }

    @Override
    public boolean isDirectoryValid(Directory directory) {
        if (!super.isDirectoryValid(directory))
            return false;
        try {
            ChannelSftp channelSftp = getChannelSftp();
            if (channelSftp.ls(directory.getPath()).isEmpty()) {
                directory.setActive(false);
            }
            return true;
        } catch (SftpException e) {
            server.setActive(false);
            return false;
        }
    }

    @Override
    public List<Log> getNewLogs() {
        server.setLastAccessByJob(new Date());
        validateConnection();
        return collectNewLogs();
    }

    private ChannelSftp getChannelSftp() {
        ChannelSftp channelSftp;
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            channelSftp = (ChannelSftp) sftp;
        } catch (JSchException e) {
            logger.error("Error with creating SFTP", e);
            throw new ServerConnectionException("Error with creating SFTP ", e);
        }
        return channelSftp;
    }

    private List<Log> collectNewLogs() {
        List<Log> result = new ArrayList<>();
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            for (int i = 0; i < directories.size(); i++) {
                HierarchyContainer directory = directories.get(i);
                result.addAll(tryExtractLogsFromDirectory(channelSftp, directory));
            }
            channelSftp.disconnect();
        } catch (JSchException | SftpException e) {
            throw new ServerConnectionException("Error in polling time", e);
        }
        return result;
    }

    private void validateConnection() {
        if ((!isConnected || session == null) && !connect()) {
            throw new ServerConnectionException("Cant establish connection");
        }
    }

    private List<Log> tryExtractLogsFromDirectory(ChannelSftp channelSftp, HierarchyContainer directoryContainer) throws SftpException {
        Directory directory = (Directory) directoryContainer.getOriginal();
        List<Log> result = new ArrayList<>();
        directory.setLastExistenceCheck(new Date());
        try {
            channelSftp.cd("/" + directory.getPath());
            for (int j = 0; j < directoryContainer.getChildren().size(); j++) {
                LogFile logFile = (LogFile) directoryContainer.getChildren().get(j).getOriginal();
                result.addAll(tryExtractLogsFromFile(channelSftp, logFile));
            }
        } catch (SftpException e) {
            logger.info("Mark directory {} as unavailable",directory.getName(), e);
            directory.setActive(false);
        }
        channelSftp.cd("/");
        return result;
    }

    private List<Log> tryExtractLogsFromFile(ChannelSftp channelSftp, LogFile logFile) {
        logFile.setLastUpdate(new Date());
        List<Log> result = new ArrayList<>();
        try {
            InputStream inputStream = channelSftp.get(logFile.getName());
            result.addAll(extractLogsFromStream(inputStream, logFile));
        } catch (SftpException e) {
            logger.error("Error with reading file", e);
        }
        return result;
    }
}
