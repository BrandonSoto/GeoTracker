/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class RegistrationActivityTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {
    private Solo solo;

    public RegistrationActivityTest() {
        super(RegistrationActivity.class);
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

    public void testARequiredFields() {
        int i;
        solo.unlockScreen();

        solo.clickOnButton("Login");
        boolean textFound = solo.searchText("This field is required");
        assertTrue("Required fields validation failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "t");
        solo.clickOnButton("Login");
        textFound = solo.searchText("This email address is invalid");
        assertTrue("Login email address failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "est@dummy.com");
        solo.enterText(1, "p");
        solo.clickOnButton("Login");
        textFound = solo.searchText("Password must exceed 5 characters");
        assertTrue("Login password failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    public void testBOrientation() {
        int i;
        solo.clickOnButton("Register");
        solo.enterText(0, "test@dummy.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "security answer");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.setActivityOrientation(Solo.PORTRAIT);
        textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    public void testCLogin() {
        int i;
        solo.enterText(0, "loralyn@uw.edu");
        solo.enterText(1, "password");
        solo.clickOnButton("Login");
        boolean textFound = solo.searchText("loralyn@uw.edu has signed in!");
        assertTrue("Login failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

}
