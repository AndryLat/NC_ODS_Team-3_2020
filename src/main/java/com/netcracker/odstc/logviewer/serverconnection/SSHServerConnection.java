package com.netcracker.odstc.logviewer.serverconnection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SSHServerConnection extends AbstractServerConnection {
    private final Logger logger = LogManager.getLogger(SSHServerConnection.class.getName());
    private final JSch jSchClient;
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
    public List<LogFile> getLogFilesFromDirectory(Directory directory) {
        validateConnection();
        List<LogFile> logFiles = new ArrayList<>();
        ChannelSftp channelSftp = getChannelSftp();

        try {
            List files = channelSftp.ls("/" + directory.getPath().replace('\\', '/'));
            for (Object file : files) {
                ChannelSftp.LsEntry sftpFile = ((ChannelSftp.LsEntry) file);
                if (!sftpFile.getAttrs().isDir() && !sftpFile.getAttrs().isLink()) {
                    String fileName = sftpFile.getFilename();

                    LogFile logFile = new LogFile(fileName, 0, directory.getObjectId());
                    logFiles.add(logFile);
                }
            }
        } catch (SftpException e) {
            logger.error("Exception when trying get list of files from {} at {}", directory.getPath(), server.getIp(), e);
            throw new ServerConnectionException("Can't list files from SSH due to error", e);
        }
        return logFiles;
    }

    @Override
    public boolean connect() {
        logger.debug("Making connection to {}", server.getIp());
        try {
            session = jSchClient.getSession(server.getLogin(), server.getIp(), server.getPort());
            session.setPassword(server.getPassword());
            session.setTimeout(CONNECT_TIMEOUT + 10000);
            session.connect();
            server.setConnectable(session.isConnected());
        } catch (JSchException e) {
            logger.error("Error with connection to {}", server.getIp(), e);
            server.setConnectable(false);
            throw new ServerConnectionException(e.getMessage(), e);
        }
        isConnected = server.isConnectable();
        return server.isConnectable();
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
        ChannelSftp channelSftp;
        try {
            channelSftp = getChannelSftp();
        } catch (ServerConnectionException serverConnectionException) {
            logger.error("Error with connection to {} during directory checking", server.getIp());
            server.setConnectable(false);
            return false;
        }
        try {
            channelSftp.cd("/" + directory.getPath().replace('\\', '/'));
            channelSftp.cd("/");
            return true;
        } catch (SftpException e) {
            directory.setConnectable(false);
            return false;
        }
    }

    protected List<Log> collectNewLogs() {
        List<Log> result = new ArrayList<>();
        if (directories.isEmpty()) {
            return result;
        }
        try {
            ChannelSftp channelSftp = getChannelSftp();
            for (int i = 0; i < directories.size(); i++) {
                HierarchyContainer directory = directories.get(i);
                result.addAll(extractLogsFromDirectory(channelSftp, directory));
            }
            channelSftp.disconnect();
        } catch (SftpException e) {
            throw new ServerConnectionException("Server connection problem in collectNewLogs", e);
        }
        return result;
    }

    private ChannelSftp getChannelSftp() {
        ChannelSftp channelSftp;
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            channelSftp = (ChannelSftp) sftp;
        } catch (JSchException e) {
            logger.error("Error with creating SFTP on {}", server.getIp(), e);
            throw new ServerConnectionException("Error with creating SFTP ", e);
        }
        return channelSftp;
    }

    private List<Log> extractLogsFromDirectory(ChannelSftp channelSftp, HierarchyContainer directoryContainer) throws SftpException {
        Directory directory = (Directory) directoryContainer.getOriginal();
        List<Log> result = new ArrayList<>();
        try {
            channelSftp.cd("/" + directory.getPath().replace('\\', '/'));
            for (int i = 0; i < directoryContainer.getChildren().size(); i++) {
                LogFile logFile = (LogFile) directoryContainer.getChildren().get(i).getOriginal();
                result.addAll(extractLogsFromFile(channelSftp, logFile));
            }
            channelSftp.cd("/");
        } catch (SftpException e) {
            logger.error("Marking directory {} from {} as unavailable", directory.getPath(), server.getIp(), e);
            directory.setConnectable(false);
        }
        validateDirectoryByLogCollectionResult(result, directory);
        return result;
    }

    private List<Log> extractLogsFromFile(ChannelSftp channelSftp, LogFile logFile) {
        logFile.setLastUpdate(new Date());
        List<Log> result = new ArrayList<>();
        try {
            try (InputStream inputStream = channelSftp.get(logFile.getFileName())) {
                if (inputStream == null) {
                    logger.error("Can't reach file {} from {}", logFile.getFileName(), server.getIp());
                } else {
                    result.addAll(extractLogsFromStream(inputStream, logFile));
                }
            }
        } catch (SftpException | IOException e) {
            logger.error("Error with read file {} from {}", logFile.getFileName(), server.getIp(), e);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
