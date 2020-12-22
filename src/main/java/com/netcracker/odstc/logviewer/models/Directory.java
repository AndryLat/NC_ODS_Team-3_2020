package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.exceptions.IllegalDirectoryStateException;

import java.math.BigInteger;
import java.util.Date;

public class Directory extends EAVObject {

    public Directory() {
        super();
        setObjectTypeId(BigInteger.valueOf(3));
    }

    public Directory(BigInteger id) {
        super(id);
        setObjectTypeId(BigInteger.valueOf(3));
    }

    public Directory(String path) {
        super();
        setPath(path);
        setEnabled(true);
        setCanConnect(true);
        setLastExistenceCheck(new Date());
        setLastAccessByUser(new Date());
    }

    public String getPath() {
        return (String.valueOf(getAttributeValue(Attributes.PATH_OT_DIRECTORY.getAttrId())));
    }

    public void setPath(String path) {
        setAttributeValue(Attributes.PATH_OT_DIRECTORY.getAttrId(), path);
    }

    public boolean isEnabled() {
        switch (getAttributeListValueId(Attributes.IS_ENABLED_OT_DIRECTORY.getAttrId()).intValue()) {
            case 9:
                return true;
            case 10:
                return false;
            default:
                throw new IllegalDirectoryStateException(String.valueOf(getAttributeListValueId(Attributes.IS_ENABLED_OT_DIRECTORY.getAttrId())));
        }
    }

    public void setEnabled(boolean active) {
        if (active)
            setAttributeListValueId(Attributes.IS_ENABLED_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(9));
        else
            setAttributeListValueId(Attributes.IS_ENABLED_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(10));
    }

    public boolean isCanConnect() {
        switch (getAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_DIRECTORY.getAttrId()).intValue()) {
            case 11:
                return true;
            case 12:
                return false;
            default:
                throw new IllegalDirectoryStateException(String.valueOf(getAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_DIRECTORY.getAttrId())));
        }
    }

    public void setCanConnect(boolean active) {
        if (active)
            setAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(11));
        else
            setAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(12));
    }

    public Date getLastExistenceCheck() {
        return getAttributeDateValue(Attributes.LAST_EXISTENCE_CHECK_OT_DIRECTORY.getAttrId());
    }

    public void setLastExistenceCheck(Date lastExistenceCheck) {
        setAttributeDateValue(Attributes.LAST_EXISTENCE_CHECK_OT_DIRECTORY.getAttrId(), lastExistenceCheck);
    }

    public Date getLastAccessByUser() {
        return getAttributeDateValue(Attributes.LAST_ACCESS_BY_USER_OT_DIRECTORY.getAttrId());
    }

    public void setLastAccessByUser(Date lastAccessByUser) {
        setAttributeDateValue(Attributes.LAST_ACCESS_BY_USER_OT_DIRECTORY.getAttrId(), lastAccessByUser);
    }
}
