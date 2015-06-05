/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.model;

import junit.framework.TestCase;

import edu.uw.ProjectMayhem.model.User;

/**UserTest tests the User class. */
public class UserTest extends TestCase{
    /**User for the tests. */
    private User mUser;

    /** sets up conditions prior to each test. */
    public void setUp() {
        mUser = new User("TestDummy", "test@dummy.com", "test", "Where were you born?", "Aberdeen");
    }

    /**tests User constructor. */
    public void testConstructor() {
        User user = new User("id", "email@email.com", "password", "question", "answer");
        assertNotNull(user);
    }

    /**tests getUserID(). */
    public void testGetUserID() {
        assertEquals("getUserID() failed!", "TestDummy", "TestDummy");
    }

    /**tests getEmail(). */
    public void testGetEmail() {
        assertEquals("getEmail() failed!", "test@dummy.com", "test@dummy.com");
    }
    /**tests getPassword(). */
    public void testGetPassword() {
        assertEquals("getPassword() failed!", "test", "test");
    }
    /**tests getSecurityQuestion(). */
    public void testGetSecurityQuestion() {
        assertEquals("getSecurityQuestion() failed!", "Where were you born?", "Where were you born?");
    }
    /**tests getSecurityAnswer(). */
    public void testGetSecurityAnswer() {
        assertEquals("getSecurityAnswer() failed!", "Aberdeen", "Aberdeen");
    }
}
