package com.netcracker.odstc.logviewer.serverconnection;

import com.jcraft.jsch.*;
import com.netcracker.odstc.logviewer.LogClass;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
import org.apache.logging.log4j.Level;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Description:
 *
 * @author Aleksanid
 * created 01.12.2020
 */
public class SSHServerConnection extends AbstractServerConnection {
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
        LogClass.log(Level.DEBUG, "Making connection to " + server.getName());
        try {
            session = jSchClient.getSession(server.getLogin(), server.getIp(), server.getPort());
            session.setPassword(server.getPassword());
            session.setTimeout(500);
            session.connect();
            return session.isConnected();
        } catch (JSchException e) {
            LogClass.log(Level.ERROR, "Error with connection to " + server.getName());
            server.setActive(false);
        }
        return false;
    }

    @Override
    public void disconnect() {
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
        try {
            Channel sftp = session.openChannel("sftp");
            sftp.connect(500);

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
    public List<Log> getNewLogs() {
        if (session == null) {
            throw new ServerLogProcessingException("Session is not created");
        }
        if(!session.isConnected()&&!connect()){
            throw new ServerLogProcessingException("Cant establish connection");
        }
        List<Log> result = new LinkedList<>();
        try {
            Channel sftp = session.openChannel("sftp");

            // TODO:Тайм аут
            sftp.connect(500);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            for (int i = 0; i < server.getDirectoryList().size(); i++) {
                Directory directory = server.getDirectoryList().get(i);
                try {
                    channelSftp.cd("/" + directory.getPath());
                    if (!directory.isActive()) {
                        continue;
                    }
                    for (int j = 0; j < directory.getLogFileList().size(); j++) {
                        LogFile logFile = directory.getLogFileList().get(j);
                        try {
                            InputStream inputStream = channelSftp.get(logFile.getName());//TODO: Игнорировать если файл не доступен или помечать диркторию?
                            result.addAll(extractLogsFromStream(inputStream, logFile));
                        }catch (SftpException e){
                            LogClass.log(Level.ERROR,"Error with reading file from "+logFile.getParentTree());//TODO: Заменить на новый метод
                        }
                    }
                } catch (SftpException e) {
                    LogClass.log(Level.INFO, "Mark directory " + directory.getPath() + " as unavilable");
                    directory.setActive(false);
                } finally {
                    channelSftp.cd("/");//Не работает
                }
            }
        } catch (JSchException | SftpException e) {
            throw new ServerLogProcessingException(e);
        }
        return result;
    }
}
