package com.netcracker.odstc.logviewer.containers;

import java.math.BigInteger;
import java.util.Date;

public class AttributeObjectContainer {
    private BigInteger objectId;
    private BigInteger parentId;
    private String name;
    private BigInteger objectTypeId;

    private BigInteger attrId;
    private String value;
    private Date dateValue;
    private BigInteger listValueId;

    public BigInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(BigInteger objectId) {
        this.objectId = objectId;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(BigInteger objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public BigInteger getAttrId() {
        return attrId;
    }

    public void setAttrId(BigInteger attrId) {
        this.attrId = attrId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public BigInteger getListValueId() {
        return listValueId;
    }

    public void setListValueId(BigInteger listValueId) {
        this.listValueId = listValueId;
    }
    // Можно сделать геттер на объект и атрибут.
}
