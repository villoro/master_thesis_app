package com.villoro.expensor_beta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.villoro.expensor_beta.R;

/**
 * Created by Arnau on 20/05/2015.
 */
public class DialogAcceptSolution extends DialogFragment {

    CommAcceptSolution communicator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.dialog_settle));
        builder.setMessage(getString(R.string.dialog_are_you_sure_return));
        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(false);
            }
        });
        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                communicator.ifOkDo(true);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    public void setCommunicator(CommAcceptSolution communicator){
        this.communicator = communicator;
    }

    public interface CommAcceptSolution
    {
        public void ifOkDo(boolean ok);
    }

}
