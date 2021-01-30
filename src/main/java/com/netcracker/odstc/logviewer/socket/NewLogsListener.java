package com.netcracker.odstc.logviewer.socket;

import com.netcracker.odstc.logviewer.models.Log;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOChangeListener;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import com.netcracker.odstc.logviewer.serverconnection.publishers.ObjectChangeEvent;
import org.springframework.stereotype.Component;

@Component
public class NewLogsListener implements DAOChangeListener {

    private final SocketManager socketManager;

    public NewLogsListener(SocketManager socketManager) {
        DAOPublisher.getInstance().addListener(ObjectTypes.LOG, this);
        this.socketManager = socketManager;
    }


    @Override
    public void objectChanged(ObjectChangeEvent objectChangeEvent) {
        if (objectChangeEvent.getChangeType() == ObjectChangeEvent.ChangeType.UPDATE) {
            socketManager.sendNewLog((Log) objectChangeEvent.getObject());
        }
    }
}
