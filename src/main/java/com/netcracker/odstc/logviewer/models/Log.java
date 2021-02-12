package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.models.lists.LogLevel;

import java.math.BigInteger;
import java.util.Date;

public class Log extends EAVObject {

    public Log() {
        super();
        setObjectTypeId(ObjectTypes.LOG.getObjectTypeID());
    }

    public Log(BigInteger id) {
        super(id);
        setObjectTypeId(BigInteger.valueOf(5));
    }

    public Log(String text, LogLevel level) {
        this();
        setText(text);
        setLevel(level);
    }

    public Log(String text, LogLevel level, Date creationDate, BigInteger parentId) {
        this(text, level);
        setCreationDate(creationDate);
        setParentId(parentId);
    }

    public String getText() {
        return getAttributeValue(Attributes.TEXT_OT_LOG.getAttrId());
    }

    public void setText(String text) {
        setAttributeValue(Attributes.TEXT_OT_LOG.getAttrId(), text);
    }

    public LogLevel getLevel() {
        if (getAttributeListValueId(Attributes.LEVEL_OT_LOG.getAttrId()) == null) {
            return null;
        }
        return LogLevel.getByID(getAttributeListValueId(Attributes.LEVEL_OT_LOG.getAttrId()).intValue());
    }

    public void setLevel(LogLevel level) {
        setAttributeListValueId(Attributes.LEVEL_OT_LOG.getAttrId(), level == null ? null : new BigInteger(String.valueOf(level.getValue())));
    }

    public Date getCreationDate() {
        return getAttributeDateValue(Attributes.CREATION_DATE_OT_LOG.getAttrId());
    }

    public void setCreationDate(Date creationDate) {
        setAttributeDateValue(Attributes.CREATION_DATE_OT_LOG.getAttrId(), creationDate);
    }
}
