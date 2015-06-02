/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void testRequiredFields() {
        solo.unlockScreen();

        solo.enterText(0, "");
        solo.clickOnButton("Login");
        boolean textFound = solo.searchText("This field is required");
        assertTrue("Required fields validation failed", textFound);
    }

    public void testOrientation() {
        solo.clickOnButton("Register");
        solo.enterText(0, "test@dummy.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "Your first pet's name");
        solo.enterText(4, "security answer");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);

        solo.setActivityOrientation(Solo.PORTRAIT);
        textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);
    }

    public void testLogin() {
        solo.enterText(0, "loralyn@uw.edu");
        solo.enterText(1, "password");
        solo.clickOnButton("Login");
        boolean textFound = solo.searchText("loralyn@uw.edu has signed in!");
        assertTrue("Login failed", textFound);
    }

}
