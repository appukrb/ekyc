package com.tcs.mmpl.customer.utility;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.R;

/**
 * Created by hp on 2016-12-02.
 */
public class LocalNotification {


    private Context context;
    private String notificationMessage, notificationTitle;

    private Activity activity;

    public LocalNotification(Context context, String notificationTitle, String notificationMessage) {
        this.context = context;
        this.notificationMessage = notificationMessage;
        this.notificationTitle = notificationTitle;
        this.activity = (Activity) context;
    }

    public void sendNotification() {

        int imageid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            imageid = R.drawable.notification_transparent_icon_new1;
        else
            imageid = R.drawable.notification_icon_new;


        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //use the flag FLAG_UPDATE_CURRENT to override any notification already there
        PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(imageid)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);


        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(notificationMessage);
        mBuilder.setStyle(bigTextStyle);
        mBuilder.setContentIntent(contentIntent);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int color = Color.parseColor("#1168b3");
            mBuilder.setColor(color);
        }

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setAutoCancel(true);
        notificationManager.notify(1, mBuilder.build());


    }



}
