package com.gk.koumpyol.dailyplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int notId = intent.getIntExtra("notification_id", -1);
        String notString = intent.getStringExtra("notification_string");
        String eventName = intent.getStringExtra("event_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "eventNotificationID")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(eventName)
                .setContentText("Event " + eventName + " starts in " + notString)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notId, builder.build());
    }
}
