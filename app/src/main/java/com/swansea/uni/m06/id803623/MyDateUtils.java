package com.swansea.uni.m06.id803623;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MyDateUtils is an utility class which manages simple operations on Dates
 */
public class MyDateUtils {

    private static SimpleDateFormat format = new SimpleDateFormat( "HH:mm dd/MMM/yy");

    /**
     * @param dateString is the String to be converted
     * @return a formatted Date given the passed String according to format
     */
    public static Date getDateFromString(CharSequence dateString) {

        try {
            Date mNewDate = format.parse(String.valueOf(dateString));
            return mNewDate;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param date is the Date to be converted
     * @return a formatted String for the passed Date according to format
     */
    public static String getStringFromDate(Date date) {
        return format.format(date);
    }








}
