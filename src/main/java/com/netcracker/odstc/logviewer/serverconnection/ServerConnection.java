package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.Server;

import java.util.List;
import java.util.concurrent.Callable;

public interface ServerConnection extends Callable<List<Log>> {
    Server getServer();

    void setServer(Server server);

    List<HierarchyContainer> getDirectories();

    void setDirectories(List<HierarchyContainer> directories);

    void removeDirectory(Directory directory);

    List<LogFile> getLogFilesFromDirectory(Directory directory);

    void updateDirectory(Directory directory);

    void revalidateDirectories();

    boolean connect();

    void disconnect();

    boolean isDirectoryValid(Directory directory);

    List<Log> getNewLogs();
}
