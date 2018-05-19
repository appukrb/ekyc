package com.tcs.mmpl.customer.utility;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tcs.mmpl.customer.Activity.AlertActivity;
import com.tcs.mmpl.customer.Activity.Electricity_Payment;
import com.tcs.mmpl.customer.Activity.GCIActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.WebActivity;
import com.tcs.mmpl.customer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCM";
    NotificationCompat.Builder builder;
    MyConnectionHelper db;

    Bitmap Images = null;
    private NotificationManager mNotificationManager;
    private SharedPreferences userInfoPref;
    private SharedPreferences.Editor userInfoEditor;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        db = new MyConnectionHelper(this);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString(),"","","");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " + extras.toString(),"");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.


                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                String messsage = extras.getString("message");

                //// System.out.println(messsage);

                String heading = "";
                String msg = "", image = "", action = "", link = "";
                try {
                    JSONObject jsonObject = new JSONObject(messsage);

                    heading = jsonObject.getString("heading");
                    msg = jsonObject.getString("msg");
                    image = jsonObject.getString("image");
                    action = jsonObject.getString("action");
                    link = jsonObject.getString("link");
                    if (!image.trim().equalsIgnoreCase(""))
                        getBitmapFromURL(image, msg, heading, action, link);
                    else {
                        Images = null;
                        sendNotification(msg, heading, action, link, Images);
                    }


//                    Images = BitmapFactory.decodeResource(getResources(),
//                            R.drawable.ic_launcher);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
                String alertTime = formatter.format(today);
                db.fun_insertAll_tbl_alerts(msg, alertTime, heading, action, link);

                Log.i(TAG, "Received: " + extras.toString());


            }
        }


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification(String msg, String heading, String action, String link, Bitmap myBitmap) {


        userInfoPref = this.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

//        // System.out.println("url:"+url);
//        SendNotficationClicked sendNotficationClicked = new SendNotficationClicked(this, msg);
//        sendNotficationClicked.execute(url);


        userInfoEditor.putString("msg",Uri.encode(msg+"|"+heading,"utf-8"));

        userInfoEditor.putString("notify","true");
        userInfoEditor.commit();



        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;

        if (action.equalsIgnoreCase("HOME")) {
            notificationIntent = new Intent(this, HomeScreenActivity.class);
        } else if (action.equalsIgnoreCase("RECHARGE")) {
            notificationIntent = new Intent(this, Electricity_Payment.class);
        } else if (action.equalsIgnoreCase("COUPON")) {

//            GiftVoucher giftVoucher = new GiftVoucher(AlertActivity.this);
//            giftVoucher.execute(getResources().getString(R.string.gift_url) + "?MDN=" + pref.getString("mobile_number", ""));

            notificationIntent = new Intent(this, AlertActivity.class);
        } else if (action.equalsIgnoreCase("GCI")) {
            notificationIntent = new Intent(this, GCIActivity.class);
        } else if (action.equalsIgnoreCase("URLIN")) {
            notificationIntent = new Intent(this, WebActivity.class);
            notificationIntent.putExtra("option", "LINK");
            notificationIntent.putExtra("url", link);
        } else if (action.equalsIgnoreCase("URLOUT")) {
            notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        } else {
            notificationIntent = new Intent(this, AlertActivity.class);
        }


        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        int imageid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            imageid = R.drawable.notification_transparent_icon_new1;
        else
            imageid = R.drawable.notification_icon_new;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int color = Color.parseColor("#1168b3");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(imageid)
                            .setContentTitle(heading);
            mBuilder.setContentText(msg);

            if (myBitmap == null) {
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
            } else {

                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap).setSummaryText(msg));
            }

            mBuilder.setColor(color);
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


            mBuilder.setAutoCancel(true);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {

            int color = Color.parseColor("#FFFFFF");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(imageid)
                            .setContentTitle(heading);
            mBuilder.setContentText(msg);
            if (myBitmap == null) {
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
            } else {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap).setSummaryText(msg));
                //// System.out.println("Else part");
            }
