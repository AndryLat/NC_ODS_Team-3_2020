package com.netcracker.odstc.logviewer.dao;

import com.netcracker.odstc.logviewer.mapper.AttributeMapper;
import com.netcracker.odstc.logviewer.mapper.ObjectMapper;
import com.netcracker.odstc.logviewer.mapper.ReferenceMapper;
import com.netcracker.odstc.logviewer.models.eaventity.Attribute;
import com.netcracker.odstc.logviewer.models.eaventity.EAVObject;
import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import com.netcracker.odstc.logviewer.serverconnection.publishers.DAOPublisher;
import com.netcracker.odstc.logviewer.serverconnection.publishers.ObjectChangeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Repository
public class EAVObjectDAO {

    private static final String GET_OBJECT_BY_PARENT_ID_QUERY = "SELECT object_id, NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE PARENT_ID  = ?";

    private static final String GET_OBJECT_BY_TYPE_QUERY = "SELECT object_id, NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE OBJECT_TYPE_ID  = ?";

    private static final String GET_OBJECT_BY_ID_QUERY = "SELECT object_id ,NAME, PARENT_ID, OBJECT_TYPE_ID FROM OBJECTS WHERE object_id = ?";

    private static final String GET_ATTRIBUTE_BY_ID_QUERY = "SELECT ATTR_ID, VALUE, DATE_VALUE, LIST_VALUE_ID FROM ATTRIBUTES WHERE ATTR_ID = ?";

    private static final String GET_ATTRIBUTE_BY_OBJECT_ID_QUERY = "SELECT ATTR_ID, VALUE, DATE_VALUE, LIST_VALUE_ID FROM ATTRIBUTES WHERE object_id = ?";

    private static final String GET_OBJECT_ATTRIBUTE_BY_REFERENCE_QUERY = "SELECT ATTR_ID, OBJECT_ID FROM OBJREFERENCE WHERE REFERENCE = ?";

    private static final String GET_NEXT_OBJECT_ID_QUERY = "SELECT OBJECT_ID_seq.nextval FROM DUAL";

    private static final String UPDATE_OBJECT_QUERY = "MERGE INTO OBJECTS p USING (SELECT ? object_id, ? name,? parent_id,? object_type_id FROM DUAL) p1 " +
            "ON (p.object_id = p1.object_id) WHEN MATCHED THEN UPDATE SET p.name = p1.name, p.PARENT_ID=p1.parent_id " +
            "WHEN NOT MATCHED THEN INSERT (p.object_id, p.name,p.OBJECT_TYPE_ID,p.PARENT_ID)    " +
            "VALUES (p1.object_id, p1.name,p1.object_type_id,p1.parent_id)";

    private static final String UPDATE_ATTRIBUTE_QUERY = "MERGE INTO ATTRIBUTES p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? value, ? date_value, ? list_value_id FROM DUAL) p1\n" +
            "   ON (p.object_id = p1.object_id AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.value = p1.value,p.date_value = p1.date_value,p.list_value_id = p1.list_value_id     \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.value,p.date_value,p.list_value_id)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.value,p1.date_value,p1.list_value_id)";

    private static final String UPDATE_REFERENCE_QUERY = "MERGE INTO OBJREFERENCE p\n" +
            "   USING (   SELECT ? object_id, ? attr_id, ? reference FROM DUAL) p1\n" +
            "   ON (p.reference = p1.reference AND p.attr_id = p1.attr_id)\n" +
            "   WHEN MATCHED THEN UPDATE SET p.object_id = p1.object_id    \n" +
            "   WHEN NOT MATCHED THEN INSERT (p.attr_id, p.object_id,p.reference)\n" +
            "    VALUES (p1.attr_id, p1.object_id,p1.reference)";

    private static final String DELETE_OBJECT_QUERY = "DELETE FROM OBJECTS WHERE object_id = ?";

    private static final String GET_ATTRIBUTE_BY_VALUE_QUERY = "select attr_id, object_id, value\n" +
            " from attributes attr\n" +
            " where attr.value like ?";

