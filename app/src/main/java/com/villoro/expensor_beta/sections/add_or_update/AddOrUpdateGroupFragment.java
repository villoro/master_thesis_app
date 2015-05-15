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
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.PeopleInGroupAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;

/**
 * Created by Arnau on 01/03/2015.
 */
public class AddOrUpdateGroupFragment extends Fragment implements PeopleInGroupAdapter.CommPeopleInGroup, AddOrUpdateInterface {

    Context context;
    long currentID;

    EditText e_name;

    String name;
    ArrayList<String> names;
    ArrayList<Long> ids;

    PeopleInGroupAdapter peopleInGroupAdapter;
    ListView listView;

    public AddOrUpdateGroupFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        names = new ArrayList<>();
        ids = new ArrayList<>();

        names.add("me");
        ids.add(UtilitiesNumbers.getMyId(context));
        names.add("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_groups, container, false);

        e_name = (EditText) rv.findViewById(R.id.et_groups_name);
        listView = (ListView) rv.findViewById(R.id.lv);
        setList();
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
        tempCursor.close();
    }

    @Override
    public void delete() {

    }

    public void setList(){
        peopleInGroupAdapter = new PeopleInGroupAdapter(context, names, ids);
        peopleInGroupAdapter.setCommunicator(this);
        listView.setAdapter(peopleInGroupAdapter);
    }

    @Override
    public void resetArrayLists(ArrayList<String> names, ArrayList<Long> ids) {
        this.names = names;
        this.ids = ids;
        setList();
    }
}
