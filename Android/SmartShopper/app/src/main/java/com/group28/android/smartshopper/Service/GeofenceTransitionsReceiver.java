package com.group28.android.smartshopper.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.group28.android.smartshopper.Activity.HomeActivity;
import com.group28.android.smartshopper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mihir on 11/4/2016.
 */

public class GeofenceTransitionsReceiver extends BroadcastReceiver {

    protected static final String TAG = "GeofenceTransitionsReceiver";

    private static final String GROUP_ID = "SmartShopNotifs";

    private static int generatedId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }

        String requestId = event.getTriggeringGeofences().get(0).getRequestId();
        int transition = event.getGeofenceTransition();
        switch (transition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER: {
                Log.i(TAG, "Entered " + requestId);
                sendNotification("You're near " + requestId + ". Do you want to buy something?", context);
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT: {
                Log.i(TAG, "Exit from " + requestId);
                //sendNotification("Exit from " + requestId,context);
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_DWELL: {
                Log.i(TAG, "Dwelling at " + requestId );
                sendNotification("You were at " + requestId + ". Did you buy?", context);
                break;
            }
            default: {
                Log.i(TAG, "Default");
                break;
            }
        }
    }

    private void sendNotification(String notificationDetails, Context context) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, HomeActivity.class);

        // Construct a task stack.
        android.app.TaskStackBuilder stackBuilder = android.app.TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(HomeActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setColor(Color.RED)
                .setContentTitle("SmartShopper")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent)
                .setGroup(GROUP_ID);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(generatedId++, builder.build());
    }
}
