package com.villoro.expensor_beta.sections.details;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.BalancesInGroupAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DetailsGroupSummaryFragment extends Fragment {

    ListView lv_resolution, lv_balances;
    Context context;
    long currentID;

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
                intent.putExtra(Tables.GROUP_ID, currentID);
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

        return rootView;
    }

    public void setList() {
        Cursor cursorBalances = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildFromGroupIdWithBalancesUri(currentID), null, null, null, null);
        BalancesInGroupAdapter balancesInGroupAdapter = new BalancesInGroupAdapter(context, cursorBalances, 0);
        lv_balances.setAdapter(balancesInGroupAdapter);

        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_balances);

    }

    public void initialize(long id){
        currentID = id;
    }

}
