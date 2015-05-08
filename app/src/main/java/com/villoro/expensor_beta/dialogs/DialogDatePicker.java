package com.villoro.expensor_beta.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.villoro.expensor_beta.Utility;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DialogDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    CommDatePicker comm;
    String oldDate;
    int[] date; //{yyyy, mm, dd, hh, mm, ss}

    public DialogDatePicker() {
        // TODO Auto-generated constructor stub

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        date = Utility.onlyDateFromString(oldDate);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, date[2], date[1] - 1, date[0]);
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

    public void setPreviusDate(String date)
    {
        this.oldDate = date;
    }

    public interface CommDatePicker
    {
        public void setDate(int year, int month, int day);
    }

}
