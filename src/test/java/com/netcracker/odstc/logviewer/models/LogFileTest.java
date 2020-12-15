package com.netcracker.odstc.logviewer.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class LogFileTest {

    private LogFile logFileUnderTest;

    @BeforeEach
    void setUp() {
        logFileUnderTest = new LogFile("log.log", 0);
    }

    @Test
    void testConstructors() {
        LogFile emptyLogFile = new LogFile();
        assertNotNull(emptyLogFile.getAttributes());
        assertNotNull(emptyLogFile.getReferences());
        assertNull(emptyLogFile.getObjectId());

        LogFile logFileWithId = new LogFile(BigInteger.valueOf(1024));
        assertNotNull(logFileWithId.getObjectId());
        assertEquals(BigInteger.valueOf(1024), logFileWithId.getObjectId());
    }

    @Test
    void testSetGetName() {
        logFileUnderTest.setName("name");
        final String result = logFileUnderTest.getName();
        assertEquals("name", result);
    }

    @Test
    void testSetGetLastUpdate() {
        logFileUnderTest.setLastUpdate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        final Date result = logFileUnderTest.getLastUpdate();
        assertEquals(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), result);
    }

    @Test
    void testSetGetLastRow() {
        logFileUnderTest.setLastRow(0);
        final int result = logFileUnderTest.getLastRow();
        assertEquals(0, result);
    }
}
