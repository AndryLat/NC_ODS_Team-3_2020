package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.models.eaventity.Attribute;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.AttributeMapper;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.ObjectMapper;
import com.netcracker.odstc.logviewer.models.eaventity.mappers.ReferenceMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EAVObjectDAO {

    public final JdbcTemplate jdbcTemplate;

    public EAVObjectDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private BigInteger objectId;

    public List<EAVObject> findObjectsByObjectTypeId(BigInteger objectTypeId) {
        List<Object> objectIds = jdbcTemplate.queryForObject("SELECT object_id FROM OBJECTS WHERE OBJECT_TYPE_ID  = ?", new ObjectMapper(), objectTypeId);

        if (objectIds == null) {
            throw new EAVAttributeException("Object id cant be find in DataBase or its corrupted by object type id");
        }
        List<EAVObject> eavObjects = new ArrayList<>();
        EAVObject eavObject;
        for (int i = 0; i < objectIds.size(); i++) {
            eavObject = new EAVObject((BigInteger) objectIds.get(i));
            eavObjects.add(eavObject);
        }
        return eavObjects;
    }

    public void saveObjects(EAVObject... eavObjects) {
        for (EAVObject eavObject : eavObjects) {
            String updateObjectSQL = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 ON (p.object_id = p1.object_id)WHEN MATCHED THEN UPDATE SET p.name = p1.name,p.PARENT_ID=p1.parent_id WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";
            jdbcTemplate.update(updateObjectSQL,
                    objectId,
                    eavObject.getName(),
                    eavObject.getParentId(),
                    eavObject.getObjectTypeId());
            eavObject.setObjectId(objectId);
        }
    }

    public EAVObject getObjectById(BigInteger id, BigInteger... attributeIds) {
        EAVObject eavObject = new EAVObject(id);
        List<Object> columns = jdbcTemplate.queryForObject("SELECT NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?", new ObjectMapper(), id);
        if (columns == null || columns.size() < 3) {
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }

        eavObject.setParentId((BigInteger) columns.remove(0));
        eavObject.setObjectTypeId((BigInteger) columns.remove(0));
        eavObject.setName((String) columns.remove(0));


        for (BigInteger attributeId : attributeIds) {
            List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query("SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE ATTR_ID = ?",
                    new AttributeMapper(),
                    attributeId);

            HashMap<BigInteger, Attribute> attributes = new HashMap<>();
            for (Map.Entry<BigInteger, Attribute> attribute :
                    objectAttributes) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }
            eavObject.setAttributes(attributes);
        }
        return eavObject;

    }

    public void saveObject(EAVObject eavObject) {
        objectId = eavObject.getObjectId();
        if (objectId == null) {
            objectId = BigInteger.valueOf(jdbcTemplate.queryForObject("SELECT OBJECT_ID_seq.nextval FROM DUAL", Integer.class));
        }

        String updateObjectSQL = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 ON (p.object_id = p1.object_id)WHEN MATCHED THEN UPDATE SET p.name = p1.name,p.PARENT_ID=p1.parent_id WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";
        jdbcTemplate.update(updateObjectSQL,
                objectId,
                eavObject.getName(),
                eavObject.getParentId(),
                eavObject.getObjectTypeId());
        eavObject.setObjectId(objectId);
    }

    public void saveAttributes(BigInteger objectId, Map<BigInteger, Attribute> attributes) {
        String updateAttributeSQL = "MERGE INTO ATTRIBUTES p\n" +
                "   USING (   SELECT ? object_id, ? attr_id, ? value, ? date_value, ? list_value_id FROM DUAL) p1\n" +
                "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
                "   WHEN MATCHED THEN UPDATE SET p.value = p1.value,p.date_value = p1.date_value,p.list_value_id = p1.list_value_id     \n" +
                "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.value,p.date_value,p.list_value_id)\n" +
                "    VALUES (p1.attr_id, p1.object_id,p1.value,p1.date_value,p1.list_value_id)";

        // Сделать проверку на изменение
        /*if(objectId == null){
            objectId = this.objectId;
        }*/

        for (Map.Entry<BigInteger, Attribute> attribute :
                attributes.entrySet()) {
            jdbcTemplate.update(updateAttributeSQL,
                    objectId,
                    attribute.getKey(),
                    attribute.getValue().getValue(),
                    attribute.getValue().getDateValue(),
                    attribute.getValue().getListValueId());
        }
    }

    public void saveReferences(BigInteger objectId, Map<BigInteger, BigInteger> references) {
        String updateReferenceSQL = "MERGE INTO OBJREFERENCE p\n" +
                "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
                "   ON (p.reference = p1.reference AND p.attr_id = p1.attr_id)\n" +
                "   WHEN MATCHED THEN UPDATE SET p.object_id = p1.object_id    \n" +
                "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
                "    VALUES (p1.attr_id, p1.object_id,p1.reference)";

        for (Map.Entry<BigInteger, BigInteger> reference :
                references.entrySet()) {
            jdbcTemplate.update(updateReferenceSQL,
                    reference.getValue(),
                    reference.getKey(),
                    objectId);
        }
    }

    public EAVObject getObject(BigInteger objectId) {

        EAVObject eavObject = new EAVObject(objectId);
        List<Object> columns = jdbcTemplate.queryForObject("SELECT NAME,PARENT_ID,OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?", new ObjectMapper(), objectId);
        if (columns == null || columns.size() < 3) {
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }

        eavObject.setParentId((BigInteger) columns.remove(0));
        eavObject.setObjectTypeId((BigInteger) columns.remove(0));
        eavObject.setName((String) columns.remove(0));

        List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query("SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ?",
                new AttributeMapper(),
                objectId);

        HashMap<BigInteger, Attribute> attributes = new HashMap<>();
        for (Map.Entry<BigInteger, Attribute> attribute :
                objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
        eavObject.setAttributes(attributes);

        List<Map.Entry<BigInteger, BigInteger>> objectReferences = jdbcTemplate.query("SELECT ATTR_ID,OBJECT_ID FROM OBJREFERENCE WHERE REFERENCE = ?",
                new ReferenceMapper(),
                objectId);

        HashMap<BigInteger, BigInteger> references = new HashMap<>();
        for (Map.Entry<BigInteger, BigInteger> attribute :
                objectReferences) {
            references.put(attribute.getKey(), attribute.getValue());
        }
        eavObject.setReferences(references);
        return eavObject;
    }

    public BigInteger getObjectId() {
        return objectId;
    }

    public void deleteById(BigInteger id) {
        String sql = "DELETE FROM OBJECTS WHERE object_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
