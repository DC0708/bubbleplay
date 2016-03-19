package com.example.mapdemo;

/**
 * Created by DC0708 on 27-Feb-16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import static com.example.mapdemo.CommonUtilities.SENDER_ID;
import static com.example.mapdemo.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    SharedPreferences sp;




    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
    //    Log.d("NAME", GCMMainActivity.name);
    //    ServerUtilities.register(context, GCMMainActivity.name, GCMMainActivity.email, registrationId,);
    }

    /**
     * Method called on device unregistred
     **/
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     **/
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        Bundle xtra = intent.getExtras();
        String message = intent.getExtras().getString("message");


        for (String key: xtra.keySet())
        {
            Log.d("values ", xtra.get(key)+ "volaa");
            Log.d ("myApplication", key + " is a key in the bundle");
        }

        Log.d("Message is ",message + "!!!!!!!!!!!!!!!!!!!");
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     **/
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, String message) {

        Log.d("in generate notification","yooo!!!");

        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserSession",0);

        int icon = R.drawable.icon;

        //Bitmap bd = (((Bitmap)c.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
//      Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);

        Log.d("notification is : ","generated");

        String username = "USERNAME!";

        Intent intent = new Intent(getApplicationContext(), Challenge.class);



        intent.putExtra("challengeID",sp.getString("challengeID","challengeID"));
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);



        Notification notification = new Notification.Builder(context)
                .setContentTitle("New Challenge request from " + username)
                .setContentText("Accept to start the challenge.")
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
                .setWhen(when)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        //Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =
//                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(context, title, message, intent);


        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }

}
