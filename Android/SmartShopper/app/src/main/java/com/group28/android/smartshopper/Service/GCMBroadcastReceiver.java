package com.group28.android.smartshopper.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by digantjagtap on 11/13/16.
 */

public class GCMBroadcastReceiver extends BroadcastReceiver {


    //When the broadcast received
    //We are sending the broadcast from GCMRegistrationIntentService

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("GCM","Inside custom receiver");

    }
}