package com.netcracker.odstc.logviewer.models.eaventity;

import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.AttributeMapper;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.ObjectMapper;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.ReferenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author Aleksanid
 * created 03.12.2020
 */
@Component // Анотация
public class EAVObject {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private BigInteger objectId;
    private BigInteger parentId;
    private BigInteger objectTypeId;
    private String name;
    private Map<BigInteger, Attribute> attributes;// Чтоб эти БигИнтеджеры
    private Map<BigInteger, BigInteger> references;

    public EAVObject(){
        attributes = new HashMap<>();
        references = new HashMap<>();
    }

    public EAVObject(BigInteger objectId) {
        this.objectId = objectId;
        attributes = new HashMap<>();
        references = new HashMap<>();

        List<Object> columns = jdbcTemplate.queryForObject("SELECT NAME,PARENT_ID,OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?", new ObjectMapper(), objectId);
        if(columns==null||columns.size()<3){
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }

        parentId = (BigInteger) columns.remove(0);
        objectTypeId = (BigInteger) columns.remove(0);
        name = (String) columns.remove(0);

        List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query("SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ?",
                new AttributeMapper(),
                objectId);

        for (Map.Entry<BigInteger, Attribute> attribute :
                objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }

        List<Map.Entry<BigInteger, BigInteger>> objectReferences = jdbcTemplate.query("SELECT ATTR_ID,REFERENCE FROM OBJREFERENCE WHERE object_id = ?",
                new ReferenceMapper(),
                objectId);

        for (Map.Entry<BigInteger, BigInteger> attribute :
                objectReferences) {
            references.put(attribute.getKey(), attribute.getValue());
        }
    }

    public EAVObject(BigInteger objectId, BigInteger... attrIds) {// А референсы как?
        this.objectId = objectId;
        attributes = new HashMap<>();
        references = new HashMap<>();


        List<Object> columns = jdbcTemplate.queryForObject("SELECT NAME,PARENT_ID,OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?", new ObjectMapper(), objectId);

        if(columns==null||columns.size()<3){
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }
        parentId = (BigInteger) columns.remove(0);
        objectTypeId = (BigInteger) columns.remove(0);
        name = (String) columns.remove(0);


        StringBuilder sqlStatement = new StringBuilder("SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ? AND ATTR_ID = " + attrIds[0] + " ");// Или лучше много запросов?
        for (int i = 1; i < attrIds.length; i++) {
            sqlStatement.append("OR ATTR_ID = ").append(attrIds[i]).append(" ");
        }// Заменить на PreparedStatement
        // Добавить запрос на References

        List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query(sqlStatement.toString(),
                new AttributeMapper(),
                objectId);

        for (Map.Entry<BigInteger, Attribute> attribute :
                objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
    }

    public void setAttributeValue(BigInteger attrId, String value) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setValue(value);
        } else {
            attributes.put(attrId,new Attribute(value));
        }
    }// Поменять сеттеры на такие

    public void setAttributeDateValue(BigInteger attrId, Date dateValue) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setDateValue(dateValue);
        } else {
            throw new EAVAttributeException("Setting non existing attribute");
        }
    }

    public void setAttributeListValueId(BigInteger attrId, BigInteger listValueId) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setListValueId(listValueId);
        } else {
            throw new EAVAttributeException("Setting non existing attribute");
        }
    }

    public String getAttributeValue(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getValue();
        } else {
            throw new EAVAttributeException("Accessing non existing attribute");
        }
    }

    public Date getAttributeDateValue(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getDateValue();
        } else {
            throw new EAVAttributeException("Accessing non existing attribute");
        }
    }

    public BigInteger getAttributeListValueId(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getListValueId();
        } else {
            throw new EAVAttributeException("Accessing non existing attribute");
        }
    }

    public void setReference(BigInteger attrId, BigInteger reference) {
        if (references.containsKey(attrId)) {
            references.replace(attrId, reference);
        } else {
            throw new EAVAttributeException("Setting non existing reference");
        }
    }

    public BigInteger getReference(BigInteger attrId) {
        if (references.containsKey(attrId)) {
            return references.get(attrId);
        } else {
            throw new EAVAttributeException("Accessing non existing reference");
        }
    }

    public void saveToDB() {
        if(objectId==null){
            objectId = BigInteger.valueOf(jdbcTemplate.queryForObject("SELECT OBJECT_ID_seq.nextval FROM DUAL",Integer.class));
        }
        String updateObjectSQL = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 ON (p.object_id = p1.object_id)WHEN MATCHED THEN UPDATE SET p.name = p1.name,p.PARENT_ID=p1.parent_id WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";
        jdbcTemplate.update(updateObjectSQL,
                objectId,
                name,
                parentId,
                objectTypeId);

        // Сделать проверку на изменение
        String updateAttributeSQL = "MERGE INTO ATTRIBUTES p\n" +
                "   USING (   SELECT ? object_id, ? attr_id, ? value, ? date_value, ? list_value_id FROM DUAL) p1\n" +
                "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
                "   WHEN MATCHED THEN UPDATE SET p.value = p1.value,p.date_value = p1.date_value,p.list_value_id = p1.list_value_id     \n" +
                "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.value,p.date_value,p.list_value_id)\n" +
                "    VALUES (p1.attr_id, p1.object_id,p1.value,p1.date_value,p1.list_value_id)";

        for (Map.Entry<BigInteger, Attribute> attribute :
                attributes.entrySet()) {
            jdbcTemplate.update(updateAttributeSQL,
                    objectId,
                    attribute.getKey(),
                    attribute.getValue().getValue(),
                    attribute.getValue().getDateValue(),
                    attribute.getValue().getListValueId());
        }

        String updateReferenceSQL = "MERGE INTO OBJREFERENCE p\n" +
                "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
                "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
                "   WHEN MATCHED THEN UPDATE SET p.reference = p1.reference    \n" +
                "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
                "    VALUES (p1.attr_id, p1.object_id,p1.reference)";

        for (Map.Entry<BigInteger, BigInteger> reference :
                references.entrySet()) {
            jdbcTemplate.update(updateReferenceSQL,
                    objectId,
                    reference.getKey(),
                    reference.getValue());
        }
    }


    public BigInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(BigInteger objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<BigInteger, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<BigInteger, Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<BigInteger, BigInteger> getReferences() {
        return references;
    }

    public void setReferences(Map<BigInteger, BigInteger> references) {
        this.references = references;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public BigInteger getObjectTypeId() {
        return objectTypeId;
    }
}
