package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.models.lists.Role;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class User extends EAVObject {

    private BigInteger id;
    private String email;
    private String login;
    private String password;
    private Role role; // int или enum?
    private List<Server> serverList;
    private BigInteger created;
    private static Logger log = Logger.getLogger(User.class.getName());

    public User() {
        super();
        setObjectTypeId(BigInteger.ONE);
        this.serverList = new ArrayList<>();
    }

    public User(BigInteger id) {
        super(id);
        this.id = id;
        setObjectTypeId(BigInteger.ONE);
        this.serverList = new ArrayList<>();
    }

    public User(EAVObject eavObject) {
        super();
        if (eavObject.getObjectTypeId() != BigInteger.ONE) {
            throw new IllegalArgumentException();
        }
        setObjectId(eavObject.getObjectId());
        this.id = getObjectId();
        setAttributes(eavObject.getAttributes());
        setReferences(eavObject.getReferences());
        try {
            email = getAttributeValue(Attributes.EMAIL_OT_USER.getAttrId());
            login = getAttributeValue(Attributes.LOGIN_OT_USER.getAttrId());
            password = getAttributeValue(Attributes.PASSWORD_OT_USER.getAttrId());
            role = Role.getByID(getAttributeListValueId(Attributes.ROLE_OT_USER.getAttrId()).intValue());
            created = getReference(BigInteger.valueOf(5));
        } catch (EAVAttributeException eave) {
            log.warning(eave.getMessage());
        }
        this.serverList = new ArrayList<>();
    }

    public User(String email, String login, String password, Role role, BigInteger created) {
        this();
        setEmail(email);
        setLogin(login);
        setPassword(password);
        setRole(role);
        setCreated(created);
    }

    public BigInteger getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setAttributeValue(Attributes.EMAIL_OT_USER.getAttrId(), email);
    }

    public String getLogin() {
        if (login != null){
            return login;
        }
        return getAttributeValue(Attributes.LOGIN_OT_USER.getAttrId());
    }

    public void setLogin(String login) {
        this.login = login;
        setAttributeValue(Attributes.LOGIN_OT_USER.getAttrId(), login);
    }

    public String getPassword() {
        if (password != null){
            return password;
        }
        return getAttributeValue(Attributes.PASSWORD_OT_USER.getAttrId());
    }

    public void setPassword(String password) {
        this.password = password;
        setAttributeValue(Attributes.PASSWORD_OT_USER.getAttrId(), password);
    }

    public Role getRole() {
        if (role != null){
            return role;
        }
        return Role.getByID(getAttributeListValueId(Attributes.ROLE_OT_USER.getAttrId()).intValue());
    }

    public void setRole(Role role) {
        this.role = role;
        setAttributeListValueId(Attributes.ROLE_OT_USER.getAttrId(), BigInteger.valueOf(role.getValue()));
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    public BigInteger getCreated() {
        return created;
    }

    public void setCreated(BigInteger created) {
        this.created = created;
        setReference(BigInteger.valueOf(5), created);
    }

    public boolean addServer(Server server) {
        for (Server s : serverList) {
            if (s.equals(server)) return false;
        }
        serverList.add(server);
        server.setParentUser(this);
        return true;
    }
}
