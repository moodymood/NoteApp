/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swansea.uni.m06.id803623;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class NoteDetail extends  FragmentActivity {

    private static final int DELETE_ID = Menu.FIRST;

    private EditText mTitleText;
    private EditText mContentText;
    private RadioGroup mPriorityRadioGroup;
    private TextView mAlarmTimeValue;
    private Switch mAlarmSwitch;



    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    private Intent mIntent;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();


        mTitleText = (EditText) findViewById(R.id.titleEditText);
        mContentText = (EditText) findViewById(R.id.contentEditText);
        mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        mAlarmSwitch = (Switch) findViewById(R.id.alarmSwitch);
        mAlarmTimeValue = (TextView) findViewById(R.id.alarmTimeValue);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROW_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROW_ID)
                    : null;

        }

        setTitle();

        populateFields();

        updateBackgroundColour();

        setListeners();

        setAlarmPopup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        return true;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                mDbHelper.deleteNote(mRowId);
                cancelAlarm();
                saveState();
                setResult(RESULT_OK);
                finish();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        saveState();
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROW_ID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }






    public void setTitle()
    {
        if(mRowId == null)
            setTitle(R.string.new_note);
        else
            setTitle(R.string.edit_note);
    }


    public void setListeners() {

        mPriorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                updateBackgroundColour();
            }
        });

        mAlarmSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mAlarmSwitch.isChecked()) {
                    Log.d("NoteDetail.java", "ENABLING alarm, mAlarmSwitch.isChecked() is true");

                    // if the user didn't press cancel
                    showAlarmTimeDialog();

                    // Deactivating alarm
                } else {
                    cancelAlarm();
                    mAlarmTimeValue.setText("");

                    Log.d("NoteDetail.java", "DISABLING alarm)");

                }
            }
        });
    }

    public void setAlarmPopup()
    {
        Log.d("NoteDetails.java", "***************Back from alarm*************");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getBoolean("alarm"))
                dismissAlarmPopup();
        }
    }









    public void setAlarm(Date alarm)
    {

            Log.d("NoteDetail.java", "inside setAlarm()");
            Log.d("NoteDetail.java", "mAlarmTimeValue is " + mAlarmTimeValue.getText());




            // Set an alarm only if the time is in the future
            if(alarm.getTime() > System.currentTimeMillis() && mRowId != null) {
                int reqCode = Integer.valueOf(mRowId.intValue());

                mIntent = new Intent(NoteDetail.this, MyAlarmReceiver.class);
                mIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);
                mPendingIntent = PendingIntent.getBroadcast(this, reqCode, mIntent, PendingIntent.FLAG_ONE_SHOT);


                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, alarm.getTime(), mPendingIntent);

                Toast.makeText(getApplicationContext(), "Alarm set at:\n" + alarm,
                        Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "The alarm cannot be set:\nit is now or\nin the past.",
                Toast.LENGTH_LONG).show();
            }

    }

    public void cancelAlarm()
    {
        if(mRowId != null) {
            int reqCode = Integer.valueOf(mRowId.intValue());

            mIntent = new Intent(NoteDetail.this, MyAlarmReceiver.class);
            mIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);

            mPendingIntent = PendingIntent.getBroadcast(NoteDetail.this, reqCode, mIntent, PendingIntent.FLAG_ONE_SHOT);

            mPendingIntent.cancel();

            Toast.makeText(getApplicationContext(), "Alarm correctly\n cancelled.",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void showAlarmTimeDialog()
    {

        final TimePickerDialog mTimePicker;
        final DatePickerDialog mDatePicker;

        final Date mCurrDate = Calendar.getInstance().getTime();
        Log.d("NoteDetail.java", "mCurrDate is  " + mCurrDate);

        mDatePicker = new DatePickerDialog(NoteDetail.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {

                Date mNewDate = MyDateUtils.getDateFromString(mAlarmTimeValue.getText().toString());

                Log.d("NoteDetail.java", "1. mNewDate is  " + mNewDate);


                mNewDate.setYear(newYear);
                mNewDate.setMonth(newMonth);
                mNewDate.setDate(newDay);

                Log.d("NoteDetail.java", "2. mNewDate is  " + mNewDate);

                setAlarmOnGUI(mNewDate);
                setAlarm(mNewDate);

            }
        }, mCurrDate.getDay(), mCurrDate.getMonth(), mCurrDate.getYear());

        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelAlarmOnGUI();
            }
        });
        mDatePicker.setTitle("Select Alarm Date");


        mTimePicker = new TimePickerDialog(NoteDetail.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int newHour, int newMinute) {

                Date mNewDate = new Date();

                Log.d("NoteDetail.java", "3. mNewDate is  " + mNewDate);

                mNewDate.setHours(newHour);
                mNewDate.setMinutes(newMinute);

                Log.d("NoteDetail.java", "4. mNewDate is  " + mNewDate);


                setAlarmOnGUI(mNewDate);
                Log.d("NoteDetail.java", "User selected data is " + mNewDate);

            }
        }, mCurrDate.getHours(), mCurrDate.getMinutes(), true);

        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (userHasSetTime()) {
                    Log.d("NoteDetail.java", "Calling mDatePicker.show()");
                    mDatePicker.show();
                }
                else
                {
                    Log.d("NoteDetail.java", "Calling cancelAlarmOnGUI()");
                    cancelAlarmOnGUI();
                }
            }
        });
        mTimePicker.setTitle("Select Alarm Time");


        mTimePicker.show();

    }


    public void setAlarmOnGUI(Date date)
    {
        Log.d("NoteDetail.java", "calling setAlarmOnGUI(Date date)" + date);

        mAlarmTimeValue.setText(date.toString());
        Log.d("NoteDetail.java", "mAlarmTimeValue after" + mAlarmTimeValue.getText().toString());

    }

    public void cancelAlarmOnGUI()
    {
        Log.d("NoteDetail.java", "calling cancelAlarmOnGUI()");

        mAlarmSwitch.setChecked(false);
        mAlarmTimeValue.setText("");
    }

    private boolean userHasSetTime()
    {
        if( mAlarmTimeValue.getText() != null || mAlarmTimeValue.getText() != "" )
            return true;
        else
            return false;
    }


     private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note); 

            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mContentText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CONTENT)));
            int priority = note.getInt(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_PRIORITY));
            if(priority == Note.HIGH_PRIORITY)
                mPriorityRadioGroup.check(R.id.highRadioButton);
            else if(priority== Note.MEDIUM_PRIORITY)
                mPriorityRadioGroup.check(R.id.mediumRadioButton);
            else
                mPriorityRadioGroup.check(R.id.lowRadioButton);


            String currAlarm = note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ALARM));
            if (currAlarm != null) {
                mAlarmSwitch.setChecked(true);
                mAlarmTimeValue.setVisibility(View.VISIBLE);
                mAlarmTimeValue.setText(currAlarm);
            }
            else {
                mAlarmSwitch.setChecked(false);
                mAlarmTimeValue.setVisibility(View.GONE);
            }

        }
    }



    private void saveState() {
        String title = mTitleText.getText().toString();
        String content = mContentText.getText().toString();
        int priority = getPriorityFromRadioGroup();

        String alarm;
        if(mAlarmSwitch.isChecked()) {
            alarm = mAlarmTimeValue.getText().toString();
        }
        else
            alarm = null;

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, content, priority, alarm);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, content, priority, alarm);
        }
    }

    int getPriorityFromRadioGroup(){
        int checkedRadioButtonId = mPriorityRadioGroup.getCheckedRadioButtonId();
        View radioButton = mPriorityRadioGroup.findViewById(checkedRadioButtonId);
        return mPriorityRadioGroup.indexOfChild(radioButton);
    }



    void changeColour(int priority){
        int backgroundColor, sectionColor;

        LinearLayout mainWrapper = (LinearLayout) findViewById(R.id.mainWrapper);
        LinearLayout titleAndContentWrapper = (LinearLayout) findViewById(R.id.titleAndContentWrapper);
        LinearLayout priorityWrapper = (LinearLayout) findViewById(R.id.priorityWrapper);
        LinearLayout alarmWrapper = (LinearLayout) findViewById(R.id.alarmWrapper);

        if(priority == Note.HIGH_PRIORITY) {
            backgroundColor = getResources().getColor(R.color.redDark);
            sectionColor = getResources().getColor(R.color.white);

        }
        else if(priority == Note.MEDIUM_PRIORITY)
        {
            backgroundColor = getResources().getColor(R.color.orangeDark);
            sectionColor = getResources().getColor(R.color.white);

        }
        else
        {
            backgroundColor = getResources().getColor(R.color.yellowDark);
            sectionColor = getResources().getColor(R.color.white);

        }

        mainWrapper.setBackgroundColor(backgroundColor);
        titleAndContentWrapper.setBackgroundColor(sectionColor);
        priorityWrapper.setBackgroundColor(sectionColor);
        alarmWrapper.setBackgroundColor(sectionColor);
    }

    void updateBackgroundColour(){
        int priority = getPriorityFromRadioGroup();
        changeColour(priority);
    }


    void dismissAlarmPopup()
    {


        AlertDialog.Builder mBuilder = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mBuilder = new AlertDialog.Builder(NoteDetail.this);
        } else {
            mBuilder = new AlertDialog.Builder(NoteDetail.this, AlertDialog.THEME_HOLO_LIGHT);
        }

        AlertDialog mAlertDialog = mBuilder.create();

        mAlertDialog.setMessage("Time is over,\ndismiss alarm.");
        mAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                cancelAlarm();
                cancelAlarmOnGUI();
            }
        });
        mAlertDialog.show();

    }




}
