package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;

import java.util.Deque;
import java.util.concurrent.Callable;

public interface ServerConnection extends Callable<Deque<Log>> {
    Server getServer();

    boolean connect();

    void disconnect();

    void revalidateDirectories();

    boolean isDirectoryValid(Directory directory);

    Deque<LogFile> getLogFileList(Directory directory, String extension);

    Deque<Log> getNewLogs();
}
