package com.netcracker.odstc.logviewer.models.eaventity;

import com.netcracker.odstc.logviewer.models.eaventity.exceptions.EAVAttributeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EAVObjectTest {
    EAVObject eavObject1;

    @BeforeEach
    void setUp() {
        eavObject1 = new EAVObject();
    }
    @Test
    void constructors(){
        EAVObject eavObjectEmpty = new EAVObject();
        assertNotNull(eavObjectEmpty.getAttributes());
        assertNotNull(eavObjectEmpty.getReferences());

        assertNull(eavObjectEmpty.getObjectId());

        EAVObject eavObjectWithId = new EAVObject(BigInteger.valueOf(2));

        assertNotNull(eavObjectWithId.getAttributes());
        assertNotNull(eavObjectWithId.getReferences());

        assertNotNull(eavObjectWithId.getObjectId());
        assertEquals(BigInteger.valueOf(2),eavObjectWithId.getObjectId());

        String name = "Omega";

        EAVObject eavObjectFull = new EAVObject(BigInteger.valueOf(3),BigInteger.valueOf(1),BigInteger.valueOf(5),name);

        assertNotNull(eavObjectFull.getAttributes());
        assertNotNull(eavObjectFull.getReferences());

        assertNotNull(eavObjectFull.getObjectId());
        assertNotNull(eavObjectFull.getParentId());
        assertNotNull(eavObjectFull.getObjectTypeId());
        assertNotNull(eavObjectFull.getName());

        assertEquals(BigInteger.valueOf(3),eavObjectFull.getObjectId());
        assertEquals(BigInteger.valueOf(1),eavObjectFull.getParentId());
        assertEquals(BigInteger.valueOf(5),eavObjectFull.getObjectTypeId());
        assertEquals(name,eavObjectFull.getName());
    }

    @Test
    void setAttributeValue() {
        String value = "Omega";
        eavObject1.setAttributeValue(BigInteger.valueOf(1),value);

        assertEquals(value,eavObject1.getAttributeValue(BigInteger.valueOf(1)));
    }

    @Test
    void setAttributeDateValue() {
        Date date = new Date(23);

        eavObject1.setAttributeDateValue(BigInteger.valueOf(2),date);

        assertEquals(date,eavObject1.getAttributeDateValue(BigInteger.valueOf(2)));
    }

    @Test
    void setAttributeListValueId() {
        BigInteger listValueId = BigInteger.valueOf(3);

        eavObject1.setAttributeListValueId(BigInteger.valueOf(3),listValueId);

        assertEquals(listValueId,eavObject1.getAttributeListValueId(BigInteger.valueOf(3)));
    }

    @Test
    void setReference() {
        BigInteger referenceId = BigInteger.valueOf(25);

        eavObject1.setReference(BigInteger.valueOf(4),referenceId);

        assertEquals(referenceId,eavObject1.getReference(BigInteger.valueOf(4)));
    }

    @Test
    void setAttributes() {
        Map<BigInteger,Attribute> attributeMap = new HashMap<>();
        String value = "Omega";
        attributeMap.put(BigInteger.valueOf(1),new Attribute(value));
        Date dateValue = new Date(34);
        attributeMap.put(BigInteger.valueOf(2),new Attribute(dateValue));
        BigInteger listValueId = BigInteger.valueOf(12);
        attributeMap.put(BigInteger.valueOf(3),new Attribute(listValueId));

        eavObject1.setAttributes(attributeMap);

        assertNotNull(eavObject1.getAttributes());
        assertEquals(3, eavObject1.getAttributes().size());

        assertEquals(value,eavObject1.getAttributeValue(BigInteger.valueOf(1)));
        assertEquals(dateValue,eavObject1.getAttributeDateValue(BigInteger.valueOf(2)));
        assertEquals(listValueId,eavObject1.getAttributeListValueId(BigInteger.valueOf(3)));
    }

    @Test
    void setReferences() {
        Map<BigInteger,BigInteger> references = new HashMap<>();

        BigInteger reference1 = BigInteger.valueOf(45);
        BigInteger reference2 = BigInteger.valueOf(23);

        references.put(BigInteger.valueOf(10),reference1);
        references.put(BigInteger.valueOf(20),reference2);

        eavObject1.setReferences(references);

        assertNotNull(eavObject1.getReferences());
        assertEquals(2, eavObject1.getReferences().size());

        assertEquals(reference1,eavObject1.getReference(BigInteger.valueOf(10)));
        assertEquals(reference2,eavObject1.getReference(BigInteger.valueOf(20)));
    }

    @Test
    void setParentId() {
        BigInteger parentId = BigInteger.valueOf(12);

        eavObject1.setParentId(parentId);

        assertEquals(parentId,eavObject1.getParentId());
    }

    @Test
    void setObjectTypeId() {
        BigInteger objectTypeId = BigInteger.valueOf(12);

        eavObject1.setParentId(objectTypeId);

        assertEquals(objectTypeId,eavObject1.getParentId());
    }
    @Test
    void getNonExistedAttributeValue(){
        BigInteger attrId = BigInteger.valueOf(1111);
        assertThrows(EAVAttributeException.class,()->eavObject1.getAttributeValue(attrId));
    }
    @Test
    void getNonExistedAttributeDateValue(){
        BigInteger attrId = BigInteger.valueOf(2222);
        assertThrows(EAVAttributeException.class,()->eavObject1.getAttributeDateValue(attrId));
    }
    @Test
    void getNonExistedAttributeListValueId(){
        BigInteger attrId = BigInteger.valueOf(3333);
        assertThrows(EAVAttributeException.class,()->eavObject1.getAttributeListValueId(attrId));
    }
    @Test
    void setExistedAttributes(){
        EAVObject eavObjectWithAttributes = new EAVObject();
        Map<BigInteger,Attribute> attributeMap = new HashMap<>();
        attributeMap.put(BigInteger.valueOf(1),new Attribute("Omega"));
        attributeMap.put(BigInteger.valueOf(2),new Attribute(new Date()));
        attributeMap.put(BigInteger.valueOf(3),new Attribute(BigInteger.valueOf(1)));
        eavObjectWithAttributes.setAttributes(attributeMap);

        String value = "Alpha";
        Date dateValue = new Date(11);
        BigInteger listValueId = BigInteger.valueOf(224);

        eavObjectWithAttributes.setAttributeValue(BigInteger.valueOf(1),value);
        eavObjectWithAttributes.setAttributeDateValue(BigInteger.valueOf(2),dateValue);
        eavObjectWithAttributes.setAttributeListValueId(BigInteger.valueOf(3),listValueId);

        assertEquals(value,eavObjectWithAttributes.getAttributeValue(BigInteger.valueOf(1)));
        assertEquals(dateValue,eavObjectWithAttributes.getAttributeDateValue(BigInteger.valueOf(2)));
        assertEquals(listValueId,eavObjectWithAttributes.getAttributeListValueId(BigInteger.valueOf(3)));
    }
    @Test
    void costructObjectWithSetters(){
        EAVObject eavObject = new EAVObject();
        BigInteger objectId = BigInteger.valueOf(1);
        eavObject.setObjectId(objectId);
        BigInteger objectTypeId = BigInteger.valueOf(2);
        eavObject.setObjectTypeId(objectTypeId);
        BigInteger parentId = BigInteger.valueOf(3);
        eavObject.setParentId(parentId);
        String name = "Omegison";
        eavObject.setName(name);

        assertEquals(objectId,eavObject.getObjectId());
        assertEquals(objectTypeId,eavObject.getObjectTypeId());
        assertEquals(parentId,eavObject.getParentId());
        assertEquals(name,eavObject.getName());
    }
    @Test
    void getNonExistingReference(){
        BigInteger attrId = BigInteger.valueOf(1111);
        assertThrows(EAVAttributeException.class,()->eavObject1.getReference(attrId));
    }
    @Test
    void setExistingReference(){
        EAVObject eavObject = new EAVObject();
        eavObject.setReference(BigInteger.valueOf(12),BigInteger.valueOf(10));
        eavObject.setReference(BigInteger.valueOf(12),BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1),eavObject.getReference(BigInteger.valueOf(12)));
    }
}