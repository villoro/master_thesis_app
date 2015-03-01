package com.villoro.expensor_beta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DialogLongClickList extends DialogFragment {

    public static int CASE_EDIT = 0;
    public static int CASE_DELETE = 1;

    CommGetChoise commGetChoise;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Options");
        String[] options = {"Edit", "Delete"};
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                commGetChoise.getChoise(which);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    public void setCommunicator(CommGetChoise commGetChoise){
        this.commGetChoise = commGetChoise;
    }

    public interface CommGetChoise
    {
        public void getChoise(int choise);
    }

}