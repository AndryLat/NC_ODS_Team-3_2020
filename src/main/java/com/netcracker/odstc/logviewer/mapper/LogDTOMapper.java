package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogDTOMapper  implements RowMapper<LogDTO> {

    @Override
    public LogDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        LogDTO log = new LogDTO();
        log.setObjectId(resultSet.getBigDecimal("id").toBigInteger());
        log.setCreationDate(resultSet.getTimestamp("log_timestamp_value"));
        if(resultSet.getBigDecimal("log_level_value")!=null) {
            log.setLevel(resultSet.getBigDecimal("log_level_value").toBigInteger());
        }
        log.setText(resultSet.getString("fcl_value"));
        return log;
    }
}
