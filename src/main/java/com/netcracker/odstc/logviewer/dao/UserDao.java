package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class UserDao {

    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> users(){
        String sql = "select object_id " +
                "from objects ob " +
                "where ob.object_type_id = 1";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    public User getById(int id){
        User user = new User(BigInteger.valueOf(id));
        return user;
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
