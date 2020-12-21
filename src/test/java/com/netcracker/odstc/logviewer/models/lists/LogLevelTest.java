package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogLevelTest {

    @Test
    void testGetValue() {
        assertEquals(13, LogLevel.SEVERE.getValue());
        assertEquals(14, LogLevel.WARNING.getValue());
        assertEquals(15, LogLevel.INFO.getValue());
        assertEquals(16, LogLevel.CONFIG.getValue());
        assertEquals(17, LogLevel.FINE.getValue());
        assertEquals(18, LogLevel.FINER.getValue());
        assertEquals(19, LogLevel.FINEST.getValue());
        assertEquals(20, LogLevel.DEBUG.getValue());
        assertEquals(21, LogLevel.TRACE.getValue());
        assertEquals(22, LogLevel.ERROR.getValue());
        assertEquals(23, LogLevel.FATAL.getValue());
    }

    @Test
    void testGetByID() {
        assertEquals(LogLevel.ERROR, LogLevel.getByID(22));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.getByID(42));
    }

    @Test
    void testContains(){
        assertTrue(LogLevel.contains("DEBUG"));
        assertFalse(LogLevel.contains("DEBAG"));
    }
}
