/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto
 */
package edu.uw.ProjectMayhem;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Displays the user's trajectory information.
 */
public class MyTrajectory extends FragmentActivity {

    /**
     * map used for placeholder at the moment for my trajectory activity.
     */
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /** movement database handler. */
    private MovementDBHandler myData;

    /** start date. */
    private Date mStartDate;

    /** end date. */
    private Date mEndDate;

    /**
     * onCreate method creates the MyTrajectory activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trajectory);

        Bundle extras = getIntent().getExtras();

        mStartDate = (Date) extras.get("Start Date");
        mEndDate = (Date) extras.get("End Date");

        myData = new MovementDBHandler(this);
        List<MovementData> moves = myData.getAllMovement();

        for (MovementData m : moves) {

            StringBuilder sb = new StringBuilder();

            sb.append(m.getLatitude());
            sb.append("----");
            sb.append(m.getLongitude());
            sb.append("----");
            sb.append(m.getSpeed());
            sb.append("----");
            sb.append(m.getHeading());
            sb.append("----");
//            sb.append(m.getSourceID());
            sb.append("----");
            sb.append(m.getTimeStamp());

            Log.d("+++++++++++++++", "TEST DATA TEST************");
            Log.d("Data: ", sb.toString());

            String c = myData.getMovementCount()+"";

            Log.d("+++++++++++++++","COUNT************");
            Log.d("Count: ", c);
        }


        setUpMapIfNeeded();
    }

    /**
     * Sets onResume() functionality.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.d("MyTrajectory", "Setting up the map...");
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * add all dates within the range to the map with title "Position".
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        List<MovementData> data = new ArrayList<MovementData>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String uid = prefs.getString("uid", "");

        Log.d("MyTrajectory", "User id is:" + uid);

        DataDownloadTask task = new DataDownloadTask(uid,
                (mStartDate.getTime() / 1000),
                (mEndDate.getTime() / 1000));
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

                    // Now parse the JSON array
                    JSONArray myArr = o.getJSONArray("points");

                    Log.d("MyTrajectory", "Size of data array is: " + myArr.length());

                    for (int i = 0; i < myArr.length(); i++) {
                        JSONObject obj = myArr.getJSONObject(i);
                        MovementData move = new MovementData(Double.parseDouble(obj.getString("lat")),
                                Double.parseDouble(obj.getString("lon")),
                                Double.parseDouble(obj.getString("speed")),
                                Double.parseDouble(obj.getString("heading")),
                                uid,
                                Long.parseLong(obj.getString("time")));
                        data.add(move);
                    }

                } else {

                    Toast.makeText(this, o.get("error").toString(), Toast.LENGTH_LONG).show();
                    Log.d("MyTrajectory", "Error fetching locations");

                }
            } catch (JSONException e) {
                System.out.println("JSON Exception " + e);
            }
        }
        LatLng latest = new LatLng(0,0);

        final PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(5);

        for (final MovementData m : data) {
            latest = new LatLng(m.getLatitude(), m.getLongitude());
            polylineOptions.add(latest);

            mMap.addMarker(new MarkerOptions().position(latest).title("Position"));

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latest, 5));
        mMap.addPolyline(polylineOptions);
    }

    /**
     * Represents an asynchronous task to pull down locations
     */
    public class DataDownloadTask extends AsyncTask<Void, Void, String> {

        private String webURL = "http://450.atwebpages.com/view.php";
        private String userid;
        private String startDate;
        private String endDate;

        public DataDownloadTask(String uid, long start, long end) {
            super();
            userid = uid;
            startDate = new Long(start).toString();
            endDate = new Long(end).toString();

        }


        /** {@inheritDoc} */
        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            HttpURLConnection connection;

            URL url = null;
            String parameters = ("?uid=" + userid
                                + "&start=" + startDate
                                + "&end=" + endDate);
            try
            {
                Log.d("MyTrajectory", "Sending url: " + webURL + parameters);
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
