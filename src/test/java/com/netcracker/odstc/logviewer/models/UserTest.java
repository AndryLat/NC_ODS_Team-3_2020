package com.netcracker.odstc.logviewer.models;

import com.netcracker.odstc.logviewer.models.lists.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    private User userUnderTest;

    @BeforeEach
    void setUp() {
        userUnderTest = new User("email@gmail.com", "velomit", "qwerty", Role.ADMIN, new BigInteger("1"));
    }

    @Test
    void testConstructors() {
        User emptyUser = new User();
        assertNotNull(emptyUser.getAttributes());
        assertNotNull(emptyUser.getReferences());
        assertNull(emptyUser.getObjectId());

        User userWithId = new User(BigInteger.valueOf(128));
        assertNotNull(userWithId.getObjectId());
        assertEquals(BigInteger.valueOf(128), userWithId.getObjectId());
    }

    @Test
    void testSetGetEmail() {
        userUnderTest.setEmail("email");
        final String result = userUnderTest.getEmail();
        assertEquals("email", result);
    }

    @Test
    void testSetGetLogin() {
        userUnderTest.setLogin("login");
        final String result = userUnderTest.getLogin();
        assertEquals("login", result);
    }

    @Test
    void testSetGetPassword() {
        userUnderTest.setPassword("password");
        final String result = userUnderTest.getPassword();
        assertEquals("password", result);
    }

    @Test
    void testSetGetRole() {
        userUnderTest.setRole(Role.ADMIN);
        final Role result = userUnderTest.getRole();
        assertEquals(Role.ADMIN, result);
    }

    @Test
    void testSetGetCreated() {
        userUnderTest.setCreated(new BigInteger("1"));
        final BigInteger result = userUnderTest.getCreated();
        assertEquals(new BigInteger("1"), result);
    }
}
