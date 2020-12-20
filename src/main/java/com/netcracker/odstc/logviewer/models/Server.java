package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.exceptions.IllegalServerStateException;
import com.netcracker.odstc.logviewer.models.lists.Protocol;

import java.math.BigInteger;
import java.util.Date;

public class Server extends EAVObject {

    private boolean isOn = true;

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public Server() {
        super();
        setObjectTypeId(BigInteger.TWO);
    }

    public Server(BigInteger id) {
        super(id);
        setObjectTypeId(BigInteger.TWO);
    }

    public Server(String ip, int port, String login, String password, Protocol protocol) {
        super();
        setIp(ip);
        setLogin(login);
        setPassword(password);
        setProtocol(protocol);
        setPort(port);
        setActive(true);
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

    public boolean isActive() {
        switch (getAttributeListValueId(Attributes.IS_ACTIVE_OT_SERVER.getAttrId()).intValue()) {
            case 5:
                return true;
            case 6:
                return false;
            default:
                throw new IllegalServerStateException(String.valueOf(getAttributeListValueId(Attributes.IS_ACTIVE_OT_SERVER.getAttrId())));
        }
    }

    public void setActive(boolean active) {
        if (active) setAttributeListValueId(Attributes.IS_ACTIVE_OT_SERVER.getAttrId(), BigInteger.valueOf(5));
        else setAttributeListValueId(Attributes.IS_ACTIVE_OT_SERVER.getAttrId(), BigInteger.valueOf(6));
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
