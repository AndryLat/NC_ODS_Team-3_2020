package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private Server serverUnderTest;

    @BeforeEach
    void setUp() {
        serverUnderTest = new Server("ip", 0, "velomit", "qwerty", Protocol.FTP);
    }

    @Test
    void testConstructors() {
        Server emptyServer = new Server();
        assertNotNull(emptyServer.getAttributes());
        assertNotNull(emptyServer.getReferences());
        assertNull(emptyServer.getObjectId());

        Server serverWithId = new Server(BigInteger.valueOf(256));
        assertNotNull(serverWithId.getObjectId());
        assertEquals(BigInteger.valueOf(256), serverWithId.getObjectId());
    }

    @Test
    void testGetSetIp() {
        serverUnderTest.setIp("127.1.1.1");
        final String result = serverUnderTest.getIp();
        assertEquals("127.1.1.1", result);
    }

    @Test
    void testGetSetLogin() {
        serverUnderTest.setLogin("login");
        final String result = serverUnderTest.getLogin();
        assertEquals("login", result);
    }

    @Test
    void testSetGetPassword() {
        serverUnderTest.setPassword("password");
        final String result = serverUnderTest.getPassword();
        assertEquals("password", result);
    }

    @Test
    void testSetGetProtocol() {
        serverUnderTest.setProtocol(Protocol.SSH);
        final Protocol result = serverUnderTest.getProtocol();
        assertEquals(Protocol.SSH, result);
    }

    @Test
    void testSetGetPort() {
        serverUnderTest.setPort(8080);
        final int result = serverUnderTest.getPort();
        assertEquals(8080, result);
    }

    @Test
    void testSetGetIsActive() {
        serverUnderTest.setEnabled(false);
        final boolean resultFalse = serverUnderTest.isEnabled();
        assertFalse(resultFalse);
        serverUnderTest.setEnabled(true);
        final boolean resultTrue = serverUnderTest.isEnabled();
        assertTrue(resultTrue);
    }

    @Test
    void testSetGetLastAccessByJob() {
        serverUnderTest.setLastAccessByJob(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = serverUnderTest.getLastAccessByJob();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }

    @Test
    void testSetGetLastAccessByUser() {
        serverUnderTest.setLastAccessByUser(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime());
        final Date result = serverUnderTest.getLastAccessByUser();
        assertEquals(new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime(), result);
    }
}
