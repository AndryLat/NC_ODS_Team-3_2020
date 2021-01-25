package com.netcracker.odstc.logviewer.serverconnection.publishers;

import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DAOPublisher {
    private static DAOPublisher instance;
    private final Logger logger = LogManager.getLogger(DAOPublisher.class.getName());
    private final Map<ObjectTypes, List<DAOChangeListener>> listeners;

    private final List<Class> ignoredClasses;

    private DAOPublisher() {
        listeners = new EnumMap<>(ObjectTypes.class);
        ignoredClasses = new ArrayList<>();
    }

    public static DAOPublisher getInstance() {
        if (instance == null) {
            instance = new DAOPublisher();
        }
        return instance;
    }

    public void addListener(ObjectTypes objectType, DAOChangeListener daoChangeListener) {
        if (!listeners.containsKey(objectType)) {
            listeners.put(objectType, new ArrayList<>());
        }
        listeners.get(objectType).add(daoChangeListener);
    }

    public void addListener(DAOChangeListener daoChangeListener, ObjectTypes... objectTypeIds) {
        for (ObjectTypes objectTypeId : objectTypeIds) {
            if (!listeners.containsKey(objectTypeId)) {
                listeners.put(objectTypeId, new ArrayList<>());
            }
            listeners.get(objectTypeId).add(daoChangeListener);
        }
    }

    public void removeListener(ObjectTypes objectType, DAOChangeListener daoChangeListener) {
        if (listeners.containsKey(objectType)) {
            listeners.get(objectType).remove(daoChangeListener);
        }
    }

    public void notifyListeners(ObjectChangeEvent objectChangeEvent, ObjectTypes objectType) {
        logger.info("Got new event {}, Value: {}", objectChangeEvent.getChangeType(), objectChangeEvent);
        for (Class clazz : ignoredClasses) {
            if (clazz.isAssignableFrom(objectChangeEvent.getSource().getClass())) {
                logger.info("Ignoring event from {} with event {}", clazz.getName(), objectChangeEvent);
                return;
            }
        }
        if (listeners.containsKey(objectType)) {
            for (DAOChangeListener changeListener : listeners.get(objectType)) {
                changeListener.objectChanged(objectChangeEvent);
            }
        }
    }

    public void addIgnoredClass(Class clazz) {
        ignoredClasses.add(clazz);
    }

    public void removeIgnoredClass(Class clazz) {
        ignoredClasses.remove(clazz);
    }
}
