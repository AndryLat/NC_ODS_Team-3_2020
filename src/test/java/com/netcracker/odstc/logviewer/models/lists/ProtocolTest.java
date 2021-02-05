package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProtocolTest {

    @Test
    void testGetValue_providesID_WhenValueIsValid() {
        assertEquals(3, Protocol.SSH.getValue());
        assertEquals(4, Protocol.FTP.getValue());
    }

    @Test
    void testGetByID_providesValue_WhenIdIsValid() {
        assertEquals(Protocol.FTP, Protocol.getByID(4));
        assertThrows(IllegalArgumentException.class, () -> Protocol.getByID(42));
    }
}
