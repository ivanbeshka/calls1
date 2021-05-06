package com.example.my_application;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.debug("onReceive: ");
        if (intent.getAction().equals("android.intent.action.PHONE_STATE_CHANGED")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.debug("Show window: " + phoneNumber);
                showWindow(context, phoneNumber);
            }
        }
    }

    void showWindow(Context context, String phone) {
        NotificationShower.show(context, phone);
    }
}
