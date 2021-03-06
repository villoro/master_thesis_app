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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.PeopleAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogDatePicker;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;

/**
 * Created by Arnau on 13/05/2015.
 */
public class AddOrUpdateTransactionPersonalFragment extends Fragment implements DialogDatePicker.CommDatePicker,
        AddOrUpdateInterface {

    private final static int AUTO_COMPLETE_FROM = 1;
    private final static int AUTO_COMPLETE_TO = 2;

    Context context;
    long currentID;

    EditText e_amount, e_comments;
    AutoCompleteTextView ac_from, ac_to;
    long fromId, toId, myId;
    double amount;
    String stringAmount;

    int[] date;
    Button b_date;
    DialogDatePicker dialogDate;

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

        bindButtonDate(rv);

        fromId = 0; toId = 0;

        setAutoCompletes();
        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    private void bindButtonDate(View rv)
    {
        date = UtilitiesDates.getDate();

        b_date = (Button) rv.findViewById(R.id.b_date);
        b_date.setText(UtilitiesDates.getFancyDate(date));

        dialogDate = new DialogDatePicker();
        dialogDate.setCommunicator(this);

        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogDate.setPreviousDate(date);
                dialogDate.show(getFragmentManager(), "datePicker");
            }
        });
    }

    public void setAutoCompletes(){
        ac_from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setFromCursor(s.toString().trim(), AUTO_COMPLETE_FROM);
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
                setFromCursor(s.toString().trim(), AUTO_COMPLETE_TO);
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
                ac_to.setText(context.getString(R.string.me));

                fromId = id;
                toId = myId;
            }
        });
        ac_to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout relativeLayout = (RelativeLayout) view;
                TextView tvName = (TextView) relativeLayout.findViewById(R.id.row_name);
                ac_from.setText(context.getString(R.string.me));
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
    public boolean add() {

        Log.d("AddTransactionPeople", "amount= " + e_amount.getText().toString().trim());
        stringAmount = e_amount.getText().toString().trim();
        if(stringAmount != null && stringAmount.length() > 0) {
            Log.e("addTransPers", "stingAmount= " + stringAmount + ", strAmount length = " + stringAmount.length());
            amount = Double.parseDouble(stringAmount);
        }

        Log.d("AddTransactionPeople", "from= " + fromId + ", to= " + toId + ", amount= " + amount);

        if(valuesAreCorrect()) {
            ContentValues values = new ContentValues();
            if (fromId == myId) {
                values.put(Tables.PEOPLE_ID, toId);
            } else {
                amount = -amount;
                values.put(Tables.PEOPLE_ID, fromId);
            }

            values.put(Tables.DATE, UtilitiesDates.completeDateToString(date));
            values.put(Tables.COMMENTS, e_comments.getText().toString().trim());
            values.put(Tables.AMOUNT, amount);
            if (currentID > 0) {
                context.getContentResolver().update(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI,
                        values, Tables.ID + " = '" + currentID + "'", null);
            } else {
                context.getContentResolver().insert(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI, values);
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

    }

    @Override
    public void delete() {
        context.getContentResolver().delete(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI,
                Tables.ID + " =?", new String[]{""+currentID});
    }

    @Override
    public void setDate(int year, int month, int day) {
        date[0] = year;
        date[1] = month;
        date[2] = day;

        b_date.setText(UtilitiesDates.getFancyDate(date));
    }

    @Override
    public boolean valuesAreCorrect() {
        boolean output = true;
        if(stringAmount == null || stringAmount.length() == 0) {
            e_amount.setError(getString(R.string.error_amount));
            output = false;
        } else {
            if (amount < UtilitiesNumbers.EPSILON) {
                e_amount.setError(getString(R.string.error_amount));
                output = false;
            }
        }
        if(fromId == 0){
            ac_from.setError(getString(R.string.error_people));
            output = false;
        }
        if(toId == 0){
            ac_to.setError(getString(R.string.error_people));
            output = false;
        }
        if(fromId != myId && toId != myId && toId + fromId > 0){
            ac_from.setError(getString(R.string.error_not_me));
            ac_to.setError(getString(R.string.error_not_me));
        }
        return output;
    }
}
