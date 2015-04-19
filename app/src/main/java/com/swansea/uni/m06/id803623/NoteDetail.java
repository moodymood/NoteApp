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
import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;


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

        populateFields();

        saveState();

        setListeners();

        setTitle();

        updateBackgroundColour();

        if(isNoteUrgent()){
            showAlarmSetter(true);
            if(alarmIsExpired())
                showDismissPopup();
        }

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
                if(userHasSetTime())
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
        saveState();
        setResult(RESULT_OK);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        updateBackgroundColour();
    }

    /**
     * Check if the alarm has expired
     *
     * @return  true if the activity is called as result of an
     * alarm timeout, false otherwise
     */
    public boolean alarmIsExpired()
    {
        if(userHasSetTime()) {
            Date mAlarm = MyDateUtils.getDateFromString(mAlarmTimeValue.getText());
            if (System.currentTimeMillis() >= mAlarm.getTime())
                return true;
            else
                return false;
        }
        else {
            return false;
        }
    }


    /**
     * Show a pop-up which informs that the alarm time is expired.
     */
    void showDismissPopup()
    {

        AlertDialog.Builder mBuilder = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mBuilder = new AlertDialog.Builder(NoteDetail.this);
        } else {
            mBuilder = new AlertDialog.Builder(NoteDetail.this, AlertDialog.THEME_HOLO_LIGHT);
        }

        AlertDialog mAlertDialog = mBuilder.create();

        mAlertDialog.setMessage(getResources().getString(R.string.dismissAlarmMSG));
        mAlertDialog.setButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                cancelAlarm();
                cancelAlarmOnGUI();
            }
        });
        mAlertDialog.show();

    }


    /**
     * Set the bar title, which changes according to the
     * calling intent.
     */
    public void setTitle()
    {
        if(mRowId == null)
            setTitle(R.string.new_note);
        else
            setTitle(R.string.edit_note);
    }


    /**
     * Set all activity listeners
     */
    public void setListeners() {

        mPriorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                updateBackgroundColour();
                showAlarmSetter(isNoteUrgent());
                if(userHasSetTime()) {
                    cancelAlarmOnGUI();
                    cancelAlarm();
                }
            }
        });

        mAlarmSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mAlarmSwitch.isChecked()) {
                    // if the user didn't press cancel
                    showAlarmTimeDialog();
                    // Deactivating alarm
                } else {
                    mAlarmTimeValue.setText(null);
                    cancelAlarm();

                }
            }
        });
    }


    /**
     * Show TimePickerDialog and a DatePickerDialog to
     * allow the user to choose a time for the alarm.
     */
    public void showAlarmTimeDialog()
    {

        final  DatePickerDialog mDatePicker;
        final TimePickerDialog mTimePicker;

        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date(System.currentTimeMillis()));




        mDatePicker = new DatePickerDialog(NoteDetail.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {

                mCalendar.setTime(MyDateUtils.getDateFromString(mAlarmTimeValue.getText()));
                mCalendar.set(newYear, newMonth, newDay);

                if(mCalendar.getTimeInMillis() <= System.currentTimeMillis()){
                    cancelAlarmOnGUI();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alarmInThePastMSG),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    setAlarmOnGUI(mCalendar.getTime());
                    setAlarm(mCalendar.getTime());
                }

            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelAlarmOnGUI();
            }
        });
        mDatePicker.setTitle(getResources().getString(R.string.alarm));


        mTimePicker = new TimePickerDialog(NoteDetail.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int newHour, int newMinute) {

                mCalendar.set(Calendar.HOUR_OF_DAY, newHour);
                mCalendar.set(Calendar.MINUTE, newMinute);

                setAlarmOnGUI(mCalendar.getTime());

            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                if (userHasSetTime())
                    mDatePicker.show();
                else
                    cancelAlarmOnGUI();

            }
        });
        mTimePicker.setTitle(getResources().getString(R.string.alarm));

        mTimePicker.show();

    }


    /**
     * Update the UI when the alarm is set.
     */
    public void setAlarmOnGUI(Date date)
    {
        mAlarmTimeValue.setText(MyDateUtils.getStringFromDate(date));
    }


    /**
     * Update the UI when the alarm is cancelled.
     */
    public void cancelAlarmOnGUI()
    {
        mAlarmSwitch.setChecked(false);
        mAlarmTimeValue.setText(null);
    }


    /**
     * Check ig the user has set the alarm or not
     *
     * @return true if the user set a valid alarm, false otherwise
     */
    private boolean userHasSetTime()
    {
        CharSequence charSequence = mAlarmTimeValue.getText();
        if(charSequence != null)
            if( !String.valueOf(charSequence).isEmpty()) {
                Log.d("NoteDetails", "charSequence: " + charSequence);
                return true;
            }
            else
                return false;
        else
            return false;
    }


    /**
     * Set a system alarm for the current note creating a service
     *
     * @param date the date the alarm will be set at
     */
    public void setAlarm(Date date)
    {
        if(mRowId != null) {
            int reqCode = Integer.valueOf(mRowId.intValue());

            mIntent = new Intent(NoteDetail.this, MyAlarmReceiver.class);
            mIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);
            mPendingIntent = PendingIntent.getBroadcast(this, reqCode, mIntent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, date.getTime(), mPendingIntent);

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alarmSetMSG) + MyDateUtils.getStringFromDate(date),
                    Toast.LENGTH_LONG).show();

        }
    }


    /**
     * Cancel an alarm for the current note
     */
    public void cancelAlarm()
    {
        if(mRowId != null) {
            int reqCode = Integer.valueOf(mRowId.intValue());

            mIntent = new Intent(NoteDetail.this, MyAlarmReceiver.class);
            mIntent.putExtra(NotesDbAdapter.KEY_ROW_ID, mRowId);

            mPendingIntent = PendingIntent.getBroadcast(NoteDetail.this, reqCode, mIntent, PendingIntent.FLAG_ONE_SHOT);

            mPendingIntent.cancel();

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alarmCancelledMSG),
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Invoke the DB to populate all fields of the note
     */
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
            if(priority == NotesDbAdapter.HIGH_PRIORITY)
                mPriorityRadioGroup.check(R.id.highRadioButton);
            else if(priority== NotesDbAdapter.MEDIUM_PRIORITY)
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


    /**
     * Save the note fields on DB
     */
    private void saveState() {
        String title = mTitleText.getText().toString().trim();
        String content = mContentText.getText().toString().trim();
        int priority = getPriorityFromRadioGroup();

        String alarm;
        if(mAlarmSwitch.isChecked())
            alarm = mAlarmTimeValue.getText().toString();
        else
            alarm = null;

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, content, priority, alarm);
            if (id > 0) {
                mRowId = id;
                populateFields();
            }
        } else {
            mDbHelper.updateNote(mRowId, title, content, priority, alarm);
        }
    }


    /**
     * Get the selected value from the priority checkbox group
     */
    int getPriorityFromRadioGroup(){
        int checkedRadioButtonId = mPriorityRadioGroup.getCheckedRadioButtonId();
        View radioButton = mPriorityRadioGroup.findViewById(checkedRadioButtonId);
        return mPriorityRadioGroup.indexOfChild(radioButton);
    }


    /**
     * Change the background colour of the note according to its priority
     *
     * @param  priority can be HIGH, MEDIUM, LOW
     */
    void changeColour(int priority){
        int backgroundColor, sectionColor;

        LinearLayout mainWrapper = (LinearLayout) findViewById(R.id.mainWrapper);
        LinearLayout titleAndContentWrapper = (LinearLayout) findViewById(R.id.titleAndContentWrapper);
        LinearLayout priorityWrapper = (LinearLayout) findViewById(R.id.priorityWrapper);
        LinearLayout alarmWrapper = (LinearLayout) findViewById(R.id.alarmWrapper);

        if(priority == NotesDbAdapter.HIGH_PRIORITY) {
            backgroundColor = getResources().getColor(R.color.redDark);
            sectionColor = getResources().getColor(R.color.white);

        }
        else if(priority == NotesDbAdapter.MEDIUM_PRIORITY)
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


    /**
     * Check if the current note is urgent
     *
     * @return true if it is urgent, false otherwise
     */
    public boolean isNoteUrgent()
    {
        if(getPriorityFromRadioGroup() == 0)
            return true;
        else
            return false;
    }


    /**
     * Update the GUI according to the note priority: if it is urgent the
     * alarm setter is shown, otherwisr is hidden
     *
     * @param isUrgent determine if the currentNote is urgent or not
     */
    public void showAlarmSetter(boolean isUrgent){
        if(isUrgent)
            findViewById(R.id.alarmWrapper).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.alarmWrapper).setVisibility(View.GONE);
    }
}
