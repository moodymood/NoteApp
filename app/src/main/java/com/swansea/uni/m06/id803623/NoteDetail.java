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

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.swansea.uni.m06.id803623.R;

public class NoteDetail extends  FragmentActivity {

    private EditText mTitleText;
    private EditText mContentText;
    private RadioGroup mPriorityRadioGroup;
    private Switch mAlarmSwitch;
    private TextView mAlarmText;
    private Button mConfirmButton;

    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_detail);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.titleEditText);
        mContentText = (EditText) findViewById(R.id.contentEditText);
        mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        mAlarmSwitch = (Switch) findViewById(R.id.alarmSwitch);
        mAlarmText = (TextView) findViewById(R.id.alarmTimeValue);

        mConfirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROW_ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROW_ID)
									: null;
		}

		populateFields();

        updateBackgroundColour();

        showOrHideAlarmTime();

        setListener();


    }

    public void setListener()
    {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });

        mPriorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                updateBackgroundColour();
            }
        });

        mAlarmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Setting new time", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        mAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showOrHideAlarmTime();
            }
        });
    }

    /*
    private void populateFields() {
        if (mRowId != null) {
            Note note = mDbHelper.fetchNote(mRowId);
            mTitleText.setText(note.getTitle());
            mContentText.setText(note.getContent());
            if(note.getPriority() == Note.HIGH_PRIORITY)
                mPriorityRadioGroup.check(R.id.highRadioButton);
            else if(note.getPriority() == Note.MEDIUM_PRIORITY)
                mPriorityRadioGroup.check(R.id.mediumRadioButton);
            else
                mPriorityRadioGroup.check(R.id.lowRadioButton);

            mAlarmText.setText(note.getAlarm());
        }
    }


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
            if(priority == Note.HIGH_PRIORITY)
                mPriorityRadioGroup.check(R.id.highRadioButton);
            else if(priority== Note.MEDIUM_PRIORITY)
                mPriorityRadioGroup.check(R.id.mediumRadioButton);
            else
                mPriorityRadioGroup.check(R.id.lowRadioButton);

            String currAlarm = note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ALARM));
            if(currAlarm!=null)
                mAlarmSwitch.setChecked(true);
            else
                mAlarmSwitch.setChecked(false);

        }
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

    private void saveState() {
        String title = mTitleText.getText().toString();
        String content = mContentText.getText().toString();
        //TODO get real values
        int priority = getPriorityFromRadioGroup();
        Log.d("Radio button", "saving priority" + priority);
        String alarm;
        if(mAlarmSwitch.isChecked())
            alarm = "";
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
            Log.d("NoteDetail", "setting RED");

        }
        else if(priority == Note.MEDIUM_PRIORITY)
        {
            backgroundColor = getResources().getColor(R.color.orangeDark);
            sectionColor = getResources().getColor(R.color.white);
            Log.d("NoteDetail", "setting ORANGE");

        }
        else
        {
            backgroundColor = getResources().getColor(R.color.yellowDark);
            sectionColor = getResources().getColor(R.color.white);
            Log.d("NoteDetail", "setting YELLOW");

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

    void showOrHideAlarmTime()
    {
        if(mAlarmSwitch.isChecked())
            findViewById(R.id.alarmTimeWrapper).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.alarmTimeWrapper).setVisibility(View.INVISIBLE);

    }

}
