package com.netcracker.odstc.logviewer.serverconnection;

import com.netcracker.odstc.logviewer.models.Directory;
import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.Server;

import java.util.List;

public interface ServerConnection {
    Server getServer();
    boolean connect();
    void disconnect();
    void revalidateDirectories();
    boolean isDirectoryValid(Directory directory);
    List<Log> getNewLogs();
}
