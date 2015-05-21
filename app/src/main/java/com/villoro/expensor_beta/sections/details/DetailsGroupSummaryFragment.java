package com.villoro.expensor_beta.sections.details;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.villoro.expensor_beta.PLEM.AsyncTaskPLEM;
import com.villoro.expensor_beta.PLEM.PLEM_Utilities;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.BalancesInGroupAdapter;
import com.villoro.expensor_beta.adapters.HowToSettleAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogAcceptSolution;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DetailsGroupSummaryFragment extends Fragment implements DialogAcceptSolution.CommAcceptSolution {

    ListView lv_resolution, lv_balances;
    Context context;
    long groupID, listID;

    DetailsInterfaces.CommDetailsGroup commDetailsGroup;
    DetailsInterfaces.CommSetName commSetName;

    public DetailsGroupSummaryFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setList();
        solveIfNeeded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_transaction:
                Intent intent = new Intent(getActivity(), AddOrUpdateActivity.class);
                intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_TRANSACTION_GROUP);
                intent.putExtra(Tables.GROUP_ID, groupID);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_group_summary, container, false);
        lv_resolution = (ListView) rootView.findViewById(R.id.lv_resolution);
        lv_balances = (ListView) rootView.findViewById(R.id.lv_balances);

        Button b_history = (Button) rootView.findViewById(R.id.b_history);
        b_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commDetailsGroup.changeToSection(ShowDetailsActivity.SECTION_HISTORY);
            }
        });

        //TODO setTitle with group name

        return rootView;
    }

    public void setList() {
        Cursor cursorBalances = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildFromGroupIdWithBalancesFromCaseUri(groupID),
                null, null, null, null);
        BalancesInGroupAdapter balancesInGroupAdapter = new BalancesInGroupAdapter(context, cursorBalances, 0);
        lv_balances.setAdapter(balancesInGroupAdapter);

        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_balances);

        Cursor cursorSettle = context.getContentResolver().query(
                ExpensorContract.HowToSettleEntry.buildFromGroupId(groupID), null, null, null, null);
        HowToSettleAdapter howToSettleAdapter = new HowToSettleAdapter(context, cursorSettle, 0);
        lv_resolution.setAdapter(howToSettleAdapter);
        lv_resolution.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogAcceptSolution();
                listID = id;
            }
        });

        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_resolution);
    }

    public void showDialogAcceptSolution(){
        DialogAcceptSolution dialogAcceptSolution = new DialogAcceptSolution();
        dialogAcceptSolution.setCommunicator(this);
        dialogAcceptSolution.show(getFragmentManager(), null);
    }

    public void initialize(long id, DetailsInterfaces.CommDetailsGroup commDetailsGroup,
                           DetailsInterfaces.CommSetName commSetName){
        groupID = id;
        this.commDetailsGroup = commDetailsGroup;
        this.commSetName = commSetName;
    }

    public void solveIfNeeded(){
        if(PLEM_Utilities.needsToBeSolved(context)){
            AsyncTaskPLEM asyncTaskPLEM = new AsyncTaskPLEM(new FragmentCallback() {
                @Override
                public void onTaskDone() {
                    setList();
                }
            });
            asyncTaskPLEM.execute(context, groupID);
        }
    }

    public interface FragmentCallback {
        public void onTaskDone();
    }

    @Override
    public void ifOkDo(boolean ok) {
        Uri tempUri = ExpensorContract.HowToSettleEntry.HOW_TO_SETTLE_URI;
        Cursor c = context.getContentResolver().query(tempUri, null,
                Tables.ID + " = " + listID, null, null);
        if(c.moveToFirst()) {
            ContentValues valTransGroup = new ContentValues();

            double amount = c.getDouble(c.getColumnIndex(Tables.AMOUNT));
            long groupID = c.getLong(c.getColumnIndex(Tables.GROUP_ID));
            valTransGroup.put(Tables.AMOUNT, amount);
            valTransGroup.put(Tables.GROUP_ID, groupID);
            valTransGroup.put(Tables.DATE, UtilitiesDates.completeDateToString(UtilitiesDates.getDate()));
            valTransGroup.put(Tables.TYPE, Tables.TYPE_GIVE);

            Uri uri = context.getContentResolver().insert(ExpensorContract.TransactionGroupEntry.TRANSACTION_GROUP_URI, valTransGroup);
            long transID = UtilitiesNumbers.getIdFromUri(uri);

            ContentValues valPaid = new ContentValues();

            valPaid.put(Tables.TRANSACTION_ID, transID);
            valPaid.put(Tables.PEOPLE_ID, c.getLong(c.getColumnIndex(Tables.FROM)));
            valPaid.put(Tables.PAID, amount);

            ContentValues valSpent = new ContentValues();

            valSpent.put(Tables.TRANSACTION_ID, transID);
            valSpent.put(Tables.PEOPLE_ID, c.getLong(c.getColumnIndex(Tables.TO)));
            valSpent.put(Tables.SPENT, amount);

            context.getContentResolver().insert(ExpensorContract.WhoPaidSpentEntry.WHO_PAID_SPENT_URI, valPaid);
            context.getContentResolver().insert(ExpensorContract.WhoPaidSpentEntry.WHO_PAID_SPENT_URI, valSpent);

            PLEM_Utilities.saveLastAdded(context);
            solveIfNeeded();
            setList();
        }
    }
}
