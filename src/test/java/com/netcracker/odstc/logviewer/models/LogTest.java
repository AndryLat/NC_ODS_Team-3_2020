package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogTest {

    private Log logUnderTest;

    @BeforeEach
    void setUp() {
        logUnderTest = new Log("text", LogLevel.SEVERE);
    }

    @Test
    void testConstructors() {
        Log emptyLog = new Log();
        assertNotNull(emptyLog.getAttributes());
        assertNotNull(emptyLog.getReferences());
        assertNull(emptyLog.getObjectId());

        Log logWithId = new Log(BigInteger.valueOf(1024));
        assertNotNull(logWithId.getObjectId());
        assertEquals(BigInteger.valueOf(1024), logWithId.getObjectId());
    }

    @Test
    void testSetGetText() {
        logUnderTest.setText("FATAL ERROR");
        final String result = logUnderTest.getText();
        assertEquals("FATAL ERROR", result);
    }

    @Test
    void testSetGetLevel() {
        logUnderTest.setLevel(LogLevel.SEVERE);
        final LogLevel result = logUnderTest.getLevel();
        assertEquals(LogLevel.SEVERE, result);
    }

    @Test
    void testSetGetCreationDate() {
        logUnderTest.setCreationDate(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = logUnderTest.getCreationDate();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }
}
