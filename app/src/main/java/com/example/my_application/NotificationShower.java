package com.example.my_application;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.my_application.db.AppDB;
import com.example.my_application.db.CallEntity;

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
    }
}
