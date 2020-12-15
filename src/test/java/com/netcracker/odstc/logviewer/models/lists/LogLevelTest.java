package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogLevelTest {

    @Test
    void testGetValue() {
        assertEquals(7, LogLevel.SEVERE.getValue());
        assertEquals(8, LogLevel.WARNING.getValue());
        assertEquals(9, LogLevel.INFO.getValue());
        assertEquals(10, LogLevel.CONFIG.getValue());
        assertEquals(11, LogLevel.FINE.getValue());
        assertEquals(12, LogLevel.FINER.getValue());
        assertEquals(13, LogLevel.FINEST.getValue());
        assertEquals(14, LogLevel.DEBUG.getValue());
        assertEquals(15, LogLevel.TRACE.getValue());
        assertEquals(16, LogLevel.ERROR.getValue());
        assertEquals(17, LogLevel.FATAL.getValue());
    }

    @Test
    void testGetByID() {
        assertEquals(LogLevel.ERROR, LogLevel.getByID(16));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.getByID(42));
    }

    @Test
    void testContains(){
        assertTrue(LogLevel.contains("DEBUG"));
        assertFalse(LogLevel.contains("DEBAG"));
    }
}
