package com.netcracker.odstc.logviewer.models.eaventity.mappers;

import com.netcracker.odstc.logviewer.models.eaventity.Attribute;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

public class AttributeMapper implements RowMapper<Map.Entry<BigInteger, Attribute>> {
    @Override
    public Map.Entry<BigInteger,Attribute> mapRow(ResultSet resultSet, int i) throws SQLException {

        Object listValueObject = resultSet.getObject("LIST_VALUE_ID");
        BigInteger listValueId = null;
        if(listValueObject!=null){
            listValueId = resultSet.getBigDecimal("LIST_VALUE_ID").toBigInteger();
        }

        return new AbstractMap.SimpleEntry<>(resultSet.getBigDecimal("ATTR_ID").toBigInteger(),
                new Attribute(resultSet.getString("VALUE"),
                        resultSet.getTimestamp("DATE_VALUE"),
                        listValueId));
    }
}
