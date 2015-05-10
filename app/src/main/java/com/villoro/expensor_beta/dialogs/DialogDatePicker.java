package com.villoro.expensor_beta.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.villoro.expensor_beta.Utility;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DialogDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    CommDatePicker comm;
    int[] date; //{yyyy, mm, dd, hh, mm, ss}

    public DialogDatePicker() {
        // TODO Auto-generated constructor stub

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, date[0], date[1] - 1, date[2]); //year, month, day
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        comm.setDate(year, monthOfYear + 1, dayOfMonth);
    }

    public void setCommunicator(CommDatePicker comm)
    {
        this.comm = comm;

    }

    public void setPreviousDate(int[] date)
    {
        this.date = date;
        Log.d("", "year= " + date[2] + ", month= " + date[1] + ", day= " + date[0]);
    }

    public interface CommDatePicker
    {
        public void setDate(int year, int month, int day);
    }

}
