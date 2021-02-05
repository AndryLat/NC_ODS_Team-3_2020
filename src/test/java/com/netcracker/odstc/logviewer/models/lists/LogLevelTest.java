package com.netcracker.odstc.logviewer.models.lists;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogLevelTest {

    @Test
    void testGetValue_providesID_WhenValueIsValid() {
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
    void testGetByID_providesValue_WhenIdIsValid() {
        assertEquals(LogLevel.ERROR, LogLevel.getByID(22));
        assertThrows(IllegalArgumentException.class, () -> LogLevel.getByID(42));
    }

    @Test
    void testContains_check_WhenValueMatches() {
        assertTrue(LogLevel.contains("DEBUG"));
        assertFalse(LogLevel.contains("DEBAG"));
    }
}
