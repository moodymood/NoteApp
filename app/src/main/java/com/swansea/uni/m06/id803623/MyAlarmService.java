package com.swansea.uni.m06.id803623;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class MyAlarmService extends Service
{

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent myIntent = new Intent(this.getApplicationContext(),NoteDetail.class);
        Long mRowId = intent.getExtras().getLong(NotesDbAdapter.KEY_ROW_ID);
        int reqCode = mRowId.intValue();

        myIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);
        myIntent.putExtra("alarm", true);
        Log.d("MyAlarmService", "id" + mRowId);


        Notification notification = new Notification(R.drawable.ic_launcher,"Note Reminder", System.currentTimeMillis());
        myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),reqCode, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "Note Reminder", "A note requires you attention!", pendingNotificationIntent);

        mManager.notify(reqCode, notification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}