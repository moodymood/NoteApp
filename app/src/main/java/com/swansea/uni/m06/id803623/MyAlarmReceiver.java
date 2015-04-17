package com.swansea.uni.m06.id803623;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent mServiceIntent = new Intent(context, MyAlarmService.class);
        Long mRowId = intent.getExtras().getLong(NotesDbAdapter.KEY_ROW_ID);
        mServiceIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);
        context.startService(mServiceIntent);

    }
}
