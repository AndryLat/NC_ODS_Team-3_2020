package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogLevelTest {

    @Test
    void testGetValue() {
        assertEquals(9, LogLevel.SEVERE.getValue());
        assertEquals(10, LogLevel.WARNING.getValue());
        assertEquals(11, LogLevel.INFO.getValue());
        assertEquals(12, LogLevel.CONFIG.getValue());
        assertEquals(13, LogLevel.FINE.getValue());
        assertEquals(14, LogLevel.FINER.getValue());
        assertEquals(15, LogLevel.FINEST.getValue());
        assertEquals(16, LogLevel.DEBUG.getValue());
        assertEquals(17, LogLevel.TRACE.getValue());
        assertEquals(18, LogLevel.ERROR.getValue());
        assertEquals(19, LogLevel.FATAL.getValue());
    }

    @Test
    void testGetByID() {
        assertEquals(LogLevel.ERROR, LogLevel.getByID(18));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.getByID(42));
    }

    @Test
    void testContains(){
        assertTrue(LogLevel.contains("DEBUG"));
        assertFalse(LogLevel.contains("DEBAG"));
    }
}
