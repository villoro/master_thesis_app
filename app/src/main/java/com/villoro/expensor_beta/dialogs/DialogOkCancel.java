package com.villoro.expensor_beta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.villoro.expensor_beta.R;

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

        builder.setTitle(getString(R.string.dialog_delete));
        builder.setMessage(getString(R.string.dialog_are_you_sure_delete));
        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(false, whichCase);
            }
        });
        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
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
