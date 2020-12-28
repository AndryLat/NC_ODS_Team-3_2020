package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.models.exceptions.IllegalServerStateException;
import com.netcracker.odstc.logviewer.models.lists.Protocol;

import java.math.BigInteger;
import java.util.Date;

public class Server extends EAVObject {
    public Server() {
        super();
        setObjectTypeId(ObjectTypes.SERVER.getObjectTypeID());
    }

    public Server(BigInteger id) {
        super(id);
        setObjectTypeId(ObjectTypes.SERVER.getObjectTypeID());
    }

    public Server(String ip, int port, String login, String password, Protocol protocol) {
        super();
        setIp(ip);
        setLogin(login);
        setPassword(password);
        setProtocol(protocol);
        setPort(port);
        setEnabled(true);
        setConnectable(true);
        setLastAccessByUser(new Date());
        setLastAccessByJob(new Date());
    }

    public String getIp() {
        return getAttributeValue(Attributes.IP_ADDRESS_OT_SERVER.getAttrId());
    }

    public void setIp(String ip) {
        setAttributeValue(Attributes.IP_ADDRESS_OT_SERVER.getAttrId(), ip);
    }

    public String getLogin() {
        return getAttributeValue(Attributes.LOGIN_OT_SERVER.getAttrId());
    }

    public void setLogin(String login) {
        setAttributeValue(Attributes.LOGIN_OT_SERVER.getAttrId(), login);
    }

    public String getPassword() {
        return getAttributeValue(Attributes.PASSWORD_OT_SERVER.getAttrId());
    }

    public void setPassword(String password) {
        setAttributeValue(Attributes.PASSWORD_OT_SERVER.getAttrId(), password);
    }

    public Protocol getProtocol() {
        return Protocol.getByID(getAttributeListValueId(Attributes.PROTOCOL_OT_SERVER.getAttrId()).intValue());
    }

    public void setProtocol(Protocol protocol) {
        setAttributeListValueId(Attributes.PROTOCOL_OT_SERVER.getAttrId(), BigInteger.valueOf(protocol.getValue()));
    }

    public int getPort() {
        return Integer.parseInt(getAttributeValue(Attributes.PORT_OT_SERVER.getAttrId()));
    }

    public void setPort(int port) {
        setAttributeValue(Attributes.PORT_OT_SERVER.getAttrId(), String.valueOf(port));
    }

    public boolean isEnabled() {
        switch (getAttributeListValueId(Attributes.IS_ENABLED_OT_SERVER.getAttrId()).intValue()) {
            case 5: //listValueID for true
                return true;
            case 6: //listValueID for false
                return false;
            default:
                throw new IllegalServerStateException(String.valueOf(getAttributeListValueId(Attributes.IS_ENABLED_OT_SERVER.getAttrId())));
        }
    }

    public void setEnabled(boolean active) {
        if (active)
            setAttributeListValueId(Attributes.IS_ENABLED_OT_SERVER.getAttrId(), BigInteger.valueOf(5));
        else
            setAttributeListValueId(Attributes.IS_ENABLED_OT_SERVER.getAttrId(), BigInteger.valueOf(6));
    }

    public boolean isConnectable() {
        switch (getAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_SERVER.getAttrId()).intValue()) {
            case 7: //listValueID for true
                return true;
            case 8: //listValueID for false
                return false;
            default:
                throw new IllegalServerStateException(String.valueOf(getAttributeListValueId(Attributes.IS_ENABLED_OT_SERVER.getAttrId())));
        }
    }

    public void setConnectable(boolean active) {
        if (active)
            setAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_SERVER.getAttrId(), BigInteger.valueOf(7));
        else
            setAttributeListValueId(Attributes.IS_CAN_CONNECT_OT_SERVER.getAttrId(), BigInteger.valueOf(8));
    }

    public Date getLastAccessByJob() {
        return getAttributeDateValue(Attributes.LAST_ACCESS_BY_JOB_OT_SERVER.getAttrId());
    }

    public void setLastAccessByJob(Date lastAccessByJob) {
        setAttributeDateValue(Attributes.LAST_ACCESS_BY_JOB_OT_SERVER.getAttrId(), lastAccessByJob);
    }

    public Date getLastAccessByUser() {
        return getAttributeDateValue(Attributes.LAST_ACCESS_BY_USER_OT_SERVER.getAttrId());
    }

    public void setLastAccessByUser(Date lastAccessByUser) {
        setAttributeDateValue(Attributes.LAST_ACCESS_BY_USER_OT_SERVER.getAttrId(), lastAccessByUser);
    }
}
