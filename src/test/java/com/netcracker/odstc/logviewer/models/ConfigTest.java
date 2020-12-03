package com.netcracker.odstc.logviewer.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class ConfigTest {
    private Config config;
    private short expected;

    private short newDate;

    private Date newPeriod = new Date(200);

    @BeforeAll
    void getInstance() {
        config = Config.getInstance();
        expected = 123;
        newDate = 521;
        config.setActivityPollingPeriod(expected);
        config.setChangesPollingPeriod(expected);
        config.setStorageLogPeriod(new Date(200));
    }

    @Test
    void contsrAllParam() {
        Config config = new Config(expected, newDate, newPeriod, newPeriod);
        assertEquals(expected, config.getChangesPollingPeriod());
        assertEquals(newDate, config.getActivityPollingPeriod());
        assertEquals(newPeriod, config.getStorageLogPeriod());
        assertEquals(newPeriod, config.getDirectoryActivityPeriod());
    }

    @Test
    void setChangesPollingPeriod() {
        config.setChangesPollingPeriod(newDate);
        assertEquals(config.getChangesPollingPeriod(), newDate);
    }

    @Test
    void setActivityPollingPeriod() {
        config.setActivityPollingPeriod(newDate);
        assertEquals(config.getActivityPollingPeriod(), newDate);
    }

    @Test
    void setStoragePeriod() {
        config.setStorageLogPeriod(newPeriod);
        assertEquals(config.getStorageLogPeriod(), newPeriod);
    }
}