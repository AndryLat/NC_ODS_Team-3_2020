package com.netcracker.odstc.logviewer.serverconnection.publishers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DAOPublisher {
    private static DAOPublisher instance;
    private final Logger logger = LogManager.getLogger(DAOPublisher.class.getName());
    private final List<DAOChangeListener> listeners;

    private DAOPublisher() {
        listeners = new ArrayList<>();
    }

    public static DAOPublisher getInstance() {
        if (instance == null) {
            instance = new DAOPublisher();
        }
        return instance;
    }

    public void addListener(DAOChangeListener daoChangeListener) {
        listeners.add(daoChangeListener);
    }

    public void removeListener(DAOChangeListener daoChangeListener) {
        listeners.remove(daoChangeListener);
    }

    public void notifyListeners(ObjectChangeEvent objectChangeEvent) {
        logger.info("Got new event {}, Value: {}", objectChangeEvent.getChangeType(), objectChangeEvent);
        for (DAOChangeListener changeListener :
                listeners) {
            changeListener.objectChanged(objectChangeEvent);
        }
    }
}
