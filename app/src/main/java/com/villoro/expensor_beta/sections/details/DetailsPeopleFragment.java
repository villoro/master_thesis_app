package com.villoro.expensor_beta.sections.details;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 13/05/2015.
 */
public class DetailsPeopleFragment extends Fragment implements DialogLongClickList.CommGetChoice{

    ListView listView;
    Context context;
    long listID, currentID;

    public DetailsPeopleFragment(){};

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
        setListView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_people:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_people, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv_transactions_personal);

        TextView t_name = (TextView) rootView.findViewById(R.id.tv_name);

        Cursor tempCursor = context.getContentResolver().query(ExpensorContract.PeopleEntry.PEOPLE_URI, null,
                Tables.ID + " '" + currentID + "'", null, null);
        if(tempCursor.moveToFirst()){
            t_name.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.NAME)));
        }

        setListView();

        return rootView;
    }

    public void setListView(){
        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.TransactionPeopleEntry.buildFromPeopleId(currentID), null, null, null, null);
        Log.d("DetailsPeopleFragment", "cursor count= " + cursor.getCount());

    }

    public void initialize(long whichID){
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
    public void getChoice(int choice) {
        if (choice == DialogLongClickList.CASE_EDIT)
        {
            Intent intent = new Intent(context, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, listID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_TRANSACTION_PERSONAL);

            startActivity(intent);
        }
        if (choice == DialogLongClickList.CASE_DELETE)
        {
            context.getContentResolver().delete(ExpensorContract.TransactionPeopleEntry.TRANSACTION_PEOPLE_URI,
                    Tables.ID + " = '" + listID + "'", null);
        }
    }
}
