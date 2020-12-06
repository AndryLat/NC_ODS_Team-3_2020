package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.LogFile;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogFileMapper implements RowMapper<LogFile> {
    @Override
    public LogFile mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LogFile logFile = new LogFile(BigInteger.valueOf(resultSet.getLong("id")));
        return logFile;
    }
}