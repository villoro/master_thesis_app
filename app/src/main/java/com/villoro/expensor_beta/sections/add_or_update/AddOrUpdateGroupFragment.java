package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 01/03/2015.
 */
public class AddOrUpdateGroupFragment extends Fragment implements AddOrUpdateInterface {

    Context context;
    long currentID;

    EditText e_name;

    String name;

    public AddOrUpdateGroupFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_groups, container, false);

        e_name = (EditText) rv.findViewById(R.id.et_groups_name);

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    @Override
    public void add() {
        name = e_name.getText().toString().trim();

        //TODO check if values are possible
        ContentValues values = new ContentValues();
        values.put(Tables.NAME, name);
        if (currentID > 0){
            context.getContentResolver().update(ExpensorContract.GroupEntry.CONTENT_URI, values, Tables.ID + " = '" + currentID + "'", null);
        } else {
            context.getContentResolver().insert(ExpensorContract.GroupEntry.CONTENT_URI, values);
        }
    }

    @Override
    public void initialize(long whichID) {
        if (whichID > 0)
        {
            this.currentID = whichID;
        }
        else
        {
            this.currentID = 0;
        }
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                ExpensorContract.GroupEntry.CONTENT_URI, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        e_name.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.NAME)));
    }

    @Override
    public void delete() {

    }
}
