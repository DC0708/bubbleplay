package com.example.mapdemo;

/**
 * Created by DC0708 on 27-Feb-16.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://10.1.42.170/BubblePlayServer/";

    // Google project id
    static final String SENDER_ID = "531446716538";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.example.mapdemo.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "price";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message, String challengeID, String user) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra("challengeID",challengeID);
        intent.putExtra("user",user);
        intent.putExtra(EXTRA_MESSAGE, message);
        Log.d("sending broadcast", " in common utilities");
        context.sendBroadcast(intent);
    }
}