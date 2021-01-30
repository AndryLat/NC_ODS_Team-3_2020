package com.netcracker.odstc.logviewer.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryTest {

    private Directory directoryUnderTest;

    @BeforeEach
    void setUp() {
        directoryUnderTest = new Directory("New folder");
    }

    @Test
    void testConstructors() {
        Directory emptyDirectory = new Directory();
        assertNotNull(emptyDirectory.getAttributes());
        assertNotNull(emptyDirectory.getReferences());
        assertNull(emptyDirectory.getObjectId());

        Directory directoryWithId = new Directory(BigInteger.valueOf(512));
        assertNotNull(directoryWithId.getObjectId());
        assertEquals(BigInteger.valueOf(512), directoryWithId.getObjectId());
    }

    @Test
    void testSetGetPath() {
        directoryUnderTest.setPath("path");
        final String result = directoryUnderTest.getPath();
        assertEquals("path", result);
    }

    @Test
    void testSetGetIsActive() {
        directoryUnderTest.setEnabled(false);
        final boolean resultFalse = directoryUnderTest.isEnabled();
        assertFalse(resultFalse);
        directoryUnderTest.setEnabled(true);
        final boolean resultTrue = directoryUnderTest.isEnabled();
        assertTrue(resultTrue);
    }

    @Test
    void testSetGetLastExistenceCheck() {
        directoryUnderTest.setLastExistenceCheck(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = directoryUnderTest.getLastExistenceCheck();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }

    @Test
    void testSetGetLastAccessByUser() {
        directoryUnderTest.setLastAccessByUser(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = directoryUnderTest.getLastAccessByUser();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }
}
