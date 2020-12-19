package com.netcracker.odstc.logviewer.serverconnection.publishers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class DAOPublisher {
    private static DAOPublisher instance;
    private final Logger logger = LogManager.getLogger(DAOPublisher.class.getName());
    private final List<PropertyChangeListener> listeners;

    private DAOPublisher() {
        listeners = new ArrayList<>();
    }

    public static DAOPublisher getInstance() {
        if (instance == null) {
            instance = new DAOPublisher();
        }
        return instance;
    }

    public void addListener(PropertyChangeListener propertyChangeListener) {
        listeners.add(propertyChangeListener);
    }

    public void removeListener(PropertyChangeListener propertyChangeListener) {
        listeners.remove(propertyChangeListener);
    }

    public void notifyListeners(PropertyChangeEvent propertyChangeEvent) {
        logger.info("Got new event {}, Value: {}", propertyChangeEvent.getPropertyName(), propertyChangeEvent);
        for (PropertyChangeListener pcl :
                listeners) {
            pcl.propertyChange(propertyChangeEvent);
        }
    }
}
