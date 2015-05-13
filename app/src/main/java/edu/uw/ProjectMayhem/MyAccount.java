/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto
 */
package edu.uw.ProjectMayhem;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Displays the user's account information.
 */
public class MyAccount extends ActionBarActivity {

    /** start date datepicker. */
    private DatePicker mStartDate;

    /** end date datepicker. */
    private DatePicker mEndDate;

    /**
     * onCreate() generates MyAccount
     *  {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        // Start the service
        Intent i = new Intent(this, LocationServices.class);
        startService(i);

        ComponentName receiver = new ComponentName(this, LocationBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = prefs.getString("uid", "");
        Log.d("MyAccount", "User id is:" + uid);

        mStartDate = (DatePicker) findViewById(R.id.start_date);
        mEndDate = (DatePicker) findViewById(R.id.end_date);

        Button mTrajectoryButton = (Button) findViewById(R.id.trajectory_button);
        mTrajectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTrajectory(view);
            }
        });

        Button mDumpDataButton = (Button) findViewById(R.id.dump_data_button);
        mDumpDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dump data to Menaka's server
                MovementDBHandler myData = new MovementDBHandler(MyAccount.this);
                List<MovementData> allData = myData.getAllMovement();
                for (MovementData d : allData) {
                    Log.d("MyAccount", "Uploading data with timestamp: " + d.getTimeStamp());
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

                            if(o.get("result").equals("success")) {

                                Log.d("MyAccount", "Data uploaded successfully.");

                            } else {

                                Log.d("MyAccount", "Error uploading data.");

                            }
                        } catch (JSONException e) {
                            System.out.println("JSON Exception " + e);
                        }
                    }
                }
                myData.deleteAllMovement();
            }
        });
    }

    /**
     * Transitions to the trajectory screen.
     */
    private void myTrajectory(View view) {
        Intent trajectoryIntent = new Intent(this, MyTrajectory.class);
        Calendar startCal = Calendar.getInstance();
        startCal.set(mStartDate.getYear(), mStartDate.getMonth(), mStartDate.getDayOfMonth());
        Calendar endCal = Calendar.getInstance();
        endCal.set(mEndDate.getYear(), mEndDate.getMonth(), mEndDate.getDayOfMonth());
        trajectoryIntent.putExtra("Start Date", startCal.getTime());
        trajectoryIntent.putExtra("End Date", endCal.getTime());
        startActivity(trajectoryIntent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous task to sumbit a location update
     */
    public class DataUploadTask extends AsyncTask<Void, Void, String> {

        private String webURL = "http://450.atwebpages.com/logAdd.php";
        private double latitude;
        private double longitude;
        private double speed;
        private double heading;
        private String userid;
        private long timestamp;

        public DataUploadTask(double lat, double lon, double speed, double heading, String uid, long time) {
            super();
            latitude = lat;
            longitude = lon;
            this.speed = speed;
            this.heading = heading;
            userid = uid;
            timestamp = time;
        }


        /** {@inheritDoc} */
        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            HttpURLConnection connection;

            URL url = null;
            String response = null;
            String parameters = ("?lat=" + latitude
                                + "&lon=" + longitude
                                + "&speed=" + speed
                                + "&heading=" + heading
                                + "&source=" + userid
                                + "&timestamp=" + timestamp);
            try
            {
                Log.d("MyAccount", "Sending url: " + webURL + parameters);
                url = new URL(webURL + parameters);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("GET");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);

                result = reader.readLine();

                isr.close();
                reader.close();

            }
            catch(IOException e)
            {
                System.err.println("Something bad happened while sending HTTP GET");
            }

            return result;
        }

        /** {@inheritDoc} */
        @Override
        protected void onPostExecute(final String result) {

        }
    }
}
