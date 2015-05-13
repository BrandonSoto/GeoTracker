package edu.uw.ProjectMayhem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

//LocationBroadcastReceiver launches on boot.
public class LocationBroadcastReceiver extends BroadcastReceiver {
    //onReceive() launches on boot and starts location tracking.
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Log.d("BroadcastReceiver", "Boot completed!");

            Intent location = new Intent(context, LocationServices.class);
            context.startService(location);

        }
    }

}
