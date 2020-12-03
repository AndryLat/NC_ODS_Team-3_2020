package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.User;
import com.netcracker.odstc.logviewer.models.lists.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("pass"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setCreated(rs.getInt("created"));
        return user;
    }
}