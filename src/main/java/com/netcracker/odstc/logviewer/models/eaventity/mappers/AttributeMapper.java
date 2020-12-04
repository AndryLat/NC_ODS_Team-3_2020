package com.netcracker.odstc.logviewer.models.eaventity.mappers;

import com.netcracker.odstc.logviewer.models.eaventity.Attribute;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Description:
 *
 * @author Aleksanid
 * created 03.12.2020
 */
public class AttributeMapper implements RowMapper<Map.Entry<BigInteger, Attribute>> {
    @Override
    public Map.Entry<BigInteger,Attribute> mapRow(ResultSet resultSet, int i) throws SQLException {

        Object listValueId = resultSet.getObject("LIST_VALUE_ID");
        BigInteger listValue = null;
        if(listValueId!=null){
            listValue = BigInteger.valueOf(resultSet.getInt("LIST_VALUE_ID"));
        }

        return new AbstractMap.SimpleEntry<>(BigInteger.valueOf(resultSet.getInt("ATTR_ID")),
                new Attribute(resultSet.getString("VALUE"),
                        resultSet.getDate("DATE_VALUE"),
                        listValue));
    }
}
