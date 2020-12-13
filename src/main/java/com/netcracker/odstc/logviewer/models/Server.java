package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.lists.Protocol;

import java.math.BigInteger;
import java.util.Date;

public class Server extends EAVObject {


    public Server() {
        super();
    }

    public Server(BigInteger id) {
        super(id);
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
        return Protocol.getByID(Integer.parseInt(getAttributeValue(Attributes.PROTOCOL_OT_SERVER.getAttrId())));
    }

    public void setProtocol(Protocol protocol) {
        setAttributeValue(Attributes.PROTOCOL_OT_SERVER.getAttrId(), String.valueOf(protocol.getValue()));
    }

    public int getPort() {
        return Integer.parseInt(getAttributeValue(Attributes.PORT_OT_SERVER.getAttrId()));
    }

    public void setPort(int port) {
        setAttributeValue(Attributes.PORT_OT_SERVER.getAttrId(), String.valueOf(port));
    }

    public boolean isActive() {
        return Boolean.parseBoolean(getAttributeValue(Attributes.IS_ACTIVE_OT_SERVER.getAttrId()));
    }

    public void setActive(boolean active) {
        setAttributeValue(Attributes.IS_ACTIVE_OT_SERVER.getAttrId(), String.valueOf(active));
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
