package com.netcracker.odstc.logviewer.models.eaventity.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributesTest {

    @Test
    void testValues_idNotNull_allValuesHaveId() {
        for(Attributes attributes:Attributes.values()){
            assertNotNull(attributes.getAttrId());
        }
    }
}
