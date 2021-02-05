package com.netcracker.odstc.logviewer.mapper;

import com.netcracker.odstc.logviewer.containers.AttributeObjectContainer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AttributeObjectMapper implements RowMapper<AttributeObjectContainer> {
    @Override
    public AttributeObjectContainer mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        AttributeObjectContainer attributeObjectContainer = new AttributeObjectContainer();

        attributeObjectContainer.setObjectId(resultSet.getBigDecimal("OBJECT_ID").toBigInteger());
        attributeObjectContainer.setObjectTypeId(resultSet.getBigDecimal("OBJECT_TYPE_ID").toBigInteger());

        Object parent = resultSet.getObject("PARENT_ID");
        if (parent != null) {
            attributeObjectContainer.setParentId(resultSet.getBigDecimal("PARENT_ID").toBigInteger());
        }

        attributeObjectContainer.setName(resultSet.getString("NAME"));

        Object listValueObject = resultSet.getObject("LIST_VALUE_ID");
        if (listValueObject != null) {
            attributeObjectContainer.setListValueId(resultSet.getBigDecimal("LIST_VALUE_ID").toBigInteger());
        }
        attributeObjectContainer.setAttrId(resultSet.getBigDecimal("ATTR_ID").toBigInteger());
        attributeObjectContainer.setValue(resultSet.getString("VALUE"));
        attributeObjectContainer.setDateValue(resultSet.getTimestamp("DATE_VALUE"));

        return attributeObjectContainer;
    }

}
