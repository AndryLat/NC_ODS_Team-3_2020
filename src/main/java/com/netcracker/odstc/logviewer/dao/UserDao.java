package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDao {

    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> users(){
        String sql = "select ob.object_id id, email.value email, login.value login, password.value pass, role_list.value role, obref.object_id created\n" +
                "from attributes email, attributes login, attributes password, attributes role, lists role_list, objects ob left join objReference obref on obref.reference = ob.object_id and obref.attr_id = 5\n" +
                "where ob.object_type_id = 1\n" +
                "and email.object_id = ob.object_id\n" +
                "and email.attr_id = 1\n" +
                "and login.object_id = ob.object_id\n" +
                "and login.attr_id = 2\n" +
                "and password.object_id = ob.object_id\n" +
                "and password.attr_id = 3\n" +
                "and role.object_id = ob.object_id\n" +
                "and role.attr_id = 4\n" +
                "and role_list.list_value_id = role.list_value_id";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    public User getById(int id){
        String sql = "select ob.object_id id, email.value email, login.value login, password.value pass, role_list.value role, obref.object_id created\n" +
                "from attributes email, attributes login, attributes password, attributes role, lists role_list, objects ob left join objReference obref on obref.reference = ob.object_id and obref.attr_id = 5\n" +
                "where ob.object_id = ? \n" +
                "and email.object_id = ob.object_id\n" +
                "and email.attr_id = 1\n" +
                "and login.object_id = ob.object_id\n" +
                "and login.attr_id = 2\n" +
                "and password.object_id = ob.object_id\n" +
                "and password.attr_id = 3\n" +
                "and role.object_id = ob.object_id\n" +
                "and role.attr_id = 4\n" +
                "and role_list.list_value_id = role.list_value_id";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
    }

    public void addUser(String name, String email, String login, String password, int role, int created){
        String sql = "INSERT ALL\n" +
                "INTO OBJECTS(OBJECT_ID, OBJECT_TYPE_ID, NAME)\n" +
                "VALUES(OBJECT_ID_seq.nextval, 1, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, value)\n" +
                "VALUES(1, OBJECT_ID_seq.currval, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, value)\n" +
                "VALUES(2, OBJECT_ID_seq.currval, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, value)\n" +
                "VALUES(3, OBJECT_ID_seq.currval, ?)\n" +
                "INTO ATTRIBUTES(attr_id, object_id, list_value_id)\n" +
                "VALUES(4, OBJECT_ID_seq.currval, ?)\n" +
                "INTO objreference(attr_id, reference, object_id)\n" +
                "VALUES(5,OBJECT_ID_seq.currval, ?)" +
                "SELECT * FROM dual";
        jdbcTemplate.update(sql, name, email, login, password, role, created);
    }

    public void deleteUser(int id){
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }

}
