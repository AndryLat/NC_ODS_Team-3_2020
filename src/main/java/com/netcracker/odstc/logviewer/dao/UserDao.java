package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends EAVObjectDAO {

    private final Logger logger = LogManager.getLogger(UserDao.class.getName());

    private static final String QUERY_USER_BY_LOGIN = "select ob.object_id\n" +
                "from objects ob, attributes attr\n" +
                "where attr.attr_id = 2 and ob.object_type_id = 1 and attr.object_id = ob.object_id\n" +
                "and attr.value = ? ";
    private static final String QUERY_PAGEABLE_USER_ID = "SELECT object_id FROM OBJECTS WHERE OBJECT_TYPE_ID = 1 OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    public UserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public User getByLogin(String login) {
        User userId;
        try {
            userId = jdbcTemplate.queryForObject(QUERY_USER_BY_LOGIN , new UserMapper(), login);
        } catch (Exception exp) {
            logger.error("User by login not found:",exp);
            return null;
        }
        return getObjectById(userId.getObjectId(), User.class);
    }

    public List<User> getUsers(Pageable page) {
        List<User> usersIds = jdbcTemplate.query(QUERY_PAGEABLE_USER_ID,new UserMapper(),page.getOffset(), page.getPageSize());
        List<User> users = new ArrayList<>();
        for(User id : usersIds){
            User user = getObjectById(id.getObjectId(), User.class);
            users.add(user);
        }
        return users;
    }
}
