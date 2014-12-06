package com.elmeripoikolainen.habifier;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by elmeripoikolainen on 09/11/14.
 */
//public class DatePickerFragment extends DialogFragment
//        //implements DatePickerDialog.OnDateSetListener
//{
//
//    DatePickerDialog.OnDateSetListener onDateSet;
//
//
//
//    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
//        onDateSet = ondate;
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new instance of DatePickerDialog and return it
//        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSet, year, month, day);
//        dialog.getDatePicker().setMaxDate((new Date()).getTime()); //This might create bugs
//        //dialog.getDatePicker().setMinDate(graphMinDate.getTime());
//
//        return dialog;
//    }
//
////    public void onDateSet(DatePicker view, int year, int month, int day) {
////        Calendar calendar = Calendar.getInstance();
////        calendar.clear();
////        calendar.set(Calendar.MONTH, month);
////        calendar.set(Calendar.YEAR, year);
////        calendar.set(Calendar.DAY_OF_MONTH, day);
////        chosenDate = calendar.getTime();
////        // Do something with the date chosen by the user
////
////    }
//}