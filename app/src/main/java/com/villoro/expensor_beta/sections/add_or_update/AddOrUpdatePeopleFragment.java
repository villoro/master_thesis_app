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
public class AddOrUpdatePeopleFragment extends Fragment implements AddOrUpdateInterface{

    Context context;
    long currentID;

    EditText e_name, e_email;

    String name, email;

    public AddOrUpdatePeopleFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_people, container, false);

        e_name = (EditText) rv.findViewById(R.id.et_people_name);
        e_email = (EditText) rv.findViewById(R.id.et_people_email);

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    @Override
    public boolean add() {
        name = e_name.getText().toString().trim();
        email = e_email.getText().toString().trim();

        if(valuesAreCorrect()) {
            ContentValues values = new ContentValues();
            values.put(Tables.NAME, name);
            if(email != null && email.length() > 0) {
                values.put(Tables.EMAIL, email);
            }
            if (currentID > 0) {
                context.getContentResolver().update(ExpensorContract.PeopleEntry.PEOPLE_URI, values, Tables.ID + " = '" + currentID + "'", null);
            } else {
                context.getContentResolver().insert(ExpensorContract.PeopleEntry.PEOPLE_URI, values);
            }
            return true;
        } else {
            return false;
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
                ExpensorContract.PeopleEntry.PEOPLE_URI, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        e_name.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.NAME)));
        e_email.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.EMAIL)));
    }

    @Override
    public void delete() {
        context.getContentResolver().delete(ExpensorContract.PeopleEntry.PEOPLE_URI,
                Tables.ID + " = '" + currentID + "'", null);
    }

    @Override
    public boolean valuesAreCorrect() {
        boolean output = true;
        if(name.length() == 0){
            e_name.setError(getString(R.string.error_name));
            output = false;
        }
        if(email.length() == 0){
            e_email.setError(getString(R.string.error_email));
        }
        return output;
    }
}
