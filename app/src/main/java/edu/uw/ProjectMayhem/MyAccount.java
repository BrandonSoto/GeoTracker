/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto
 */
package edu.uw.ProjectMayhem;

import android.app.DatePickerDialog;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Displays the user's account information.
 */
public class MyAccount extends ActionBarActivity implements View.OnClickListener {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    private TextView mStartDateText;
    private TextView mEndDateText;
    private Button mStartButton;
    private Button mEndButton;
    private DatePickerDialog mStartDateDialog;
    private DatePickerDialog mEndDateDialog;
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;

    private Intent locationServiceIntent;

    /**
     * onCreate() generates MyAccount
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        // Start the service
        locationServiceIntent = new Intent(this, LocationServices.class);
        startService(locationServiceIntent);

        ComponentName receiver = new ComponentName(this, LocationBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = prefs.getString("uid", "");
        Log.d("MyAccount", "User id is:" + uid);

        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();

        mStartDateText = (TextView) findViewById(R.id.start_date_textview);
        mEndDateText = (TextView) findViewById(R.id.end_date_textview);

        mStartButton = (Button) findViewById(R.id.start_date_button);
        mEndButton = (Button) findViewById(R.id.end_date_button);

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

                            if (o.get("result").equals("success")) {

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

        setupDateDialogs();
    }

    private void setupDateDialogs() {
        final Calendar initial_calendar = Calendar.getInstance();
        final String start = getString(R.string.start_date);
        final String end = getString(R.string.end_date);


        mStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mStartCalendar.set(year, monthOfYear, dayOfMonth);
                mStartDateText.setText(start + ": " + DATE_FORMATTER.format(mStartCalendar.getTime()));
            }
        }, initial_calendar.get(Calendar.YEAR), initial_calendar.get(Calendar.MONTH),
                initial_calendar.get(Calendar.DAY_OF_MONTH));

        mEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mEndCalendar.set(year, monthOfYear, dayOfMonth);
                mEndDateText.setText(end + ": " + DATE_FORMATTER.format(mEndCalendar.getTime()));
            }
        }, initial_calendar.get(Calendar.YEAR), initial_calendar.get(Calendar.MONTH),
                initial_calendar.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Transitions to the trajectory screen.
     */
    private void myTrajectory(View view) {
        final Intent trajectoryIntent = new Intent(this, MyTrajectory.class);
        trajectoryIntent.putExtra("Start Date", mStartCalendar.getTime());
        trajectoryIntent.putExtra("End Date", mEndCalendar.getTime());

        startActivity(trajectoryIntent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Displays the settings activity. */
    private void showSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    /** Logs out the user and stops tracking. */
    private void logout() {

        // Stop tracking
        stopService(locationServiceIntent);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putBoolean("tracking", false);
        spe.apply();

        // Go to the login page
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackPressed() {
        // Go to the login page
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mStartButton) {
            mStartDateDialog.show();
        } else if (v == mEndButton) {
            mEndDateDialog.show();
        }
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


        /**
         * {@inheritDoc}
         */
        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            HttpURLConnection connection;

            URL url;
            String parameters = ("?lat=" + latitude
                    + "&lon=" + longitude
                    + "&speed=" + speed
                    + "&heading=" + heading
                    + "&source=" + userid
                    + "&timestamp=" + timestamp);
            try {
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

            } catch (IOException e) {
                System.err.println("Something bad happened while sending HTTP GET");
            }

            return result;
        }
    }
}
