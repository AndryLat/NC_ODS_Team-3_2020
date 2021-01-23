package com.netcracker.odstc.logviewer.serverconnection.containers;

import com.netcracker.odstc.logviewer.containers.HierarchyContainer;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.serverconnection.ServerConnection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RemovedObjectsCollector {
    private final Map<ObjectTypes, List<BigInteger>> iterationRemove;

    public RemovedObjectsCollector() {
        iterationRemove = new EnumMap<>(ObjectTypes.class);

        iterationRemove.put(ObjectTypes.SERVER, new ArrayList<>());
        iterationRemove.put(ObjectTypes.DIRECTORY, new ArrayList<>());
        iterationRemove.put(ObjectTypes.LOGFILE, new ArrayList<>());
    }

    public void addRemovedObjectId(ObjectTypes objectType, BigInteger id) {
        iterationRemove.get(objectType).add(id);
    }

    public void excludeRemovedFromConnections(Collection<ServerConnection> connections) {
        for (Iterator<ServerConnection> serverConnectionIterator = connections.iterator(); serverConnectionIterator.hasNext(); ) {
            ServerConnection serverConnection = serverConnectionIterator.next();
            if (iterationRemove.get(ObjectTypes.SERVER).contains(serverConnection.getServer().getObjectId())) {
                serverConnection.disconnect();
                serverConnectionIterator.remove();
                continue;
            }
            for (Iterator<HierarchyContainer> directoryIterator = serverConnection.getDirectories().iterator(); directoryIterator.hasNext(); ) {
                HierarchyContainer directoryContainer = directoryIterator.next();
                if (iterationRemove.get(ObjectTypes.DIRECTORY).contains(directoryContainer.getOriginal().getObjectId())) {
                    directoryIterator.remove();
                    continue;
                }
                for (Iterator<HierarchyContainer> logFileIterator = directoryContainer.getChildren().iterator(); logFileIterator.hasNext(); ) {
                    HierarchyContainer logFileContainer = logFileIterator.next();
                    if (iterationRemove.get(ObjectTypes.LOGFILE).contains(logFileContainer.getOriginal().getObjectId())) {
                        logFileIterator.remove();
                    }
                }
            }
        }
        clearIterationInfo();
    }

    private void clearIterationInfo() {
        iterationRemove.get(ObjectTypes.SERVER).clear();
        iterationRemove.get(ObjectTypes.DIRECTORY).clear();
        iterationRemove.get(ObjectTypes.LOGFILE).clear();
    }
}
