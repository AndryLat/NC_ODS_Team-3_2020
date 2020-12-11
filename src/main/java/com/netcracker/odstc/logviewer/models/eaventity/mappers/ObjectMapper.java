package com.netcracker.odstc.logviewer.models.eaventity.mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapper implements RowMapper<List<Object>> {
    @Override
    public List<Object> mapRow(ResultSet resultSet, int i) throws SQLException {
        List<Object> objectColumns = new ArrayList<>();

        Object parent = resultSet.getObject("PARENT_ID");
        if (parent == null) {
            objectColumns.add(null);
        } else {
            objectColumns.add(resultSet.getBigDecimal("PARENT_ID").toBigInteger());
        }
        objectColumns.add(resultSet.getBigDecimal("OBJECT_TYPE_ID").toBigInteger());
        objectColumns.add(resultSet.getString("NAME"));

        return objectColumns;
    }
}
