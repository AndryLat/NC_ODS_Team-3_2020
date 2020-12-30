package com.netcracker.odstc.logviewer.models.eaventity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    Attribute attribute;
    String value = "Omega";
    Date dateValue = new Date(23);
    BigInteger listValueId = BigInteger.valueOf(12);

    @BeforeEach
    void setUp() {
        attribute = new Attribute();
    }


    @Test
    void constructors(){
        Attribute attribute2 = new Attribute(value);
        Attribute attribute3 = new Attribute(dateValue);
        Attribute attribute4 = new Attribute(listValueId);

        assertEquals(value,attribute2.getValue());
        assertEquals(dateValue,attribute3.getDateValue());
        assertEquals(listValueId,attribute4.getListValueId());

        Attribute attribute5 = new Attribute(value,dateValue,listValueId);

        assertEquals(value,attribute5.getValue());
        assertEquals(dateValue,attribute5.getDateValue());
        assertEquals(listValueId,attribute5.getListValueId());
    }
    @Test
    void setGetValue() {
        attribute.setValue(value);

        assertEquals(value,attribute.getValue());
    }

    @Test
    void setGetDateValue() {
        attribute.setDateValue(dateValue);

        assertEquals(dateValue,attribute.getDateValue());
    }

    @Test
    void setGetListValueId() {
        attribute.setListValueId(listValueId);

        assertEquals(listValueId,attribute.getListValueId());
    }

    @Test
    void testToString() {
        assertNotNull(attribute.toString());
    }
}
