package com.example.coursework1;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context, "You have a trip coming up soon! Open the app to see the details.");

    }

    public void sendNotification(Context context, String text){ // send notification when alarm is triggered (for trip upcoming trip date)

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "TravelBudNotification")
                        .setSmallIcon(R.mipmap.travelbud_icon)
                        .setContentTitle("Trip Details Service")
                        .setContentText(text);

        Intent resultIntent = new Intent(context, HomePageActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomePageActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(111, mBuilder.build());
    }

}
