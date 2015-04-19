package com.swansea.uni.m06.id803623;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class NoteList extends ListActivity {

    protected static final int ACTIVITY_CREATE = 0;
    protected static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;

    private NotesDbAdapter mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_list);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        fillData();

        registerForContextMenu(getListView());
    }


    /** Create a context menu to add new notes. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }


    /**
     * Manages clicks on menu items: in this case it is only possible
     * to add new notes.
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    /** Manages click on list items, calling a new activity. */
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


    /**
     * Create a new intent which gets to a new activity to
     * create a new note.
     */
    private void createNote() {
        Intent i = new Intent(this, NoteDetail.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    /** Fill list's items. */
    private void fillData() {

        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);

        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_PRIORITY};
        int[] to = new int[]{R.id.itemText};

        MySimpleCursorAdapter notes =
                new MySimpleCursorAdapter(this, R.layout.notes_item, notesCursor, from, to);
        setListAdapter(notes);
    }


    /**
     * MySimpleCursorAdapter allows to manage background colours according
     * to the note's priority.
     */
    public class MySimpleCursorAdapter extends SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
            super(context, layout, cursor, from, to);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)  {
            super.bindView(view, context, cursor);

            TextView itemText = (TextView) view.findViewById(R.id.itemText);
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
            if(title.isEmpty())
                itemText.setHint(getResources().getString(R.string.emptyString));


            int priority = cursor.getInt(
                    cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_PRIORITY));

            if(priority == NotesDbAdapter.HIGH_PRIORITY)
                view.setBackgroundColor(getResources().getColor(R.color.redDark));
            else if(priority == NotesDbAdapter.MEDIUM_PRIORITY)
                view.setBackgroundColor(getResources().getColor(R.color.orangeDark));
            else
                view.setBackgroundColor(getResources().getColor(R.color.yellowDark));

        }
    }

}
