package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.Server;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerMapper implements RowMapper<Server> {

    @Override
    public Server mapRow(ResultSet resultSet, int i) throws SQLException {
        Server server = new Server(BigInteger.valueOf(resultSet.getLong("id")));
        return server;
    }
}
