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
        setActive(true);
        setLastExistenceCheck(new Date());
        setLastAccessByUser(new Date());
    }

    public String getPath() {
        return (String.valueOf(getAttributeValue(Attributes.PATH_OT_DIRECTORY.getAttrId())));
    }

    public void setPath(String path) {
        setAttributeValue(Attributes.PATH_OT_DIRECTORY.getAttrId(), path);
    }

    public boolean isActive() {
        switch (getAttributeListValueId(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId()).intValue()) {
            case 7:
                return true;
            case 8:
                return false;
            default:
                throw new IllegalDirectoryStateException(String.valueOf(getAttributeListValueId(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId())));
        }
    }

    public void setActive(boolean active) {
        if (active) setAttributeListValueId(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(7));
        else setAttributeListValueId(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId(), BigInteger.valueOf(8));
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
