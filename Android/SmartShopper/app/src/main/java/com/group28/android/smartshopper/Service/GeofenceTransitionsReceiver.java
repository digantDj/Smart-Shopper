package com.group28.android.smartshopper.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.group28.android.smartshopper.Activity.HomeActivity;
import com.group28.android.smartshopper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mihir on 11/4/2016.
 */

public class GeofenceTransitionsReceiver extends BroadcastReceiver implements GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "GeofenceTransitionsReceiver";

    private static final String GROUP_ID = "SmartShopNotifs";

    private static int generatedId = 0;

    private Timer placeTimer = new Timer();
    private static final long DWELL_WAIT_TIME =  60*1000L; //in milliseconds
    private  String placeForTimer = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }

        String requestId = event.getTriggeringGeofences().get(0).getRequestId();
        if(requestId.equals("UserGeoFence")){
            // TODO: when writing logic for EXIT usergeofence, remove the return;
            return;
        }
        int transition = event.getGeofenceTransition();
        switch (transition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER: {
                placeForTimer  = requestId;
                //TODO: for intersecting fences
                setTimerStart(requestId, context);

                Log.i(TAG, "Entered " + requestId);
                sendNotification("You're near " + requestId + ". Do you want to buy something?", context);

                break;
            }
            case Geofence.GEOFENCE_TRANSITION_EXIT: {
                Log.i(TAG, "Exit from " + requestId);
                //sendNotification("Exit from " + requestId,context);
                if(placeForTimer.equals(requestId)){
                    placeTimer.cancel();
                    placeForTimer = "";
                }
                //TODO:  for UserGeoFence recompute userGeoFence again
                break;
            }
            case Geofence.GEOFENCE_TRANSITION_DWELL: {
                Log.i(TAG, "Dwelling at " + requestId);
                sendNotification("You were at " + requestId + ". Did you buy?", context);
                break;
            }
            default: {
                Log.i(TAG, "Default");
                break;
            }
        }
    }

    private void setTimerStart(final String placeName, final Context context) {
        Timer timer = new Timer();
        Log.i(TAG, "TimerTask Method Started.");
        timer.scheduleAtFixedRate(new TimerTask() {
            private String TAG = "PlacesTimer";

            @Override
            public void run() {
                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();

                if (mGoogleApiClient.isConnected()) {
                    Log.i(TAG, "Connected to API server.");
                } else {
                    Log.i(TAG, "GoogleApiClient not connected. Connecting...");
                    mGoogleApiClient.connect();
                    Log.i(TAG, "Connected again.");
                }
                if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)) {
                    Log.e(TAG, "Broken Permissions in TimerTask.run()");
                    return;
                }
                Log.i(TAG, "Getting Your Current Place.");

                com.google.android.gms.common.api.PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                Log.i(TAG, "PlaceApiObject: " + result.toString());

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                        Log.i(TAG, "ResultCallback");
                        Log.i(TAG, "no of likely places : " + likelyPlaces.getCount());
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Log.i(TAG, placeLikelihood.getPlace().getName().toString());
                            if (placeLikelihood.getPlace().getName().toString().equals(placeName)) {
                                sendNotification("You were at: " + placeName +". Did you buy anything?", context);
                            }
                        }
                        likelyPlaces.release();
                    }
                });

            }

        }, DWELL_WAIT_TIME, DWELL_WAIT_TIME);


        Log.i(TAG, "TimerTask Started.");
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
