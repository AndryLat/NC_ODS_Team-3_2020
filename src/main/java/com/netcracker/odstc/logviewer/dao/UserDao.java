package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.UserMapper;
import com.netcracker.odstc.logviewer.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends EAVObjectDAO {
    private final Logger logger = LogManager.getLogger(UserDao.class);
    private static final String QUERY_USER_BY_LOGIN = "select ob.object_id\n" +
            "from objects ob, attributes attr\n" +
            "where attr.attr_id = 2 and ob.object_type_id = 1 and attr.object_id = ob.object_id\n" +
            "and attr.value = ? ";

    public UserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public User getByLogin(String login) {
        User userId;
        try {
            userId = jdbcTemplate.queryForObject(QUERY_USER_BY_LOGIN, new UserMapper(), login);
        } catch (EmptyResultDataAccessException exp) {
            logger.error("User by login not found:", exp);
            return null;
        }
        if (userId != null) {
            return getObjectById(userId.getObjectId(), User.class);
        }
        return null;
    }
}
