package com.villoro.expensor_beta.sections.mainSections;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.PeopleAdapter;
import com.villoro.expensor_beta.adapters.PeopleWithBalanceAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.ExpensorQueries;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;
import com.villoro.expensor_beta.sections.MainActivity;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;
import com.villoro.expensor_beta.sections.details.ShowDetailsActivity;

/**
 * Created by Arnau on 28/02/2015.
 */
public class PeopleFragmentSection extends Fragment implements DialogLongClickList.CommGetChoice, DialogOkCancel.CommOkCancel,
ListView.OnItemClickListener, ListView.OnItemLongClickListener{


    ListView lv_positive, lv_negative, lv_settled;
    Context context;
    long listID;

    public PeopleFragmentSection(){};

    public static PeopleFragmentSection newPeopleFragment(int sectionNumber){
        PeopleFragmentSection fragment = new PeopleFragmentSection();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //use to set the name of the section in the navigationDrawer inside MainActivity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }

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
        inflater.inflate(R.menu.menu_people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.action_add_people:
                intent = new Intent(getActivity(), AddOrUpdateActivity.class);
                intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_PEOPLE);
                startActivity(intent);
                return true;
            case R.id.action_add_transaction:
                intent = new Intent(getActivity(), AddOrUpdateActivity.class);
                intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_TRANSACTION_PERSONAL);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_section_people, container, false);
        lv_positive = (ListView) rootView.findViewById(R.id.lv_positive);
        lv_negative = (ListView) rootView.findViewById(R.id.lv_negative);
        lv_settled = (ListView) rootView.findViewById(R.id.lv_settled);

        setListView();

        return rootView;
    }

    public void setListView(){
        Cursor cursorPositive = context.getContentResolver().query(ExpensorContract.PeopleEntry.buildFromBalanceState(
                        ExpensorContract.PeopleEntry.CASE_BALANCE_POSITIVE), null, null, null, null);
        PeopleWithBalanceAdapter adapter_positive = new PeopleWithBalanceAdapter(context, cursorPositive, 0);
        lv_positive.setAdapter(adapter_positive);

        Cursor cursorNegative = context.getContentResolver().query(ExpensorContract.PeopleEntry.buildFromBalanceState(
                ExpensorContract.PeopleEntry.CASE_BALANCE_NEGATIVE), null, null, null, null);
        PeopleWithBalanceAdapter adapter_negative = new PeopleWithBalanceAdapter(context, cursorNegative, 0);
        lv_negative.setAdapter(adapter_negative);

        Cursor cursorSettled = context.getContentResolver().query(ExpensorContract.PeopleEntry.buildFromBalanceState(
                ExpensorContract.PeopleEntry.CASE_SETTLED), null, null, null, null);
        PeopleAdapter adapter_settled = new PeopleAdapter(context, cursorSettled, 0);
        lv_settled.setAdapter(adapter_settled);

        lv_positive.setOnItemClickListener(this);
        lv_negative.setOnItemClickListener(this);
        lv_settled.setOnItemClickListener(this);

        lv_positive.setOnItemLongClickListener(this);
        lv_negative.setOnItemLongClickListener(this);
        lv_settled.setOnItemLongClickListener(this);

        UtilitiesDates.setListViewHeightBasedOnChildren(lv_positive);
        UtilitiesDates.setListViewHeightBasedOnChildren(lv_negative);
        UtilitiesDates.setListViewHeightBasedOnChildren(lv_settled);
    }

    @Override
    public void getChoice(int choice) {
        if (choice == DialogLongClickList.CASE_EDIT)
        {
            Intent intent = new Intent(context, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, listID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_PEOPLE);

            startActivity(intent);
        }
        if (choice == DialogLongClickList.CASE_DELETE)
        {
            DialogOkCancel dialog = new DialogOkCancel();
            dialog.setCommunicator(this, dialog.CASE_FROM_LONG_CLICK);
            dialog.show(getFragmentManager(), null);

        }
    }

    @Override
    public void ifOkDo(boolean ok, int whichCase) {
        if(ok){
            Log.d("PeopleFragmentSection", "trying to delete id= " + listID);
            context.getContentResolver().delete(ExpensorContract.PeopleEntry.PEOPLE_URI, Tables.ID + " = '" + listID + "'", null);
            setListView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, ShowDetailsActivity.class);
        intent.putExtra(ShowDetailsActivity.ID_OBJECT, id);
        intent.putExtra(ShowDetailsActivity.WHICH_LIST, ShowDetailsActivity.CASE_PEOPLE);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DialogLongClickList dialog = new DialogLongClickList();
        dialog.setCommunicator(this);
        dialog.show(getFragmentManager(), null);
        listID = id;
        return true;
    }
}
