package com.villoro.expensor_beta.sections;

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
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;

/**
 * Created by Arnau on 28/02/2015.
 */

public class HistoryFragmentSection extends Fragment implements DialogLongClickList.CommGetChoice, DialogOkCancel.CommOkCancel{

    ListView listView;
    Context context;
    long listID;

    public HistoryFragmentSection(){};

    public static HistoryFragmentSection newHistoryFragment(int sectionNumber){
        HistoryFragmentSection fragment = new HistoryFragmentSection();
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
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_EXPENSE);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (ListView) rootView.findViewById(R.id.m_listview);

        setListView();
        return rootView;
    }

    public void setListView(){
        Cursor cursor = getActivity().getContentResolver().query(
                ExpensorContract.ExpenseEntry.CONTENT_URI, null, null, null, null);
        String[] aux = new String[cursor.getCount()];

        int i = 0;

        if (cursor.moveToFirst()){
            do{
                StringBuilder sb = new StringBuilder();
                sb.append("_id= " + cursor.getLong(cursor.getColumnIndex(Tables.ID)) + ", ");

                sb.append("date= " + cursor.getString(cursor.getColumnIndex(Tables.DATE)) + ", ");
                sb.append("categoryID= " + cursor.getInt(cursor.getColumnIndex(Tables.CATEGORY_ID)) + ", ");
                sb.append("amount= " + cursor.getInt(cursor.getColumnIndex(Tables.AMOUNT)) + ", ");
                sb.append("comments= " + cursor.getString(cursor.getColumnIndex(Tables.COMMENTS)) + ", ");

                sb.append("parseID= " + cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) + ", ");
                sb.append("updatedAt= " + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE)) + ", ");
                sb.append("deleted= " + cursor.getInt(cursor.getColumnIndex(Tables.DELETED)));

                aux[i] = sb.toString();
                i++;
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, aux);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO when using cursorAdapter delete the "1"
                showLongClickList(id + 1);
                return true;
            }
        });
    }

    public void showLongClickList(long id){
        DialogLongClickList dialog = new DialogLongClickList();
        dialog.setCommunicator(this);
        dialog.show(getFragmentManager(), null);
        listID = id;
    }

    @Override
    public void getChoice(int choice) {
        if (choice == DialogLongClickList.CASE_EDIT)
        {
            Intent intent = new Intent(context, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, listID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_EXPENSE);

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
            Log.d("TransactionSimpleFragment", "trying to delete id= " + listID);
            context.getContentResolver().delete(ExpensorContract.ExpenseEntry.CONTENT_URI, Tables.ID + " = '" + listID + "'", null);
            setListView();
        }
    }
}
