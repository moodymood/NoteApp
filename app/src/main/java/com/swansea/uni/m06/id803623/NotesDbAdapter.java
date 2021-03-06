package com.swansea.uni.m06.id803623;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 */

public class NotesDbAdapter {

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    private static final String DATABASE_TABLE = "notes";

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_ALARM = "alarm";

    public static final int HIGH_PRIORITY = 0;
    public static final int MEDIUM_PRIORITY = 1;
    public static final int LOW_PRIORITY = 2;


    /** Database creation sql statement */
    private static final String DATABASE_NAME = "notes_db";
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + "(" +
                    KEY_ROW_ID + " integer primary key autoincrement, " +
                    KEY_TITLE + " text not null, " +
                    KEY_CONTENT + " text not null, " +
                    KEY_PRIORITY + " integer not null, " +
                    KEY_ALARM + " text)";



    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }


    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {

        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }


    public void close() {

        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param content the content of the note
     * @param priority priority of the note
     * @param alarm alarm of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String content, int priority, String alarm) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_CONTENT, content);
        initialValues.put(KEY_PRIORITY, priority);
        initialValues.put(KEY_ALARM, alarm);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
    }


    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROW_ID, KEY_TITLE,
                KEY_CONTENT, KEY_PRIORITY, KEY_ALARM}, null, null, null, null, KEY_PRIORITY + " ASC");
    }


    /**
     * Return a Cursor over a note identified by rowId
     *
     * @return the note
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROW_ID,
                    KEY_TITLE, KEY_CONTENT, KEY_PRIORITY, KEY_ALARM}, KEY_ROW_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }


    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param content value to set note body to
     * @param priority value to set note priority to
     * @param alarm value to set note alarm to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String content, int priority, String alarm) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_CONTENT, content);
        args.put(KEY_PRIORITY, priority);
        args.put(KEY_ALARM, alarm);

        return mDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
    }
}
