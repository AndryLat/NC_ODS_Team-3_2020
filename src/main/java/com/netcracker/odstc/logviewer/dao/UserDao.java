package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends EAVObjectDAO {

    public UserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public User getByName(String name) {
        String sql = "select ob.object_id\n" +
                "        from objects ob, attributes attr\n" +
                "        where attr.attr_id = 2 and ob.object_type_id = 1 and attr.object_id = ob.object_id\n" +
                "        and attr.value = ? ";
        User userId = jdbcTemplate.queryForObject(sql, new UserMapper(), name);
        User result = new User(getObject(userId.getObjectId()));
        return result;
    }

    public List<User> getAll() {
        String sql = "select object_id " +
                "from objects ob " +
                "where ob.object_type_id = 1";
        List<User> userIds = jdbcTemplate.query(sql, new UserMapper());
        List<User> result = new ArrayList<>();
        for (User userId : userIds) {
            result.add(new User(getObject(userId.getObjectId())));
        }
        return result;
    }

    public void save(User user) {
        saveObject(user);
        saveAttributes(getObjectId(), user.getAttributes());
        saveReferences(getObjectId(), user.getReferences());
    }

    public void update(User user) {
        saveObject(user);
        saveAttributes(user.getObjectId(), user.getAttributes());
        saveReferences(user.getObjectId(), user.getReferences());
    }

    @Override
    public void deleteById(BigInteger id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public User get(BigInteger id) {
        User user = new User(getObject(id));
        return user;
    }
}
