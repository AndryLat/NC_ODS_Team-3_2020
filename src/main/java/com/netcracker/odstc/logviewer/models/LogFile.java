package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;

import java.math.BigInteger;
import java.util.Date;

public class LogFile extends EAVObject {

    public LogFile() {
        super();
        setObjectTypeId(BigInteger.valueOf(4));
    }

    public LogFile(BigInteger id) {
        super(id);
        setObjectTypeId(BigInteger.valueOf(4));
    }

    public LogFile(String name, int lastRow) {
        super();
        setName(name);
        setLastRow(lastRow);
        setLastUpdate(new Date());
    }

    @Override
    public String getName() {
        return getAttributeValue(Attributes.NAME_OT_LOGFILE.getAttrId());
    }

    @Override
    public void setName(String name) {
        setAttributeValue(Attributes.NAME_OT_LOGFILE.getAttrId(), name);
    }

    public Date getLastUpdate() {
        return getAttributeDateValue(Attributes.LAST_UPDATE_OT_LOGFILE.getAttrId());
    }

    public void setLastUpdate(Date lastUpdate) {
        setAttributeDateValue(Attributes.LAST_UPDATE_OT_LOGFILE.getAttrId(), lastUpdate);
    }

    public int getLastRow() {
        return Integer.parseInt(getAttributeValue(Attributes.LAST_ROW_OT_LOGFILE.getAttrId()));
    }

    public void setLastRow(int lastRow) {
        setAttributeValue(Attributes.LAST_ROW_OT_LOGFILE.getAttrId(), String.valueOf(lastRow));
    }
}
