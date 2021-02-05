package com.netcracker.odstc.logviewer.serverconnection.publishers;

public interface DAOChangeListener {
    void objectChanged(ObjectChangeEvent objectChangeEvent);
}
