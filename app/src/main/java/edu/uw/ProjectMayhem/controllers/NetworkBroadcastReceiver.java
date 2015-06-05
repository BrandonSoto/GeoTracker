package edu.uw.ProjectMayhem.controllers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import edu.uw.ProjectMayhem.R;
import edu.uw.ProjectMayhem.model.UploadService;

/**
 * Created by Brian on 6/1/2015.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private NotificationManager mManager;
    private NotificationCompat.Builder mNotification;

    @Override
    public void onReceive(Context context, Intent intent) {

//        If the user hasn’t selected a sampling rate and there is network connectivity, sample every
//        minute. If the network is unavailable then wait for the message that the network is
//        available. Restart sampling every minute once the state is available.

        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_mylocation);

        final Intent upload = new Intent(context, UploadService.class);

        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        final boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected) {
            /*************************************************
             * set sampling to 1 minute if using default rate
             *************************************************/
            context.startService(upload);
            mNotification.setContentText("Network Connected! - Upload On");
        } else {
            context.stopService(upload);
            mNotification.setContentText("Network Disconnected! - Upload Off");
        }

        mManager.notify(2, mNotification.build());
    }
}
