package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.PeopleAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 13/05/2015.
 */
public class AddOrUpdateTransactionPersonalFragment extends Fragment implements AddOrUpdateInterface {

    private final static int AUTO_COMPLETE_FROM = 1;
    private final static int AUTO_COMPLETE_TO = 2;

    Context context;
    long currentID;

    EditText e_amount, e_comments;
    AutoCompleteTextView ac_from, ac_to;
    long fromId, toId, myId;
    double amount;

    public AddOrUpdateTransactionPersonalFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        myId = UtilitiesNumbers.getMyId(context);
        Log.d("", "my id= " + myId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_transaction_personal, container, false);

        ac_from = (AutoCompleteTextView) rv.findViewById(R.id.et_from);
        ac_to = (AutoCompleteTextView) rv.findViewById(R.id.et_to);
        e_amount = (EditText) rv.findViewById(R.id.et_amount);
        e_comments = (EditText) rv.findViewById(R.id.et_comments);

        fromId = 0; toId = 0;

        setAutoCompletes();
        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    public void setAutoCompletes(){
        ac_from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromCursor(s.toString(), AUTO_COMPLETE_FROM);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ac_to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromCursor(s.toString(), AUTO_COMPLETE_TO);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ac_from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout relativeLayout = (RelativeLayout) view;
                TextView tvName = (TextView) relativeLayout.findViewById(R.id.row_name);
                ac_from.setText(tvName.getText().toString());
                ac_to.setText("Me");

                fromId = id;
                toId = myId;
            }
        });
        ac_to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout relativeLayout = (RelativeLayout) view;
                TextView tvName = (TextView) relativeLayout.findViewById(R.id.row_name);
                ac_from.setText("Me");
                ac_to.setText(tvName.getText().toString());

                fromId = myId;
                toId = id;
            }
        });
    }

    public void setFromCursor(String partName, int whichAutoComplete){
        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleEntry.buildFromPartOfNameUri(partName), null, null, null, null);
        PeopleAdapter peopleAdapter = new PeopleAdapter(context, cursor, 0);

        if(whichAutoComplete == AUTO_COMPLETE_FROM) {
            ac_from.setAdapter(peopleAdapter);
        } else {
            ac_to.setAdapter(peopleAdapter);
        }
    }

    @Override
    public void add() {
        Log.d("AddTransactionPeople", "amount= " +e_amount.getText().toString().trim());
        amount = Double.parseDouble(UtilitiesDates.formatDoubleToSQLite(e_amount.getText().toString().trim()));

        Log.d("AddTransactionPeople", "from= " + fromId + ", to= " + toId + ", amount= " + amount);

        //TODO check if values are possible
        ContentValues values = new ContentValues();
        if(fromId == myId){
            values.put(Tables.PEOPLE_ID, toId);
        } else {
            amount = -amount;
            values.put(Tables.PEOPLE_ID, fromId);
        }

        values.put(Tables.COMMENTS, e_comments.getText().toString().trim());
        values.put(Tables.AMOUNT, amount);
        if (currentID > 0){
            context.getContentResolver().update(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI,
                    values, Tables.ID + " = '" + currentID + "'", null);
        } else {
            context.getContentResolver().insert(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI, values);
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

    }

    @Override
    public void delete() {
        context.getContentResolver().delete(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI,
                Tables.ID + " = '" + currentID + "'", null);
    }
}
