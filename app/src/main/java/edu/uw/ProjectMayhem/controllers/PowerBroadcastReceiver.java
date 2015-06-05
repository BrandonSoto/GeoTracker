package edu.uw.ProjectMayhem.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
public class PowerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        If the device is connected to a power source, continue sampling every minute. Otherwise,
//        sample every 5 minutes to conserve battery. You could also check how much battery is left
//        and choose to sample at a larger interval


        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            /*************************************************
             * set sampling to 1 minute if using default rate
             *************************************************/
        } else {
            /*************************************************
             * set sampling to 5 minutes if using default rate
             *************************************************/
        }
    }
}
