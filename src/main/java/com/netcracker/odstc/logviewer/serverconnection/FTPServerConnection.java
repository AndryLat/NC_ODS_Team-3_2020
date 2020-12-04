package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.*;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FTPServerConnection extends AbstractServerConnection {

    private final Logger logger = LogManager.getLogger(FTPServerConnection.class.getName());

    FTPClient ftpClient;
    FTPServerConnection(Server server){
        super(server);
        ftpClient = new FTPClient();
    }
    @Override
    public boolean connect() {

        logger.debug("Making connection to "+server.getName());
        try {
            ftpClient.connect(server.getIp(), server.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    logger.error("Error with connect into "+server.getIp());
                return false;
            }
            return ftpClient.login(server.getLogin(), server.getPassword());
        } catch (IOException e) {
            logger.error("Error with connect into "+server.getIp());
        }
        return false;
    }

    @Override
    public void disconnect() {
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.error("Error with disconnect "+e.getMessage());
        }
    }

    @Override
    public void revalidateDirectories() {
        for (Directory directory :
                server.getDirectoryList()) {
            if(!isDirectoryValid(directory)){
                directory.setActive(false);
            }
        }
    }

    //TODO: Последний доступ юзером?
    @Override
    public boolean isDirectoryValid(Directory directory) {
        Config appConfiguration = Config.getInstance();
        if(new Date(directory.getLastAccessByUser().getTime()+appConfiguration.getDirectoryActivityPeriod().getTime()).before(new Date()))
            return false;
        try {
            boolean isActive = ftpClient.changeWorkingDirectory(directory.getPath());
            if(isActive)
                ftpClient.changeToParentDirectory();
            return isActive;
        } catch (IOException e) {
            server.setActive(false);
            return false;
        }
    }

    @Override
    public List<Log> getNewLogs() {
        List<Log> result = new LinkedList<>();
        if(!ftpClient.isConnected()&&!connect()){
            throw new ServerLogProcessingException("Cant establish connection");
        }
        try {
            for (int i = 0; i < server.getDirectoryList().size(); i++) {
                Directory directory = server.getDirectoryList().get(i);
                try {
                    if(!ftpClient.changeWorkingDirectory(directory.getPath())){
                        directory.setActive(false);
                        continue;
                    }
                    if (!directory.isActive())
                        continue;
                    for (int j = 0; j < directory.getLogFileList().size(); j++) {
                        LogFile logFile = directory.getLogFileList().get(j);
                        try {
                            InputStream inputStream = ftpClient.retrieveFileStream(logFile.getName());
                            result.addAll(extractLogsFromStream(inputStream, logFile));
                            ftpClient.completePendingCommand();
                        }catch (IOException e){
                            logger.error("Error with reading file from "+logFile.getParentTree(),e);//TODO: Сделать Error - record
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error with reading file from "+directory.getParentTree(),e);
                    directory.setActive(false);
                }
                ftpClient.changeToParentDirectory();
            }
        } catch (IOException e) {
            throw new ServerLogProcessingException(e);
        }
        return result;
    }
}
