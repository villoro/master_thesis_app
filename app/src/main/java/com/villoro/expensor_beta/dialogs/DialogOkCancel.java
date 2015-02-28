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

    CommOkCancel communicator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete it?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(false);
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(true);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    public void setCommunicator(CommOkCancel communicator){
        this.communicator = communicator;
    }

    public interface CommOkCancel
    {
        public void ifOkDo(boolean ok);
    }

}
