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
import android.widget.Button;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.adapters.TransactionGroupAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 20/05/2015.
 */
public class DetailsGroupHistoryFragment extends Fragment{

    ListView listView;
    Context context;
    long groupID;

    DetailsInterfaces.CommDetailsGroup commDetailsGroup;
    DetailsInterfaces.CommSetName commSetName;

    public DetailsGroupHistoryFragment(){};

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
                intent.putExtra(Tables.GROUP_ID, groupID);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_group_history, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv_transactions);

        Button b_summary = (Button) rootView.findViewById(R.id.b_summary);
        b_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commDetailsGroup.changeToSection(ShowDetailsActivity.SECTION_SUMMARY);
            }
        });

        //TODO setTitle with group name

        return rootView;
    }

    public void initialize(long id, DetailsInterfaces.CommDetailsGroup commDetailsGroup,
                           DetailsInterfaces.CommSetName commSetName){
        groupID = id;
        this.commDetailsGroup = commDetailsGroup;
        this.commSetName = commSetName;
    }

    public void setList() {
        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.TransactionGroupEntry.buildUriFromGroupId(groupID),
                null, null, null, null);
        TransactionGroupAdapter transactionGroupAdapter = new TransactionGroupAdapter(context, cursor, 0);
        listView.setAdapter(transactionGroupAdapter);
    }

}
