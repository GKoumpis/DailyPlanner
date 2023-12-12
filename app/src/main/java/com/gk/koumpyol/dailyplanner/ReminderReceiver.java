package com.gk.koumpyol.dailyplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int remId = intent.getIntExtra("reminder_id", -1);
        String remString = intent.getStringExtra("reminder_string");
        String eventName = intent.getStringExtra("event_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "eventReminderID")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(eventName)
                .setContentText("Event " + eventName + " starts in " + remString)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(remId, builder.build());
    }
}
