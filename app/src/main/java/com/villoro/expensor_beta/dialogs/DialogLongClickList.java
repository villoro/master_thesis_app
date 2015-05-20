package com.villoro.expensor_beta.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.villoro.expensor_beta.R;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DialogLongClickList extends DialogFragment {

    public static int CASE_EDIT = 0;
    public static int CASE_DELETE = 1;

    CommGetChoice commGetChoise;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.dialog_options));
        String[] options = {getString(R.string.dialog_edit), getString(R.string.dialog_delete)};
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                commGetChoise.getChoice(which);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    public void setCommunicator(CommGetChoice commGetChoice){
        this.commGetChoise = commGetChoice;
    }

    public interface CommGetChoice
    {
        public void getChoice(int choice);
    }

}