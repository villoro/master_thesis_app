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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DetailsGroupFragment extends Fragment implements DialogLongClickList.CommGetChoice {

    ListView listView;
    Context context;
    long listID, currentID;

    public DetailsGroupFragment(){};

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
        switch (id){
            case R.id.action_add_people:
                Intent intent = new Intent(getActivity(), AddOrUpdateActivity.class);
                intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_PEOPLE_IN_GROUP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_group, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv_TEMPORAL_people_in_group);

        return rootView;
    }

    public void setListView(){
        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.CONTENT_URI, null, Tables.GROUP_ID + " = '" + currentID + "'", null, null);

        String[] aux = new String[cursor.getCount()];

        Log.e("PeopleFragmentSection", "cursour count= " + cursor.getCount());

        int i = 0;

        if (cursor.moveToFirst()){
            do{
                StringBuilder sb = new StringBuilder();
                sb.append("_id= " + cursor.getLong(cursor.getColumnIndex(Tables.ID)) + ", ");

                sb.append("peopleID= " + cursor.getString(cursor.getColumnIndex(Tables.PEOPLE_ID)) + ", ");
                sb.append("groupID= " + cursor.getString(cursor.getColumnIndex(Tables.GROUP_ID)) + ", ");;

                sb.append("parseID= " + cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) + ", ");
                sb.append("updatedAt= " + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE)) + ", ");
                sb.append("deleted= " + cursor.getInt(cursor.getColumnIndex(Tables.DELETED)));

                aux[i] = sb.toString();
                i++;
            } while (cursor.moveToNext());
        }
        Log.d("PeopleFragmentSection", "aux length= " + aux.length + " values= " + aux.toString());

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
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_PEOPLE);

            startActivity(intent);
        }
        if (choice == DialogLongClickList.CASE_DELETE)
        {
            context.getContentResolver().delete(ExpensorContract.PeopleEntry.PEOPLE_URI,
                    Tables.ID + " = '" + listID + "'", null);
        }
    }
}
