package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.containers.dto.LogDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LogDTOMapper  implements RowMapper<LogDTO> {

    @Override
    public LogDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        LogDTO log = new LogDTO();
        log.setObjectId(resultSet.getBigDecimal("OBJECT_ID").toBigInteger());
        log.setCreationDate(resultSet.getTimestamp("DATE_VALUE"));
        if(resultSet.getBigDecimal("LIST_VALUE_ID")!=null) {
            log.setLevel(resultSet.getBigDecimal("LIST_VALUE_ID").toBigInteger());
        }
        log.setText(resultSet.getString("VALUE"));
        return log;
    }
}
