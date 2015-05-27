package edu.uw.ProjectMayhem.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
public class PowerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int powerStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        boolean isDeviceCharging = powerStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                powerStatus == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        boolean isChargeMethodUSB = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean isChargeMethodAC = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Log.d("+++++++++++++++++++", "Power Level:" + intent.getAction().toString());
    }
}
