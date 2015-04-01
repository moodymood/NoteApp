package com.android.demo.notepad3;

import java.util.Date;

/**
 * Created by moody on 01/04/15.
 */
public class Note {

    public static final int HIGH_PRIORITY = 0;
    public static final int MEDIUM_PRIORITY = 1;
    public static final int LOW_PRIORITY = 2;

    public long rowId;
    public String title;
    public String content;
    public int priority;
    public String alarm;


    public Note()
    {

    }

    public Note(int rowId, String title, String content, int priority, String alarm )
    {
        this.rowId = rowId;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.alarm = alarm;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }


}
