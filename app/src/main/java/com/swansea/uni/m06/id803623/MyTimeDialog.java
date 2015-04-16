package com.swansea.uni.m06.id803623;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Date;

/**
 * Created by moody on 15/04/15.
 */
public class MyTimeDialog extends android.app.DialogFragment {

    private TimePickerDialog mTimePicker;
    private DatePickerDialog mDatePicker;
    private Date result;

    final Date mCurrDate = new Date(System.currentTimeMillis());



    public void getTime()
    {

        mDatePicker = new DatePickerDialog(getParentFragment().getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Date mNewDate = new Date();
                mNewDate.setYear(year);
                mNewDate.setMonth(monthOfYear);
                mNewDate.setDate(dayOfMonth);

                result = mNewDate;

            }
        }, mCurrDate.getDay(), mCurrDate.getMonth(), mCurrDate.getYear());

        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                result = null;
            }
        });
        mDatePicker.setTitle("Select Alarm Date");


        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int newHour, int newMinute) {

                Date mNewDate = new Date();
                mNewDate.setHours(newHour);
                mNewDate.setMinutes(newMinute);

                result = mNewDate;

            }
        }, mCurrDate.getHours(), mCurrDate.getMinutes(), true);

        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (result != null) {
                    mDatePicker.show();
                }
                else
                {
                    result = null;
                }
            }
        });
        mTimePicker.setTitle("Select Alarm Time");


        mTimePicker.show();

    }
}
