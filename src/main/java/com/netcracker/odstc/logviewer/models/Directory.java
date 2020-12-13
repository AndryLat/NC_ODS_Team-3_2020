package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;

import java.math.BigInteger;
import java.util.Date;

public class Directory extends EAVObject {

    public Directory() {
        super();
    }

    public Directory(BigInteger id) {
        super(id);
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
        return Boolean.parseBoolean(getAttributeValue(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId()));
    }

    public void setActive(boolean active) {
        setAttributeValue(getAttributeListValueId(Attributes.IS_ACTIVE_OT_DIRECTORY.getAttrId()), String.valueOf(active));
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
