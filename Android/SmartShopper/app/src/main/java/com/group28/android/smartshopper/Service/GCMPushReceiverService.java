package com.group28.android.smartshopper.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.group28.android.smartshopper.Activity.MainActivity;
import com.group28.android.smartshopper.Activity.RecommendSuccess;
import com.group28.android.smartshopper.R;

import static android.R.attr.data;
import static android.R.id.message;

/**
 * Created by deepika on 11/6/2016.
 */

public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
       // String message = data.getString("message");
        //Displaying a notiffication with the message
        sendNotification(data);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(Bundle data) {
       // Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, RecommendSuccess.class);
        String catValue = data.getString("category");
        String placeValue = data.getString("place");

        intent.putExtra("category", catValue);
        intent.putExtra("place",placeValue);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       /* NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
                */

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.common_plus_signin_btn_icon_light, "Accept", pendingIntent).build();

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(data.getString("message"))
                .setAutoCancel(true)
                .setContentTitle(data.getString("title"))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(data.getString("message")))
                //.addAction(R.drawable.ic_launcher, "Accept",pendingIntent);
                .addAction(action);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
      //  notificationManager.cancelAll();
    }
}
