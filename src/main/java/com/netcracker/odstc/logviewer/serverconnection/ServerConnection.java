package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;

import java.util.List;
import java.util.concurrent.Callable;

public interface ServerConnection extends Callable<List<Log>> {
    Server getServer();

    boolean connect();

    void disconnect();

    void revalidateDirectories();

    boolean isDirectoryValid(Directory directory);

    List<LogFile> getLogFileList(Directory directory, String extension);

    List<Log> getNewLogs();
}
