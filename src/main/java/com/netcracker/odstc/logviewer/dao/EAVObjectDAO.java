package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.models.*;
import com.netcracker.odstc.logviewer.models.eaventity.Attribute;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.mapper.AttributeMapper;
import com.netcracker.odstc.logviewer.mapper.ObjectMapper;
import com.netcracker.odstc.logviewer.mapper.ReferenceMapper;
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
    private static final String QUERY_OBJECT_BY_TYPE = "SELECT object_id, NAME, PARENT_ID FROM OBJECTS WHERE OBJECT_TYPE_ID  = ?";
    private static final String QUERY_OBJECT_BY_ID = "SELECT NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?";
    private static final String QUERY_ATTRIBUTE_BY_ID = "SELECT ATTR_ID, VALUE, DATE_VALUE, LIST_VALUE_ID FROM ATTRIBUTES WHERE ATTR_ID = ?";
    private static final String QUERY_ATTRIBUTE_BY_OBJECT_ID = "SELECT ATTR_ID, VALUE, DATE_VALUE, LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ?";
    private static final String QUERY_OBJECT_ATTRIBUTE_BY_REFERENCE = "SELECT ATTR_ID, OBJECT_ID FROM OBJREFERENCE WHERE REFERENCE = ?";
    private static final String UPDATE_OBJECT_SQL = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 " +
            "ON (p.object_id = p1.object_id) WHEN MATCHED THEN UPDATE SET p.name = p1.name, p.PARENT_ID=p1.parent_id " +
            "WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    " +
            "VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";
    private static final String QUERY_NEXT_OBJECT_ID = "SELECT OBJECT_ID_seq.nextval FROM DUAL";
    private static final String UPDATE_ATTRIBUTE_SQL = "MERGE INTO ATTRIBUTES p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? value, ? date_value, ? list_value_id FROM DUAL) p1\n" +
            "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.value = p1.value,p.date_value = p1.date_value,p.list_value_id = p1.list_value_id     \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.value,p.date_value,p.list_value_id)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.value,p1.date_value,p1.list_value_id)";
    private static final String UPDATE_REFERENCE_SQL = "MERGE INTO OBJREFERENCE p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
            "   ON (p.reference = p1.reference AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.object_id = p1.object_id    \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.reference)";
    private static final String DELETE_OBJECT = "DELETE FROM OBJECTS WHERE object_id = ?";

    public EAVObjectDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private HashMap<BigInteger, BigInteger> getReference(BigInteger id) {
        List<Map.Entry<BigInteger, BigInteger>> objectReferences = jdbcTemplate.query(QUERY_OBJECT_ATTRIBUTE_BY_REFERENCE,
                new ReferenceMapper(),
                id);

        HashMap<BigInteger, BigInteger> references = new HashMap<>();
        for (Map.Entry<BigInteger, BigInteger> attribute :
                objectReferences) {
            references.put(attribute.getKey(), attribute.getValue());
        }
        return references;
    }

    private HashMap<BigInteger, Attribute> getAttributes(BigInteger attributeId, String query) {
        List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query(query,
                new AttributeMapper(),
                attributeId);

        HashMap<BigInteger, Attribute> attributes = new HashMap<>();
        for (Map.Entry<BigInteger, Attribute> attribute :
                objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
        return attributes;
    }

    public <T extends EAVObject> List<T> getObjectsByObjectTypeId(BigInteger objectTypeId, Class<T> clazz) {
        List<EAVObject> objectIds = jdbcTemplate.query(QUERY_OBJECT_BY_TYPE, new ObjectMapper(), objectTypeId);
        List<EAVObject> eavObjects = new ArrayList<>();
        EAVObject eavObject;
        for (int i = 0; i < objectIds.size(); i++) {
            eavObject = createEAVObject(objectIds.get(i).getObjectId(), clazz);
            eavObject.setName(objectIds.get(i).getName());
            eavObject.setParentId(objectIds.get(i).getParentId());
            eavObjects.add(eavObject);
            eavObject.setAttributes(getAttributes(objectIds.get(i).getObjectId(), QUERY_ATTRIBUTE_BY_OBJECT_ID));
            eavObject.setReferences(getReference(objectIds.get(i).getObjectId()));
        }
        return (List<T>) eavObjects;
    }

    public <T extends EAVObject> T getObjectByIdAttrByIds(BigInteger id, Class<T> clazz, List<BigInteger> attributeIds) {
        EAVObject eavObject = createEAVObject(id, clazz);
        EAVObject columns = jdbcTemplate.queryForObject(QUERY_OBJECT_BY_ID, new ObjectMapper(), id);
        if (columns == null) {
            throw new EAVAttributeException("Object id cant be find in DataBase or its corrupted by object type id");
        }
        eavObject.setName(columns.getName());
        eavObject.setParentId(columns.getParentId());
        eavObject.setObjectTypeId(columns.getObjectTypeId());
        for (BigInteger attributeId : attributeIds) {
            eavObject.setAttributes(getAttributes(attributeId, QUERY_ATTRIBUTE_BY_ID));
        }
        eavObject.setReferences(getReference(id));

        return (T) eavObject;

    }

    public <T extends EAVObject> T getObjectById(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject = createEAVObject(objectId, clazz);
        EAVObject columns = jdbcTemplate.queryForObject(QUERY_OBJECT_BY_ID, new ObjectMapper(), objectId);
        if (columns == null) {
            throw new EAVAttributeException("Object id cant be find in DataBase or its corrupted by object type id");
        }
        eavObject.setName(columns.getName());
        eavObject.setParentId(columns.getParentId());
        eavObject.setObjectTypeId(columns.getObjectTypeId());
        eavObject.setAttributes(getAttributes(objectId, QUERY_ATTRIBUTE_BY_OBJECT_ID));
        eavObject.setReferences(getReference(objectId));
        return (T) eavObject;
    }

    public <T extends EAVObject> void saveAll(T eavObject){
        saveObject(eavObject);
        saveAttributes(eavObject.getObjectId(), eavObject.getAttributes());
        saveReferences(eavObject.getObjectId(), eavObject.getReferences());
    }

    public <T extends EAVObject> void saveObjects(List<T> eavObjects) {
        for (EAVObject eavObject : eavObjects) {
            BigInteger objectId = nextObjectId(eavObject);
            jdbcTemplate.update(UPDATE_OBJECT_SQL,
                    objectId,
                    eavObject.getName(),
                    eavObject.getParentId(),
                    eavObject.getObjectTypeId());
            eavObject.setObjectId(objectId);
        }
    }

    public <T extends EAVObject> void saveObject(T eavObject) {
        BigInteger objectId = nextObjectId(eavObject);

        jdbcTemplate.update(UPDATE_OBJECT_SQL,
                objectId,
                eavObject.getName(),
                eavObject.getParentId(),
                eavObject.getObjectTypeId());
        eavObject.setObjectId(objectId);
    }

    public void saveAttributes(BigInteger objectId, Map<BigInteger, Attribute> attributes) {
        for (Map.Entry<BigInteger, Attribute> attribute :
                attributes.entrySet()) {
            jdbcTemplate.update(UPDATE_ATTRIBUTE_SQL,
                    objectId,
                    attribute.getKey(),
                    attribute.getValue().getValue(),
                    attribute.getValue().getDateValue(),
                    attribute.getValue().getListValueId());
        }
    }

    public void saveReferences(BigInteger objectId, Map<BigInteger, BigInteger> references) {
        for (Map.Entry<BigInteger, BigInteger> reference :
                references.entrySet()) {
            jdbcTemplate.update(UPDATE_REFERENCE_SQL,
                    reference.getValue(),
                    reference.getKey(),
                    objectId);
        }
    }

    public void deleteById(BigInteger id) {
        jdbcTemplate.update(DELETE_OBJECT, id);
    }

    private <T extends EAVObject> T createEAVObject(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject;
        if (Log.class.isAssignableFrom(clazz)) {
            eavObject = new Log(objectId);
        } else if (LogFile.class.isAssignableFrom(clazz)) {
            eavObject = new LogFile(objectId);
        } else if (Directory.class.isAssignableFrom(clazz)) {
            eavObject = new Directory(objectId);
        } else if (Server.class.isAssignableFrom(clazz)) {
            eavObject = new Server(objectId);
        } else if (User.class.isAssignableFrom(clazz)) {
            eavObject = new User(objectId);
        } else if (Config.class.isAssignableFrom(clazz)) {
            eavObject = new Config(objectId);
        } else {
            throw new EAVAttributeException("No such object");
        }
        return (T) eavObject;
    }

    private BigInteger nextObjectId(EAVObject eavObject) {
        BigInteger objectId = eavObject.getObjectId();
        if (objectId == null) {
            objectId = jdbcTemplate.queryForObject(QUERY_NEXT_OBJECT_ID, BigInteger.class);
        }
        return objectId;
    }
}