package com.netcracker.odstc.logviewer.serverconnection.publishers;

public class ObjectChangeEvent {
    private ChangeType changeType;
    private Object source;
    private Object object;
    private Object argument;

    public ObjectChangeEvent(ChangeType changeType, Object source, Object object, Object argument) {
        this.changeType = changeType;
        this.source = source;
        this.object = object;
        this.argument = argument;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getArgument() {
        return argument;
    }

    public void setArgument(Object argument) {
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "ObjectChangeEvent{" +
                "changeType=" + changeType +
                ", source=" + source +
                ", object=" + object +
                ", argument=" + argument +
                '}';
    }

    public enum ChangeType {
        DELETE,
        UPDATE
    }
}
