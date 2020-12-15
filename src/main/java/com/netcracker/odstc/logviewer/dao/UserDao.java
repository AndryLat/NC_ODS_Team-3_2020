package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        if(userId != null){
            User result = getObjectById(userId.getObjectId(), User.class);
            return result;
        }
        return null;
    }

    public void save(User user) {
        saveObject(user);
        saveAttributes(user.getObjectId(), user.getAttributes());
        saveReferences(user.getObjectId(), user.getReferences());
    }
}
