package com.example.my_application;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.my_application.db.AppDB;
import com.example.my_application.db.CallEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationShower {

    public static void show(Context context, String phone) {
        String notifyId = "my_channel_1";

        NotificationChannel channel = null;   // for heads-up notifications
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(notifyId, "channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        AppDB db = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "contact.db").allowMainThreadQueries().build();
        CallEntity call = db.callDao().getCall(phone);




        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.info);
        if (call == null) {
            phone = "этого номера нет в базе данных";
            notificationLayout.setViewVisibility(R.id.layout_tags, View.GONE);
        } else {
            if (!call.isSpam) {
                notificationLayout.setViewVisibility(R.id.spam, View.GONE);
            }
            if (call.tags == null || call.tags.isEmpty()) {
                notificationLayout.setViewVisibility(R.id.info_tag, View.GONE);
            } else {
                String[] tags = call.tags.split(" ");
                for (String tag : tags) {
                    RemoteViews viewTag = new RemoteViews(context.getPackageName(), R.layout.tag);
                    viewTag.setTextViewText(R.id.info_tag, tag);
                    notificationLayout.addView(R.id.layout_tags, viewTag);
                }

            }
        }
        notificationLayout.setTextViewText(R.id.tv_info_number, phone);


        Notification notify = new NotificationCompat.Builder(context, notifyId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(phone)
                .setChannelId("my_channel_1")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContent(notificationLayout)
                .build();

        notificationManager.notify(1, notify);

        ifSpamCalling(context, call);
    }

    private static void ifSpamCalling(Context context, CallEntity call) {
        if (call != null && call.isSpam) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            boolean notSpam = sharedPreferences.getBoolean("spam", false);
            Log.debug(String.valueOf(notSpam));
            if (notSpam) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                        if (tm == null) {
                            throw new NullPointerException("tm == null");
                        }

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        telecomManager.endCall();
                    } else {
                        disconnect(tm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void disconnect(TelephonyManager tm) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class c = Class.forName(tm.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
        c = Class.forName(telephonyService.getClass().getName()); // Get its class
        m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
        m.setAccessible(true); // Make it accessible
        m.invoke(telephonyService); // invoke endCall()
    }
}
