package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.models.*;
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
    private String queryObjectByType = "SELECT object_id FROM OBJECTS WHERE OBJECT_TYPE_ID  = ?";
    private String queryObjectById = "SELECT NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?";
    private String queryAttributeById = "SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE ATTR_ID = ?";
    private String queryAttributeByObjectId = "SELECT ATTR_ID,VALUE,DATE_VALUE,LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ?";
    private String queryObjectAttributeByREFERENCE = "SELECT ATTR_ID,OBJECT_ID FROM OBJREFERENCE WHERE REFERENCE = ?";
    private String updateObjectSQL = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 " +
            "ON (p.object_id = p1.object_id) WHEN MATCHED THEN UPDATE SET p.name = p1.name, p.PARENT_ID=p1.parent_id " +
            "WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    " +
            "VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";
    private String queryNextObjectId = "SELECT OBJECT_ID_seq.nextval FROM DUAL";
    private String updateAttributeSQL = "MERGE INTO ATTRIBUTES p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? value, ? date_value, ? list_value_id FROM DUAL) p1\n" +
            "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.value = p1.value,p.date_value = p1.date_value,p.list_value_id = p1.list_value_id     \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.value,p.date_value,p.list_value_id)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.value,p1.date_value,p1.list_value_id)";
    private String updateReferenceSQL = "MERGE INTO OBJREFERENCE p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
            "   ON (p.reference = p1.reference AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.object_id = p1.object_id    \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.reference)";
    private String deleteObject = "DELETE FROM OBJECTS WHERE object_id = ?";
    private int columnCount = 3;


    public EAVObjectDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T extends EAVObject> List<T> findObjectsByObjectTypeId(BigInteger objectTypeId, Class<T> clazz) {
        List<Object> objectIds = jdbcTemplate.queryForObject(queryObjectByType, new ObjectMapper(), objectTypeId);

        if (objectIds == null) {
            throw new EAVAttributeException("Object id cant be find in DataBase or its corrupted by object type id");
        }
        List<EAVObject> eavObjects = new ArrayList<>();
        EAVObject eavObject;
        for (int i = 0; i < objectIds.size(); i++) {
            eavObject = createEAVObject((BigInteger) objectIds.get(i), clazz);
            eavObjects.add(eavObject);
        }
        return (List<T>) eavObjects;
    }

    public <T extends EAVObject> T getObjectById(BigInteger id, Class<T> clazz, List<BigInteger> attributeIds) {
        EAVObject eavObject = createEAVObject(id, clazz);
        List<Object> columns = jdbcTemplate.queryForObject(queryObjectById, new ObjectMapper(), id);
        if (columns == null || columns.size() < columnCount) {
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }

        eavObject.setParentId((BigInteger) columns.get(0));
        eavObject.setObjectTypeId((BigInteger) columns.get(1));
        eavObject.setName((String) columns.get(2));

        for (BigInteger attributeId : attributeIds) {
            List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query(queryAttributeById,
                    new AttributeMapper(),
                    attributeId);

            HashMap<BigInteger, Attribute> attributes = new HashMap<>();
            for (Map.Entry<BigInteger, Attribute> attribute :
                    objectAttributes) {
                attributes.put(attribute.getKey(), attribute.getValue());
            }
            eavObject.setAttributes(attributes);
        }

        eavObject.setReferences(setReference(id));

        return (T) eavObject;

    }

    public <T extends EAVObject> T getObject(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject = createEAVObject(objectId, clazz);

        List<Object> columns = jdbcTemplate.queryForObject(queryObjectById, new ObjectMapper(), objectId);
        if (columns == null || columns.size() < columnCount) {
            throw new EAVAttributeException("Object cant be find in DataBase or its corrupted");
        }

        eavObject.setParentId((BigInteger) columns.remove(0));
        eavObject.setObjectTypeId((BigInteger) columns.remove(0));
        eavObject.setName((String) columns.remove(0));

        List<Map.Entry<BigInteger, Attribute>> objectAttributes = jdbcTemplate.query(queryAttributeByObjectId,
                new AttributeMapper(),
                objectId);

        HashMap<BigInteger, Attribute> attributes = new HashMap<>();
        for (Map.Entry<BigInteger, Attribute> attribute :
                objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
        eavObject.setAttributes(attributes);

        eavObject.setReferences(setReference(objectId));
        return (T) eavObject;
    }

    public void saveObjects(List<EAVObject> eavObjects) {
        for (EAVObject eavObject : eavObjects) {
            BigInteger objectId = nextObjectId(eavObject);
            jdbcTemplate.update(updateObjectSQL,
                    objectId,
                    eavObject.getName(),
                    eavObject.getParentId(),
                    eavObject.getObjectTypeId());
            eavObject.setObjectId(objectId);
        }
    }

    public void saveObject(EAVObject eavObject) {
        BigInteger objectId = nextObjectId(eavObject);

        jdbcTemplate.update(updateObjectSQL,
                objectId,
                eavObject.getName(),
                eavObject.getParentId(),
                eavObject.getObjectTypeId());
        eavObject.setObjectId(objectId);
    }

    public void saveAttributes(BigInteger objectId, Map<BigInteger, Attribute> attributes) {
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
        for (Map.Entry<BigInteger, BigInteger> reference :
                references.entrySet()) {
            jdbcTemplate.update(updateReferenceSQL,
                    reference.getValue(),
                    reference.getKey(),
                    objectId);
        }
    }

    public void deleteById(BigInteger id) {
        jdbcTemplate.update(deleteObject, id);
    }

    private <T extends EAVObject> T createEAVObject(BigInteger objectId, Class<T> clazz){
        EAVObject eavObject;
        if (Log.class.isAssignableFrom(clazz)){
            eavObject = new Log(objectId);
        }else if (LogFile.class.isAssignableFrom(clazz)){
            eavObject = new LogFile(objectId);
        }else if (Directory.class.isAssignableFrom(clazz)){
            eavObject= new Directory(objectId);
        }else if (Server.class.isAssignableFrom(clazz)){
            eavObject = new Server(objectId);
        }else if (User.class.isAssignableFrom(clazz)){
            eavObject = new User(objectId);
        }else if (Config.class.isAssignableFrom(clazz)){
            eavObject = new Config(objectId);
        }else{
            throw new EAVAttributeException("No such object");
        }
        return (T) eavObject;
    }

    private BigInteger nextObjectId(EAVObject eavObject){
        BigInteger objectId = eavObject.getObjectId();
        if (objectId == null) {
            objectId = jdbcTemplate.queryForObject(queryNextObjectId, BigInteger.class);
        }
        return objectId;
    }

    private HashMap<BigInteger, BigInteger> setReference(BigInteger id){
        List<Map.Entry<BigInteger, BigInteger>> objectReferences = jdbcTemplate.query(queryObjectAttributeByREFERENCE,
                new ReferenceMapper(),
                id);

        HashMap<BigInteger, BigInteger> references = new HashMap<>();
        for (Map.Entry<BigInteger, BigInteger> attribute :
                objectReferences) {
            references.put(attribute.getKey(), attribute.getValue());
        }
        return references;
    }
}
