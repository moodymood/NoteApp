package com.swansea.uni.m06.id803623;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by moody on 12/04/15.
 */
public class MyDateUtils {

    public static Date getDateFromString(String date) {





        SimpleDateFormat readFormat = new SimpleDateFormat( "EEE HH:mm, dd/MMM/yy");
        SimpleDateFormat writeFormat = new SimpleDateFormat( "HH:mm, dd/MMM/yy");


        try {
            return readFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromDate(Date date) {

        SimpleDateFormat readFormat = new SimpleDateFormat( "EEE HH:mm, dd/MMM/yy");

        return readFormat.format(date);

    }

    public static Date getCurrentTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm, dd/MMM/yy", Locale.ENGLISH);

        try {
            return formatter.parse(Calendar.getInstance().getTime().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }







}
