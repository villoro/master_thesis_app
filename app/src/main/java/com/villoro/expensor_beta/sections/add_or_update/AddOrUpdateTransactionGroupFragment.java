package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.villoro.expensor_beta.PLEM.PLEM_Utilities;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.GroupTransactionPaidAdapter;
import com.villoro.expensor_beta.adapters.GroupTransactionSpentAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogDatePicker;

/**
 * Created by Arnau on 17/05/2015.
 */
public class AddOrUpdateTransactionGroupFragment extends Fragment implements AddOrUpdateInterface,
        GroupTransactionPaidAdapter.CommPaid, GroupTransactionSpentAdapter.CommSpent, DialogDatePicker.CommDatePicker {

    public final static int NO_PERSON = -1;

    boolean isDividing;

    Context context;
    long currentID, groupID;
    ListView lv_spent, lv_paid;

    double[] paid, spentLocked, spent;
    boolean[] locked;
    double totalPaid, totalSpent;

    EditText e_comments;
    TextView header_paid, header_spent;

    Button b_date;
    int[] date;
    DialogDatePicker dialogDate;

    GroupTransactionPaidAdapter groupTransactionPaidAdapter;
    GroupTransactionSpentAdapter groupTransactionSpentAdapter;

    public AddOrUpdateTransactionGroupFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        Bundle bundle = this.getArguments();
        groupID = bundle.getLong(Tables.GROUP_ID);
        isDividing = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_transaction_group, container, false);

        bindButtonDate(rv);

        e_comments = (EditText) rv.findViewById(R.id.et_comments);

        lv_paid = (ListView) rv.findViewById(R.id.lv_paid);
        lv_spent = (ListView) rv.findViewById(R.id.lv_spent);

        header_paid = (TextView) rv.findViewById(R.id.header_paid);
        header_spent = (TextView) rv.findViewById(R.id.header_spent);

        Cursor cursorPeople = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildUriFromGroupId(groupID), null, null, null, null);

        int length = cursorPeople.getCount();

        groupTransactionPaidAdapter = new GroupTransactionPaidAdapter(context, cursorPeople, 0);
        groupTransactionSpentAdapter = new GroupTransactionSpentAdapter(context, cursorPeople, 0);

        groupTransactionPaidAdapter.setCommPaid(this);
        groupTransactionSpentAdapter.setCommSpent(this);

        lv_paid.setAdapter(groupTransactionPaidAdapter);
        lv_spent.setAdapter(groupTransactionSpentAdapter);

        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_paid);
        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_spent);

        paid = new double[length];
        spentLocked = new double[length];
        spent = new double[length];
        locked = new boolean[length];
        totalPaid = 0;
        totalSpent = 0;

        if (currentID >0)
        {
            setValues();
        }

        return rv;
    }

    @Override
    public boolean add() {
        if(valuesAreCorrect()) {
            String comments = e_comments.getText().toString().trim();

            ContentValues transactionValues = new ContentValues();

            transactionValues.put(Tables.DATE, UtilitiesDates.completeDateToString(date));
            transactionValues.put(Tables.COMMENTS, comments);
            transactionValues.put(Tables.AMOUNT, totalPaid);
            transactionValues.put(Tables.GROUP_ID, groupID);
            transactionValues.put(Tables.TYPE, Tables.TYPE_TRANSACTION); //TODO allow gives

            if (currentID > 0) {
                context.getContentResolver().update(ExpensorContract.TransactionGroupEntry.CONTENT_URI
                        , transactionValues, Tables.ID + " = '" + currentID + "'", null);
            } else {
                Uri uri = context.getContentResolver().insert(ExpensorContract.TransactionGroupEntry.CONTENT_URI, transactionValues);
                currentID = UtilitiesNumbers.getIdFromUri(uri);
            }

            long[] ids = groupTransactionPaidAdapter.ids;

            for (int i = 0; i < paid.length; i++) {
                ContentValues whoValues = new ContentValues();

                whoValues.put(Tables.TRANSACTION_ID, currentID);
                whoValues.put(Tables.PEOPLE_ID, ids[i]);
                whoValues.put(Tables.PAID, paid[i]);
                whoValues.put(Tables.SPENT, spent[i]);

                context.getContentResolver().insert(ExpensorContract.WhoPaidSpentEntry.CONTENT_URI, whoValues);
            }

            PLEM_Utilities.saveLastAdded(context);

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
                dialogDate.setPreviousDate(date);
                dialogDate.show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void changePaid(int position, double amount) {
        totalPaid -= paid[position];
        paid[position] = amount;
        totalPaid += amount;

        divideSpent(NO_PERSON);
    }

    @Override
    public boolean lockPerson(int position, double amount) {
        if(!isDividing) {
            totalSpent -= spentLocked[position];
            spentLocked[position] = amount;
            locked[position] = true;
            totalSpent += amount;
            Log.d("", "person " + position + " locked");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void unlockPerson(int position) {
        clearPerson(position);
    }

    public void clearPerson(int position){
        totalSpent -= spentLocked[position];
        spentLocked[position] = 0;
        locked[position] = false;
        Log.d("", "unlocked " + position);
    }

    @Override
    public void divideSpent() {
        divideSpent(NO_PERSON);
    }

    //input position of the people focused, needs to be done the last, -1 if none
    public void divideSpent(int position) {
        isDividing = true;
        if(totalPaid - totalSpent > UtilitiesNumbers.EPSILON) {
            header_paid.setError(null);
            header_spent.setError(null);
            int totalWeight = 0;
            int[] weights = new int[lv_spent.getChildCount()];
            for (int i = 0; i < lv_spent.getChildCount(); i++) {
                if (!locked[i]) {
                    TextView tv_weight = (TextView) lv_spent.getChildAt(i).findViewById(R.id.tv_number);
                    weights[i] = Integer.parseInt(tv_weight.getText().toString());
                    totalWeight += weights[i];
                }
            }
            if(totalWeight == 0)
                totalWeight = 1;
            Log.d("", "total weight= " + totalWeight);
            for (int i = 0; i < lv_spent.getChildCount(); i++) {
                if (!locked[i] && i != position) {
                    EditText et_amount = (EditText) lv_spent.getChildAt(i).findViewById(R.id.et_amount);
                    double toPay = (totalPaid - totalSpent) * weights[i] / totalWeight;
                    Log.d("", "toPay= " + toPay + " ,pos= " + i);
                    et_amount.setText(Double.toString(UtilitiesNumbers.round(toPay, 2)));
                    spent[i] = toPay; //this is what it would be saved
                }
            }

            if(position >= 0){
                EditText et_amount = (EditText) lv_spent.getChildAt(position).findViewById(R.id.et_amount);
                double toPay = (totalPaid - totalSpent) * weights[position] / totalWeight;
                Log.d("", "toPay= " + toPay + " ,pos= " + position);
                et_amount.setText(Double.toString(UtilitiesNumbers.round(toPay, 2)));
                spent[position] = toPay; //this is what it would be saved
            }
        } else {
            if (totalPaid > UtilitiesNumbers.EPSILON) {
                header_spent.setError(getString(R.string.error_spent));
            }
        }
        isDividing = false;
    }

    @Override
    public boolean isLocked(int position) {
        return locked[position];
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
        if(totalPaid < UtilitiesNumbers.EPSILON){
            header_paid.setError(getString(R.string.error_paid));
            output = false;
        }
        if(totalPaid - totalSpent < - UtilitiesNumbers.EPSILON){
            header_spent.setError(getString(R.string.error_spent));
            output = false;
        } else {
            if (totalSpent < UtilitiesNumbers.EPSILON) {
                header_spent.setError(getString(R.string.error_not_spent));
                output = false;
            }
        }
        return output;
    }
}
