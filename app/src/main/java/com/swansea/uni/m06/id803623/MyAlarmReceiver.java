package com.swansea.uni.m06.id803623;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;
    private final String myBlog = "http://android-er.blogspot.com/";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service = new Intent(context, MyAlarmService.class);
        Long mRowId = intent.getExtras().getLong(NotesDbAdapter.KEY_ROW_ID);
        service.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);
        Log.d("MyAlarmSReceiver", "id" + mRowId);

        context.startService(service);

    }
}
