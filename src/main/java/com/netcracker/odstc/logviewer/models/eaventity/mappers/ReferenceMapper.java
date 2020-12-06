package com.netcracker.odstc.logviewer.models.eaventity.mappers;

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
 * created 04.12.2020
 */
public class ReferenceMapper implements RowMapper<Map.Entry<BigInteger,BigInteger>> {
    @Override
    public Map.Entry<BigInteger, BigInteger> mapRow(ResultSet resultSet, int i) throws SQLException {
        return new AbstractMap.SimpleEntry<BigInteger, BigInteger>(
                BigInteger.valueOf(resultSet.getLong("ATTR_ID")),
                BigInteger.valueOf(resultSet.getLong("OBJECT_ID")));
    }
}
