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

package com.android.demo.notepad3;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.util.Calendar;

public class NoteDetail extends  FragmentActivity {

    private EditText mTitleText;
    private EditText mContentText;
    private RadioGroup mPriorityRadioGroup;
    private EditText mAlarmText;

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
        mAlarmText = (EditText) findViewById(R.id.dateEditText);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROW_ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROW_ID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
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

            mAlarmText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ALARM)));
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
        String alarm = mAlarmText.getText().toString();

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




}
