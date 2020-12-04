package com.netcracker.odstc.logviewer.models.eaventity.mappers;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author Aleksanid
 * created 04.12.2020
 */
public class ObjectMapper implements RowMapper<List<Object>> {
    @Override
    public List<Object> mapRow(ResultSet resultSet, int i) throws SQLException {
        List<Object> objectColumns = new ArrayList<>();

        Object parent = resultSet.getObject("PARENT_ID");
        if(parent==null){
            objectColumns.add(null);
        }else{
            objectColumns.add(BigInteger.valueOf(resultSet.getLong("PARENT_ID")));
        }
        objectColumns.add(BigInteger.valueOf(resultSet.getLong("OBJECT_TYPE_ID")));
        objectColumns.add(resultSet.getString("NAME"));

        return objectColumns;
    }
}
