package com.netcracker.odstc.logviewer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.constants.ObjectTypes;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.models.lists.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

public class User extends EAVObject {

    private final Logger logger = LogManager.getLogger(User.class.getName());

    public User() {
        super();
        setObjectTypeId(ObjectTypes.USER.getObjectTypeID());
    }

    public User(BigInteger id) {
        super(id);
        setObjectTypeId(ObjectTypes.USER.getObjectTypeID());
    }

    public User(String email, String login, String password, Role role, BigInteger created) {
        this();
        setEmail(email);
        setLogin(login);
        setPassword(password);
        setRole(role);
        setCreated(created);
    }

    public String getEmail() {
        try {
            return getAttributeValue(Attributes.EMAIL_OT_USER.getAttrId());
        } catch (EAVAttributeException exp) {
            logger.error("Accessing non existing reference email", exp);
            return null;
        }
    }

    public void setEmail(String email) {
        setAttributeValue(Attributes.EMAIL_OT_USER.getAttrId(), email);
    }

    public String getLogin() {
        return getAttributeValue(Attributes.LOGIN_OT_USER.getAttrId());
    }

    public void setLogin(String login) {
        setAttributeValue(Attributes.LOGIN_OT_USER.getAttrId(), login);
    }

    @JsonIgnore
    public String getPassword() {
        return getAttributeValue(Attributes.PASSWORD_OT_USER.getAttrId());
    }

    @JsonProperty
    public void setPassword(String password) {
        setAttributeValue(Attributes.PASSWORD_OT_USER.getAttrId(), password);
    }

    public Role getRole() {
        return Role.getByID(getAttributeListValueId(Attributes.ROLE_OT_USER.getAttrId()).intValue());
    }

    public void setRole(Role role) {
        setAttributeListValueId(Attributes.ROLE_OT_USER.getAttrId(), BigInteger.valueOf(role.getValue()));
    }

    public BigInteger getCreated() {
        try {
            return getReference(Attributes.CREATED_OT_USER.getAttrId());
        } catch (EAVAttributeException exp) {
            logger.error("Accessing non existing reference created", exp);
            return null;
        }
    }

    public void setCreated(BigInteger created) {
        setReference(Attributes.CREATED_OT_USER.getAttrId(), created);
    }
}
