package com.swansea.uni.m06.id803623;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.util.Log;


public class MyAlarmService extends Service
{



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

        String NOTIFICATION_TITLE = getResources().getString(R.string.noteReminderMSG);
        String NOTIFICATION_MSG = getResources().getString(R.string.noteReminderMessageMSG);

        Long mRowId = intent.getExtras().getLong(NotesDbAdapter.KEY_ROW_ID);
        int reqCode = mRowId.intValue();

        NotificationManager mManager = (NotificationManager)
                this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this.getApplicationContext(), NoteDetail.class);
        mIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);


        Notification mNotification = new Notification(R.drawable.ic_launcher, NOTIFICATION_TITLE,
                System.currentTimeMillis());
        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mPendingNotificationIntent = PendingIntent.getActivity(
                this.getApplicationContext(), reqCode, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.setLatestEventInfo(this.getApplicationContext(), NOTIFICATION_TITLE,
                NOTIFICATION_MSG, mPendingNotificationIntent);

        mManager.notify(reqCode, mNotification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}