    private static final String GET_ATTRIBUTE_BY_DATE_VALUE_QUERY = "select attr_id, object_id, value\n" +
            " from attributes attr\n" +
            " where attr.DATE_value like ?";

    private static final String GET_ATTRIBUTE_BY_LIST_VALUE_QUERY = "select attr.attr_id, attr.object_id, Lists.value\n" +
            "    from attributes attr join LISTS on attr.LIST_VALUE_ID = LISTS.LIST_VALUE_ID\n" +
            "    where Lists.value like ?";

    protected final JdbcTemplate jdbcTemplate;

    private final Logger logger = LogManager.getLogger(EAVObjectDAO.class.getName());

    public EAVObjectDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T extends EAVObject> List<T> getObjectsByParentId(BigInteger bigInteger, Class<T> clazz) {
        return getObjectsByQuery(bigInteger, clazz, GET_OBJECT_BY_PARENT_ID_QUERY);
    }

    public <T extends EAVObject> List<T> getObjectsByObjectTypeId(BigInteger objectTypeId, Class<T> clazz) {
        return getObjectsByQuery(objectTypeId, clazz, GET_OBJECT_BY_TYPE_QUERY);
    }

    public <T extends EAVObject> T getObjectByIdAttrByIds(BigInteger id, Class<T> clazz, List<BigInteger> attributeIds) {
        EAVObject eavObject = createEAVObject(id, clazz);
        EAVObject columns = jdbcTemplate.queryForObject(GET_OBJECT_BY_ID_QUERY, new ObjectMapper(), id);
        if (columns == null) {
            throw new EAVAttributeException("Object id cant be find in DataBase or its corrupted by object type id");
        }
        eavObject.setName(columns.getName());
        eavObject.setParentId(columns.getParentId());
        eavObject.setObjectTypeId(columns.getObjectTypeId());
        for (BigInteger attributeId : attributeIds) {
            eavObject.setAttributes(getAttributes(attributeId, GET_ATTRIBUTE_BY_ID_QUERY));
        }
        eavObject.setReferences(getReference(id));

        return (T) eavObject;

    }

    public <T extends EAVObject> T getObjectById(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject = createEAVObject(objectId, clazz);
        EAVObject columns = jdbcTemplate.queryForObject(GET_OBJECT_BY_ID_QUERY, new ObjectMapper(), objectId);
        if (columns != null) {
            eavObject.setName(columns.getName());
            eavObject.setParentId(columns.getParentId());
            eavObject.setObjectTypeId(columns.getObjectTypeId());
            eavObject.setAttributes(getAttributes(objectId, GET_ATTRIBUTE_BY_OBJECT_ID_QUERY));
            eavObject.setReferences(getReference(objectId));
        }
        return (T) eavObject;
    }

    public <T extends EAVObject> List<T> getAttributeByValue(String value) {
        return getAttribute(value, GET_ATTRIBUTE_BY_VALUE_QUERY);
    }

    public <T extends EAVObject> List<T> getAttributeByDateValue(Date dateValue) {
        return getAttribute(dateValue, GET_ATTRIBUTE_BY_DATE_VALUE_QUERY);
    }

    public <T extends EAVObject> List<T> getAttributeByListValue(String listValue) {
        return getAttribute(listValue, GET_ATTRIBUTE_BY_LIST_VALUE_QUERY);
    }

    public <T extends EAVObject> void saveObjectAttributesReferences(T eavObject) {
        saveObject(eavObject);
        saveAttributes(eavObject.getObjectId(), eavObject.getAttributes());
        saveReferences(eavObject.getObjectId(), eavObject.getReferences());
        DAOPublisher.getInstance().notifyListeners(new ObjectChangeEvent(ObjectChangeEvent.ChangeType.UPDATE, this, eavObject, null));
    }

    public <T extends EAVObject> void saveObjectsAttributesReferences(List<T> eavObjects) {
        for (EAVObject eavObject : eavObjects) {
            saveObjectAttributesReferences(eavObject);
        }
    }

    public <T extends EAVObject> void saveObjects(List<T> eavObjects) {
        for (EAVObject eavObject : eavObjects) {
            saveObject(eavObject);
        }
    }

