/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.uw.ProjectMayhem.data.MovementDBHandler;
import edu.uw.ProjectMayhem.data.MovementData;

/**UploadService uploads movement data to web services. */
public class UploadService extends Service {

    /**upload timer. */
    private Timer uploadTimer;
    /**upload interval. */
    private static final int UPLOAD_INTERVAL = 3600;

    /**onCreate() instantiates UploadService. */
    @Override
    public void onCreate() {

        super.onCreate();

        // Start the location update timer
        uploadTimer = new Timer();
        uploadTimer.scheduleAtFixedRate(new UploadService.UploadTimer(), 0, UPLOAD_INTERVAL);
        Log.d("LocationInterval", ("Location timer started with interval: " + UPLOAD_INTERVAL));

    }

    /**onBind returns null. */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**getContext() retrieves application context. */
    public Context getContext() {
        return getApplicationContext();
    }

    /**manualUpload() uploads data to web services when user overrides the timer. */
    public static void manualUpload(Context context) {

        MovementDBHandler dbHandler = new MovementDBHandler(context);

        uploadData(dbHandler, context);

    }

    /**uploadData() uploads movement data to the web services. */
    private static void uploadData(MovementDBHandler locationDatabase, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String uid = prefs.getString("uid", "");
        Log.d("MyAccountActivity", "User id is:" + uid);

        List<MovementData> allData = locationDatabase.getAllMovement();
        for (MovementData d : allData) {
            Log.d("MyAccountActivity", "Uploading data with timestamp: " + d.getTimeStamp());
            DataUploadTask task = new DataUploadTask(d.getLatitude(),
                    d.getLongitude(),
                    d.getSpeed(),
                    d.getHeading(),
                    uid,
                    d.getTimeStamp());
            task.execute();
            String response = "";

            try {
                response = task.get();
            } catch (Exception e) {
                System.err.println("Something bad happened while parsing JSON");
            }

            System.out.println("response: " + response);

            if (response != null) {
                try {

                    JSONObject o = new JSONObject(response);

                    if (o.get("result").equals("success")) {

                        Log.d("MyAccountActivity", "Data uploaded successfully.");

                    } else {

                        Log.d("MyAccountActivity", "Error uploading data.");

                    }
                } catch (JSONException e) {
                    System.out.println("JSON Exception " + e);
                }
            }
        }
        locationDatabase.deleteAllMovement();

    }


    /** Uploads data to the server on a timer. */
    public class UploadTimer extends TimerTask {

        MovementDBHandler locationDatabase = new MovementDBHandler(getContext());

        /** LocationUpdate() constructor. */
        public UploadTimer() {
            super();
        }

        /** Runs the LocationUpdate features. */
        @Override
        public void run() {

            uploadData(locationDatabase, getContext());

        }
    }

}
