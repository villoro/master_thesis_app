package com.villoro.expensor_beta.sections.add_or_update;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.GroupTransactionPaidAdapter;
import com.villoro.expensor_beta.adapters.GroupTransactionSpentAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.util.List;

/**
 * Created by Arnau on 17/05/2015.
 */
public class AddOrUpdateTransactionGroupFragment extends Fragment implements AddOrUpdateInterface,
        GroupTransactionPaidAdapter.CommPaid, GroupTransactionSpentAdapter.CommSpent {

    public final static int NO_PERSON = -1;

    Context context;
    long currentID, groupID;
    ListView lv_spent, lv_paid;

    double[] paid, spentLocked, spent;
    boolean[] locked;
    double totalPaid, totalSpent;

    public AddOrUpdateTransactionGroupFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        Bundle bundle = this.getArguments();
        groupID = bundle.getLong(Tables.GROUP_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_transaction_group, container, false);

        lv_paid = (ListView) rv.findViewById(R.id.lv_paid);
        lv_spent = (ListView) rv.findViewById(R.id.lv_spent);

        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildPeopleInGroupUri(groupID), null, null, null, null);

        int length = cursor.getCount();

        GroupTransactionPaidAdapter groupTransactionPaidAdapter = new GroupTransactionPaidAdapter(context, cursor, 0);
        GroupTransactionSpentAdapter groupTransactionSpentAdapter = new GroupTransactionSpentAdapter(context, cursor, 0);

        groupTransactionPaidAdapter.setCommPaid(this);
        groupTransactionSpentAdapter.setCommSpent(this);

        lv_paid.setAdapter(groupTransactionPaidAdapter);
        lv_spent.setAdapter(groupTransactionSpentAdapter);

        UtilitiesDates.setListViewHeightBasedOnChildren(lv_paid);
        UtilitiesDates.setListViewHeightBasedOnChildren(lv_spent);

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
    public void add() {

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

    @Override
    public void changePaid(int position, double amount) {
        totalPaid -= paid[position];
        paid[position] = amount;
        totalPaid += amount;

        divideSpent(NO_PERSON);
    }

    @Override
    public void lockPerson(int position, double amount) {
        totalSpent -= spentLocked[position];
        spentLocked[position] = amount;
        locked[position] = true;
        totalSpent += amount;
        Log.d("", "person " + position + " locked");
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
        if(totalPaid - totalSpent > 0) {
            int totalWeight = 0;
            int[] weights = new int[lv_spent.getChildCount()];
            for (int i = 0; i < lv_spent.getChildCount(); i++) {
                if (!locked[i]) {
                    TextView tv_weight = (TextView) lv_spent.getChildAt(i).findViewById(R.id.tv_number);
                    weights[i] = Integer.parseInt(tv_weight.getText().toString());
                    totalWeight += weights[i];
                }
            }
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
            Toast toast = Toast.makeText(context, "No es pot repartir, ja hi ha més diners gastats que pagats", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean isLocked(int position) {
        return false;
    }
}
