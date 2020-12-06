package com.netcracker.odstc.logviewer.serverconnection;

import com.jcraft.jsch.*;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
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
            session.connect();
            server.setActive(session.isConnected());
        } catch (JSchException e) {
            logger.error("Error with connection to {}", server.getName(), e);
            server.setActive(false);
        }
        return server.isActive();
    }

    @Override
    public void disconnect() {
        server.setActive(false);
        session.disconnect();
    }

    @Override
    public void revalidateDirectories() {
        for (Directory directory :
                server.getDirectoryList()) {
            if (!isDirectoryValid(directory)) {
                directory.setActive(false);
            }
        }
    }

    @Override
    public boolean isDirectoryValid(Directory directory) {
        if (!super.isDirectoryValid(directory))
            return false;
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            if (channelSftp.ls(directory.getPath()).isEmpty()) {
                directory.setActive(false);
            }
            return true;
        } catch (JSchException | SftpException e) {
            server.setActive(false);
            return false;
        }
    }

    @Override
    public List<LogFile> getLogFileList(Directory directory, String extension) {
        List<LogFile> logFiles = new LinkedList<>();
        if (session == null) {
            throw new ServerLogProcessingException("Session is not created");
        }
        ChannelSftp channelSftp = null;
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            channelSftp = (ChannelSftp) sftp;
        } catch (JSchException e) {
            logger.error("Error with checking directory ", e);
            return logFiles;
        }
        try {
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(directory.getPath());
            long size = 0;
            for (ChannelSftp.LsEntry file :
                    files) {
                if (file.getFilename().endsWith(extension)) {
                    size += file.getAttrs().getSize();
                    logFiles.add(new LogFile(file.getFilename(), new Date(), 0, directory));
                }
            }
            directory.setSize(size);// А надо ли оно нам?
        } catch (SftpException e) {
            logger.error("Error with working with session", e);
        }
        return logFiles;
    }

    @Override
    public List<Log> getNewLogs() {
        if (session == null) {
            throw new ServerLogProcessingException("Session is not created");
        }
        if (!session.isConnected() && !connect()) {
            throw new ServerLogProcessingException("Cant establish connection");
        }
        List<Log> result = new LinkedList<>();
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect();
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            for (int i = 0; i < server.getDirectoryList().size(); i++) {
                Directory directory = server.getDirectoryList().get(i);
                result.addAll(tryExtractLogsFromDirectory(channelSftp, directory));
            }
            channelSftp.disconnect();
        } catch (JSchException | SftpException e) {
            throw new ServerLogProcessingException(e);
        }
        return result;
    }


    private List<Log> tryExtractLogsFromDirectory(ChannelSftp channelSftp, Directory directory) throws SftpException {
        List<Log> result = new LinkedList<>();
        try {
            channelSftp.cd("/" + directory.getPath());
            if (!directory.isActive()) {
                return result;
            }
            for (int j = 0; j < directory.getLogFileList().size(); j++) {
                LogFile logFile = directory.getLogFileList().get(j);
                result.addAll(tryExtractLogsFromFile(channelSftp, logFile));
            }
        } catch (SftpException e) {
            logger.info("Mark directory {} as unavailable", directory.getPath(), e);
            directory.setActive(false);
        } finally {
            channelSftp.cd("/");//Не работает
        }
        return result;
    }

    private List<Log> tryExtractLogsFromFile(ChannelSftp channelSftp, LogFile logFile) {
        List<Log> result = new LinkedList<>();
        try {
            InputStream inputStream = channelSftp.get(logFile.getName());
            result.addAll(extractLogsFromStream(inputStream, logFile));
        } catch (SftpException e) {
            logger.error("Error with reading file from {}", logFile.getParentTree(), e);
        }
        return result;
    }
}
