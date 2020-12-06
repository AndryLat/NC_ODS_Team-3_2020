package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.constants.Attributes;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class User extends EAVObject {

    private BigInteger id;
    private String email;
    private String login;
    private String password;
    private BigInteger role; // int или enum?
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
        try{
            email = getAttributeValue(Attributes.EMAIL.getAttrId());
            login = getAttributeValue(Attributes.LOGIN.getAttrId());
            password = getAttributeValue(Attributes.PASSWORD.getAttrId());
            role = getAttributeListValueId(Attributes.ROLE.getAttrId()); // int или enum?
            created = jdbcTemplate.queryForObject("select object_id from objReference where reference = ? and attr_id = 5",new UserMapper(),id).id;
        }catch (EAVAttributeException eave){
            log.warning(eave.getMessage());
        }
        this.serverList = new ArrayList<>();
    }

    public BigInteger getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setAttributeValue(Attributes.EMAIL.getAttrId(), email);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
        setAttributeValue(Attributes.LOGIN.getAttrId(), login);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setAttributeValue(Attributes.PASSWORD.getAttrId(), password);
    }

    public BigInteger getRole() {
        return role;
    }

    public void setRole(BigInteger role) {
        this.role = role;
        setAttributeListValueId(Attributes.ROLE.getAttrId(), role);
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
    }

    public boolean addServer(Server server) {
        for (Server s : serverList) {
            if (s.equals(server)) return false;
        }
        serverList.add(server);
        server.setParentUser(this);
        return true;
    }

    @Override
    public void saveToDB() {
        super.saveToDB();
        if(created != null){
            String updateCreated = "MERGE INTO OBJREFERENCE p\n" +
                    "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
                    "   ON (p.reference = p1.reference AND p.attr_id = p1.attr_id)\n" +
                    "   WHEN MATCHED THEN UPDATE SET p.object_id = p1.object_id    \n" +
                    "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
                    "    VALUES (p1.attr_id, p1.object_id,p1.reference)";
            jdbcTemplate.update(updateCreated,
                    created,
                    5,
                    id);
        }
    }
}
