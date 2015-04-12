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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;


public class NoteList extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private NotesDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        fillData();

      //  updateBackgroundColour();


        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_PRIORITY};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.itemText};

        // Now create a simple cursor adapter and set it to display
        MySimpleCursorAdapter notes =
            new MySimpleCursorAdapter(this, R.layout.notes_item, notesCursor, from, to);
        setListAdapter(notes);
    }

/*

    void updateBackgroundColour(){
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
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteDetail.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteDetail.class);
        i.putExtra(NotesDbAdapter.KEY_ROW_ID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }


    public class MySimpleCursorAdapter extends SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
            super(context, layout, cursor, from, to);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)  {
            super.bindView(view, context, cursor);
            int priority = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_PRIORITY));


            if(priority == Note.HIGH_PRIORITY) {
                view.setBackgroundColor(getResources().getColor(R.color.redDark));

            }

            else if(priority == Note.MEDIUM_PRIORITY)
            {
                view.setBackgroundColor(getResources().getColor(R.color.orangeDark));
            }

            else
            {
                view.setBackgroundColor(getResources().getColor(R.color.yellowDark));
            }

        }
    }


}
