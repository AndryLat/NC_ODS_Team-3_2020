package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        User userId;
        try {
            userId = jdbcTemplate.queryForObject(sql, new UserMapper(), name);
        } catch (Exception ex) {
            return null;
        }
        if (userId != null) {
            User result = getObjectById(userId.getObjectId(), User.class);
            return result;
        }
        return null;
    }

    public Integer getUserCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM OBJECTS WHERE OBJECT_TYPE_ID = 1", Integer.class);
    }

    public List<User> getUsers(Pageable page) {
        String sql = "SELECT object_id FROM OBJECTS WHERE OBJECT_TYPE_ID = 1 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<User> usersIds = jdbcTemplate.query(sql,new UserMapper(),page.getOffset(), page.getPageSize());
        List<User> users = new ArrayList<>();
        for(User id : usersIds){
            User user = getObjectById(id.getObjectId(), User.class);
            users.add(user);
        }
        return users;
    }
}
