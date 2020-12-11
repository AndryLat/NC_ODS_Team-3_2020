package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.Log;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogMapper implements RowMapper<Log> {

    @Override
    public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
        Log log = new Log(BigInteger.valueOf(rs.getLong("object_id")));
        return log;
    }

}