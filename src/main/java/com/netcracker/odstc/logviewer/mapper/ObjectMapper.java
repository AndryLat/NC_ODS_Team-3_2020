package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectMapper implements RowMapper<EAVObject> {
    @Override
    public EAVObject mapRow(ResultSet resultSet, int i) throws SQLException {
        EAVObject eavObject = new EAVObject();

        Object parent = resultSet.getObject("PARENT_ID");
        Object object = resultSet.getObject("OBJECT_ID");
        if (parent == null) {
            eavObject.setParentId(null);
        } else {
            eavObject.setParentId(resultSet.getBigDecimal("PARENT_ID").toBigInteger());
        }
        if (object == null) {
            eavObject.setObjectId(null);
        } else {
            eavObject.setObjectId(resultSet.getBigDecimal("OBJECT_ID").toBigInteger());
        }
        eavObject.setObjectTypeId(resultSet.getBigDecimal("OBJECT_TYPE_ID").toBigInteger());
        eavObject.setName(resultSet.getString("NAME"));

        return eavObject;
    }
}