//            mBuilder.setColor(color);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


            mBuilder.setAutoCancel(true);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private Bitmap getBitmapFromURL(String imgURL, String msg, String heading, String action, String link) {
        try {


            Log.i("Image URL", imgURL);
            // Download Image from URL
            InputStream input = new java.net.URL(imgURL).openStream();
            // Decode Bitmap
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

//            URL url = new URL(imgURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //// System.out.println("Downloading......." + myBitmap);


            if (android.os.Build.VERSION.SDK_INT >= 16) {

                CustomNotification(msg, heading, action, link, myBitmap);
            }
            else
            {
                sendNotification(msg, heading, action, link, myBitmap);
            }


            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class SendNotficationClicked extends AsyncTask<String, Void, String> {

        Context context;
        private ProgressDialog pDialog;
        private SharedPreferences pref;

        private String msg;

        public SendNotficationClicked(Context context, String msg) {
            this.context = context;
            this.msg = msg;
            pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
//                jsonObject.accumulate("MSG", msg);
//
//
//                // 4. convert JSONObject to JSON to String
//                String json = jsonObject.toString();
//
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);


                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println(arg0[0].toString());
                Log.d("notification click", jsonStr);


            } catch (Exception e) {

            }

            return "success";
        }

        @Override
        protected void onPostExecute(String result) {


        }

    }


    private void CustomNotification(String msg, String heading, String action, String link, Bitmap myBitmap) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.custom_notification);


//        // System.out.println("url:"+url);
//        SendNotficationClicked sendNotficationClicked = new SendNotficationClicked(this, msg);
//        sendNotficationClicked.execute(url);

        userInfoPref = this.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();



        userInfoEditor.putString("msg",Uri.encode(msg+"|"+heading,"utf-8"));
        userInfoEditor.putString("notify","true");
        userInfoEditor.commit();


        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;

        if (action.equalsIgnoreCase("HOME")) {
            notificationIntent = new Intent(this, HomeScreenActivity.class);
        } else if (action.equalsIgnoreCase("RECHARGE")) {
            notificationIntent = new Intent(this, Electricity_Payment.class);
        } else if (action.equalsIgnoreCase("COUPON")) {

//            GiftVoucher giftVoucher = new GiftVoucher(AlertActivity.this);
//            giftVoucher.execute(getResources().getString(R.string.gift_url) + "?MDN=" + pref.getString("mobile_number", ""));

            notificationIntent = new Intent(this, AlertActivity.class);
        } else if (action.equalsIgnoreCase("GCI")) {
            notificationIntent = new Intent(this, GCIActivity.class);
        } else if (action.equalsIgnoreCase("URLIN")) {
            notificationIntent = new Intent(this, WebActivity.class);
            notificationIntent.putExtra("option", "LINK");
            notificationIntent.putExtra("url", link);
        } else if (action.equalsIgnoreCase("URLOUT")) {
            notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        } else {
            notificationIntent = new Intent(this, AlertActivity.class);
        }


        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        int imageid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            imageid = R.drawable.notification_transparent_icon_new1;
        else
            imageid = R.drawable.notification_icon_new;

//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

            int color = Color.parseColor("#1168b3");



        Notification builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(imageid)
                .setContentTitle(heading)
                .setContentText(msg)
                // Set Ticker Message
                .setTicker(heading)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap).setSummaryText(msg))
                .setColor(color)
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(contentIntent).build();


        // Locate and set the Image into customnotificationtext.xml ImageViews

        remoteViews.setImageViewBitmap(R.id.image,myBitmap);
        remoteViews.setTextViewText(R.id.txtTitle,heading);
        remoteViews.setTextViewText(R.id.txtSummary,msg);



            remoteViews.setImageViewResource(R.id.imgIconTrans, R.drawable.notification_transparent_icon_new1);


        if (android.os.Build.VERSION.SDK_INT >= 16) {
            builder.bigContentView = remoteViews;
        }



        builder.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(NOTIFICATION_ID, builder);

    }


}

