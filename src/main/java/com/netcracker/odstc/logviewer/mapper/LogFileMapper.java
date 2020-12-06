package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.LogFile;
import com.netcracker.odstc.logviewer.models.lists.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogFileMapper  implements RowMapper<LogFile> {
    @Override
    public LogFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        LogFile logFile = new LogFile();
        logFile.setId(rs.getInt("id"));
        logFile.setName(rs.getString("NAME"));
        logFile.setLastUpdate(rs.getDate("LAST_CHECK"));
        logFile.setLastRow(rs.getInt("LAST_ROW"));
        return logFile;
    }
}