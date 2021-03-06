package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void testGetValue_providesID_WhenValueIsValid() {
        assertEquals(1, Role.ADMIN.getValue());
        assertEquals(2, Role.USER.getValue());
    }

    @Test
    void testGetByID_providesValue_WhenIdIsValid() {
        assertEquals(Role.ADMIN, Role.getByID(1));
        assertThrows(IllegalArgumentException.class, () -> Role.getByID(42));
    }
}
