package com.villoro.expensor_beta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Arnau on 28/02/2015.
 */
public class DialogOkCancel extends DialogFragment{

    public static int CASE_FROM_LONG_CLICK = 1;
    public static int CASE_DIRECT = 2;

    CommOkCancel communicator;
    int whichCase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete it?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(false, whichCase);
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(true, whichCase);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    public void setCommunicator(CommOkCancel communicator, int whichCase){
        this.communicator = communicator;
        this.whichCase = whichCase;
    }

    public interface CommOkCancel
    {
        public void ifOkDo(boolean ok, int whichCase);
    }

}