    public <T extends EAVObject> void saveObject(T eavObject) {
        BigInteger objectId = nextObjectId(eavObject);
        jdbcTemplate.update(UPDATE_OBJECT_QUERY,
                objectId,
                eavObject.getName(),
                eavObject.getParentId(),
                eavObject.getObjectTypeId());
        eavObject.setObjectId(objectId);
    }

    public void saveAttributes(BigInteger objectId, Map<BigInteger, Attribute> attributes) {
        for (Map.Entry<BigInteger, Attribute> attribute : attributes.entrySet()) {
            jdbcTemplate.update(UPDATE_ATTRIBUTE_QUERY,
                    objectId,
                    attribute.getKey(),
                    attribute.getValue().getValue(),
                    attribute.getValue().getDateValue(),
                    attribute.getValue().getListValueId());
        }
    }

    public void saveReferences(BigInteger objectId, Map<BigInteger, BigInteger> references) {
        for (Map.Entry<BigInteger, BigInteger> reference : references.entrySet()) {
            jdbcTemplate.update(UPDATE_REFERENCE_QUERY,
                    reference.getValue(),
                    reference.getKey(),
                    objectId);
        }
    }

    public void deleteById(BigInteger id) {
        BigInteger objectTypeId = jdbcTemplate.queryForObject("SELECT OBJECT_TYPE_ID FROM OBJECTS WHERE OBJECT_ID = ?", BigInteger.class, id);
        jdbcTemplate.update(DELETE_OBJECT_QUERY, id);
        DAOPublisher.getInstance().notifyListeners(new ObjectChangeEvent(ObjectChangeEvent.ChangeType.DELETE, this, id, objectTypeId));
    }

    private <T extends EAVObject> T createEAVObject(BigInteger objectId, Class<T> clazz) {
        EAVObject eavObject = new EAVObject();
        try {
            eavObject = clazz.getConstructor(BigInteger.class).newInstance(objectId);
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException k) {
            logger.error("No access", k);
        } catch (InvocationTargetException j) {
            logger.error(j.getMessage());
        } catch (NoSuchMethodException m) {
            logger.error("No such method", m);
        }
        return (T) eavObject;
    }

    private BigInteger nextObjectId(EAVObject eavObject) {
        BigInteger objectId = eavObject.getObjectId();
        if (objectId == null) {
            objectId = jdbcTemplate.queryForObject(GET_NEXT_OBJECT_ID_QUERY, BigInteger.class);
        }
        return objectId;
    }

    private HashMap<BigInteger, BigInteger> getReference(BigInteger id) {
        List<Map.Entry<BigInteger, BigInteger>> objectReferences = jdbcTemplate.query(GET_OBJECT_ATTRIBUTE_BY_REFERENCE_QUERY,
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
        for (Map.Entry<BigInteger, Attribute> attribute : objectAttributes) {
            attributes.put(attribute.getKey(), attribute.getValue());
        }
        return attributes;
    }

    private <T extends EAVObject> List<T> getObjectsByQuery(BigInteger id, Class<T> clazz, String query) {
        List<EAVObject> objectIds = jdbcTemplate.query(query, new ObjectMapper(), id);
        List<EAVObject> eavObjects = new ArrayList<>();
        EAVObject eavObject;
        for (int i = 0; i < objectIds.size(); i++) {
            eavObject = createEAVObject(objectIds.get(i).getObjectId(), clazz);
            eavObject.setName(objectIds.get(i).getName());
            eavObject.setParentId(objectIds.get(i).getParentId());
            eavObjects.add(eavObject);
            eavObject.setAttributes(getAttributes(objectIds.get(i).getObjectId(), GET_ATTRIBUTE_BY_OBJECT_ID_QUERY));
            eavObject.setReferences(getReference(objectIds.get(i).getObjectId()));
        }
        return (List<T>) eavObjects;
    }

    private <T extends EAVObject> List<T> getAttribute(Object value, String query) {
        List<EAVObject> attributes = jdbcTemplate.query(query, new ObjectMapper(), value);
        return (List<T>) attributes;
    }
}
