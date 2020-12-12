package com.netcracker.odstc.logviewer.models.eaventity;

import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EAVObject {

    private BigInteger objectId;
    private BigInteger parentId;
    private BigInteger objectTypeId;
    private String name;
    private Map<BigInteger, Attribute> attributes;
    private Map<BigInteger, BigInteger> references;

    public EAVObject(){
        attributes = new HashMap<>();
        references = new HashMap<>();
    }

    public EAVObject(BigInteger objectId) {
        this();
        this.objectId = objectId;
    }
    public EAVObject(BigInteger objectId, BigInteger parentId, BigInteger objectTypeId,String name){
        this(objectId);
        this.parentId = parentId;
        this.objectTypeId = objectTypeId;
        this.name = name;
    }

    public void setAttributeValue(BigInteger attrId, String value) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setValue(value);
        } else {
            attributes.put(attrId,new Attribute(value));
        }
    }

    public void setAttributeDateValue(BigInteger attrId, Date dateValue) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setDateValue(dateValue);
        } else {
            attributes.put(attrId,new Attribute(dateValue));
        }
    }

    public void setAttributeListValueId(BigInteger attrId, BigInteger listValueId) {
        if (attributes.containsKey(attrId)) {
            attributes.get(attrId).setListValueId(listValueId);
        } else {
            attributes.put(attrId,new Attribute(listValueId));
        }
    }

    public String getAttributeValue(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getValue();
        } else {
            throw new EAVAttributeException(EAVAttributeException.NON_EXISTING_ATTRIBUTE);
        }
    }

    public Date getAttributeDateValue(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getDateValue();
        } else {
            throw new EAVAttributeException(EAVAttributeException.NON_EXISTING_ATTRIBUTE);
        }
    }

    public BigInteger getAttributeListValueId(BigInteger attrId) {
        if (attributes.containsKey(attrId)) {
            return attributes.get(attrId).getListValueId();
        } else {
            throw new EAVAttributeException(EAVAttributeException.NON_EXISTING_ATTRIBUTE);
        }
    }

    public void setReference(BigInteger attrId, BigInteger reference) {
        if (references.containsKey(attrId)) {
            references.replace(attrId, reference);
        } else {
            references.put(attrId,reference);
        }
    }

    public BigInteger getReference(BigInteger attrId) {
        if (references.containsKey(attrId)) {
            return references.get(attrId);
        } else {
            throw new EAVAttributeException(EAVAttributeException.NON_EXISTING_REFERENCE);
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

    public void setObjectTypeId(BigInteger objectTypeId) {
        this.objectTypeId = objectTypeId;
    }
}
