package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;
import com.netcracker.odstc.logviewer.serverconnection.exceptions.ServerLogProcessingException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
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

        logger.debug("Making connection to {}",server.getName());
        try {
            ftpClient.connect(server.getIp(), server.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    logger.error("Error with connect into {}",server.getIp());
                    server.setActive(false);
            }
            server.setActive(ftpClient.login(server.getLogin(), server.getPassword()));
        } catch (IOException e) {
            server.setActive(false);
            logger.error("Error with connect into {}",server.getIp(),e);
        }
        return server.isActive();
    }

    @Override
    public void disconnect() {
        try {
            server.setActive(false);
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.error("Error with disconnect {}",e.getMessage(),e);
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

    @Override
    public boolean isDirectoryValid(Directory directory) {
        if(!super.isDirectoryValid(directory))
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
        try {
            if(ftpClient.listHelp()==null&&!connect()){
                throw new ServerLogProcessingException("Cant establish connection");
            }
        } catch (IOException e) {
            logger.error("Cant get activity check from ftp ",e);
            throw new ServerLogProcessingException("Cant establish connection");
        }
        try {
            for (int i = 0; i < server.getDirectoryList().size(); i++) {
                Directory directory = server.getDirectoryList().get(i);
                result.addAll(tryExtractLogsFromDirectory(directory));
                ftpClient.changeToParentDirectory();
            }
        } catch (IOException e) {
            throw new ServerLogProcessingException(e);
        }
        return result;
    }
    private List<Log> tryExtractLogsFromDirectory(Directory directory){
        List<Log> result = new LinkedList<>();
        try {
            if(!ftpClient.changeWorkingDirectory(directory.getPath())){
                directory.setActive(false);
                return result;
            }
            if (!directory.isActive())
                return result;
            for (int j = 0; j < directory.getLogFileList().size(); j++) {
                LogFile logFile = directory.getLogFileList().get(j);
                result.addAll(tryExtractLogsFromFile(logFile));
            }
        } catch (IOException e) {
            logger.error("Marking directory as unavailable {}",directory.getParentTree(),e);
            directory.setActive(false);
        }
        return result;
    }
    private List<Log> tryExtractLogsFromFile(LogFile logFile){
        List<Log> result = new LinkedList<>();
        try {
            InputStream inputStream = ftpClient.retrieveFileStream(logFile.getName());
            result.addAll(extractLogsFromStream(inputStream, logFile));
            ftpClient.completePendingCommand();
        }catch (IOException e){
            logger.error("Error with reading file from {}",logFile.getParentTree(),e);//TODO: Сделать Error - record
        }
        return result;
    }
}
