package edu.uw.ProjectMayhem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;


public class SettingsActivity extends ActionBarActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final Intent locationServiceIntent = new Intent (this, LocationServices.class);
        final boolean trackingState = prefs.getBoolean("tracking", false);

        final Switch mTrackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        mTrackingSwitch.setChecked(trackingState);
        if (trackingState) {
            mTrackingSwitch.setText(R.string.tracking_on);
        } else {
            mTrackingSwitch.setText(R.string.tracking_off);
        }
        mTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("tracking", true);
                    editor.apply();
                    startService(locationServiceIntent);
                    mTrackingSwitch.setText(R.string.tracking_on);
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("tracking", false);
                    editor.apply();
                    stopService(locationServiceIntent);
                    mTrackingSwitch.setText(R.string.tracking_off);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            // Same effect as pressing the back button (useful for phones that don't have one)
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
