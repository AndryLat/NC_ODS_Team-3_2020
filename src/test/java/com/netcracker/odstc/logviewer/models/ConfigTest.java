package com.netcracker.odstc.logviewer.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConfigTest {

    private Config configUnderTest;

    @BeforeEach
    void setUp() {
        configUnderTest = new Config();
    }

    @Test
    void testConstructors() {
        Config emptyConfig = new Config();
        assertNotNull(emptyConfig.getAttributes());
        assertNotNull(emptyConfig.getReferences());
        assertNull(emptyConfig.getObjectId());

        Config configWithId = new Config(BigInteger.valueOf(2048));
        assertNotNull(configWithId.getObjectId());
        assertEquals(BigInteger.valueOf(2048), configWithId.getObjectId());
    }

    @Test
    void testSetGetActivityPollingPeriod() {
        configUnderTest.setActivityPollingPeriod(0L);
        final long result = configUnderTest.getActivityPollingPeriod();
        assertEquals(0L, result);
    }

    @Test
    void testSetGetChangesPollingPeriod() {
        configUnderTest.setChangesPollingPeriod(0);
        final long result = configUnderTest.getChangesPollingPeriod();
        assertEquals(0, result);
    }

    @Test
    void testSetGetStorageLogPeriod() {
        configUnderTest.setStorageLogPeriod(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = configUnderTest.getStorageLogPeriod();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }

    @Test
    void testSetGetDirectoryActivityPeriod() {
        configUnderTest.setDirectoryActivityPeriod(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = configUnderTest.getDirectoryActivityPeriod();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }

    @Test
    void testSetGetServerActivityPeriod() {
        configUnderTest.setServerActivityPeriod(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = configUnderTest.getServerActivityPeriod();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }

    @Test
    void testGetInstance() {
        Config.setInstance(new Config());
        final Config result = Config.getInstance();
        assertEquals(configUnderTest, result);
    }
}
