package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 02/03/2015.
 */
public class AddOrUpdatePeopleInGroupFragment extends Fragment implements AddOrUpdateInterface{

    Context context;
    long currentID;

    Spinner sp_people, sp_group;
    SimpleCursorAdapter adapter_people, adapter_groups;
    Cursor cursorPeople, cursorGroups;
    long peopleID, groupID;

    int[] to;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSpinner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_people_in_group, container, false);

        sp_people = (Spinner) rv.findViewById(R.id.sp_people);
        sp_group = (Spinner) rv.findViewById(R.id.sp_groups);
        setSpinner();

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    private void setSpinner(){
        to = new int[]{android.R.id.text1};
        cursorPeople = context.getContentResolver().query(
                ExpensorContract.PeopleEntry.PEOPLE_URI, null, null, null, null);
        adapter_people = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, cursorPeople, new String[]{Tables.NAME},
                to, 0);
        adapter_people.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_people.setAdapter(adapter_people);

        cursorGroups = context.getContentResolver().query(
                ExpensorContract.GroupEntry.CONTENT_URI, null, null, null, null);
        adapter_groups = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, cursorGroups, new String[]{Tables.NAME},
                to, 0);
        adapter_groups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_group.setAdapter(adapter_groups);
    }

    @Override
    public void add() {
        peopleID = sp_people.getSelectedItemId();
        groupID = sp_group.getSelectedItemId();

        ContentValues values = new ContentValues();
        values.put(Tables.PEOPLE_ID, peopleID);
        values.put(Tables.GROUP_ID, groupID);
        if (currentID > 0){
            context.getContentResolver().update(ExpensorContract.PeopleInGroupEntry.PEOPLE_IN_GROUP, values, Tables.ID + " = '" + currentID + "'", null);
        } else {
            context.getContentResolver().insert(ExpensorContract.PeopleInGroupEntry.PEOPLE_IN_GROUP, values);
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
                ExpensorContract.ExpenseEntry.EXPENSE_URI, null, Tables.ID + " = '" + currentID + "'", null, null); //TODO
        tempCursor.moveToFirst();

        int peopleID = tempCursor.getInt(tempCursor.getColumnIndex(Tables.PEOPLE_ID));
        sp_people.setSelection(peopleID);
        int groupID = tempCursor.getInt(tempCursor.getColumnIndex(Tables.GROUP_ID));
        sp_people.setSelection(groupID);
    }

    @Override
    public void delete() {
        context.getContentResolver().delete(ExpensorContract.PeopleInGroupEntry.PEOPLE_IN_GROUP, Tables.ID + " = '" + currentID + "'", null);
    }
}
