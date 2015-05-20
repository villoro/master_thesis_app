package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.PeopleAdapter;
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
    TextView header_pig;

    Uri returnGroupUri;

    AutoCompleteTextView autoComplete;

    public AddOrUpdateGroupFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        names = new ArrayList<>();
        ids = new ArrayList<>();

        names.add(getString(R.string.me));
        ids.add(UtilitiesNumbers.getMyId(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_groups, container, false);

        e_name = (EditText) rv.findViewById(R.id.et_groups_name);
        listView = (ListView) rv.findViewById(R.id.lv);
        autoComplete = (AutoCompleteTextView) rv.findViewById(R.id.ac_name);

        header_pig = (TextView) rv.findViewById(R.id.header_people_in_group);

        setList();
        if (currentID >0)
        {
            setValues();
        }

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("AddOrUpdateGroup", "text= " + s.toString());
                setFromCursor(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout linearLayout = (RelativeLayout) view;
                TextView tvName = (TextView) linearLayout.findViewById(R.id.row_name);
                String name = tvName.getText().toString().trim();
                if (!ids.contains(id)) {
                    Log.d("AddOrUpdateGroup", "selected name= " + name);

                    names.add(name);
                    ids.add(id);
                    setList();
                } else {
                    Toast.makeText(context, "Already in the group", Toast.LENGTH_LONG);
                }
                autoComplete.setText("");
            }
        });


        return rv;
    }

    public void setFromCursor(String partName){
        Cursor cursorPeople = context.getContentResolver().query(
                ExpensorContract.PeopleEntry.buildFromPartOfNameUri(partName), null, null, null, null);
        PeopleAdapter peopleAdapter = new PeopleAdapter(context, cursorPeople, 0);

        Log.d("PeopleInGroupAdapter", "cursor count= " + cursorPeople.getCount());

        autoComplete.setAdapter(peopleAdapter);
    }

    @Override
    public boolean add() {
        name = e_name.getText().toString().trim();
        if (name.length() > 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        }

        if(valuesAreCorrect()) {
            ContentValues values = new ContentValues();
            values.put(Tables.NAME, name);
            if (currentID > 0) {
                context.getContentResolver().update(ExpensorContract.GroupEntry.CONTENT_URI, values, Tables.ID + " = '" + currentID + "'", null);
            } else {
                Uri uri = context.getContentResolver().insert(ExpensorContract.GroupEntry.CONTENT_URI, values);
                currentID = UtilitiesNumbers.getIdFromUri(uri);
            }
            for (long thisId : ids) {
                ContentValues pigValues = new ContentValues();
                pigValues.put(Tables.GROUP_ID, currentID);
                pigValues.put(Tables.PEOPLE_ID, thisId);
                context.getContentResolver().insert(ExpensorContract.PeopleInGroupEntry.PEOPLE_IN_GROUP, pigValues);
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

    @Override
    public boolean valuesAreCorrect() {
        boolean output = true;
        if(name.length() == 0){
            e_name.setError(getString(R.string.error_name));
            output = false;
        }
        if(ids.size() < 2){
            header_pig.setError(getString(R.string.error_people_in_group));
            output = false;
        }
        return output;
    }
}
