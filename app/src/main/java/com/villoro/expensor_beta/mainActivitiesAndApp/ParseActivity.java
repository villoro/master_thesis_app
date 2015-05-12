package com.villoro.expensor_beta.mainActivitiesAndApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.sync.ExpensorSyncAdapter;

import java.util.Date;


public class ParseActivity extends ActionBarActivity {

    public static Activity parseActivity;
    private static String LAST_UPDATE_EXPENSOR = "last_update_expensor";
    private static long DEFAULT_DATE = 0;
    ListView listView;
    TextView lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_temp);

        parseActivity = this;
        LoginActivity.loginActivity.finish();

        listView = (ListView) findViewById(R.id.tempListView);
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);

        //TODO uncomment
        //setList();
    }

    public void setList() {
        Cursor cursor = getContentResolver().query(
                ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI, null,null, null, null);
        String[] aux = new String[cursor.getCount()];

        int i = 0;
        if (cursor.moveToFirst()){
            do{
                StringBuilder sb = new StringBuilder();
                sb.append("_id= " + cursor.getLong(cursor.getColumnIndex(Tables.ID)) + " ");
                sb.append("name= " + cursor.getString(cursor.getColumnIndex(Tables.NAME)) + " ");
                sb.append("color= " + cursor.getInt(cursor.getColumnIndex(Tables.COLOR)) + " ");
                if(cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) !=  null){
                    sb.append("parseID= " + cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) + " ");
                }
                sb.append("updatedAt= " + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE)));

                aux[i] = sb.toString();
                i++;
            } while (cursor.moveToNext());
        }

        for (String a : aux) {
            Log.d("ParseActivity", a);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aux);
        listView.setAdapter(arrayAdapter);

        lastUpdated.setText(UtilitiesDates.getStringFromDateUTC(readLastUpdateDate()) +
                " " + readLastUpdateDate().getTime());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_log_out) {
            ParseUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sync(View view){
        Log.e("", "calling ExpensorSyncAdapter.syncImmediately");
        ExpensorSyncAdapter.syncImmediately(this);
        setList();
    }

    public void updateSQL(View view){
        //updateCategory();
        //setList();
        /*ExpensorDbHelper mOpenHelper = new ExpensorDbHelper(this);
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        Cursor cursor =  database.query(Tables.TABLENAME_PEOPLE, null, null, null, null, null, null);
        Log.e("", "That user have " + cursor.getCount() + " publicPeople");
        if(cursor.moveToFirst()){
            do{
                Log.d("", "name= " + cursor.getString(cursor.getColumnIndex(Tables.NAME)) +
                        ", userID= " + cursor.getString(cursor.getColumnIndex(Tables.USER_ID)));
            } while (cursor.moveToNext());
        }

        ArrayList<String> peopleInGroup = ParseAdapter.getPeopleInGroup(this, "jmKAbEM0Sa");
        Log.d("", ParseQueries.queryPeopleInGroup("jmKAbEM0Sa"));
        Log.d("", "peopleInGroup jmKAbEM0Sa count= " + peopleInGroup.size());
        for(String person: peopleInGroup){
            Log.d("", "person= " + person);
        }

        Cursor cursor2 =  database.query(Tables.TABLENAME_PEOPLE_IN_GROUP, null, null, null, null, null, null);
        Log.e("", "That user have " + cursor2.getCount() + " publicPeople");*/

        /*List<ParseObject> parseObjects = new ArrayList<>();

        String personID = "IoowRxipMW";

        ParseObject group = ParseObject.createWithoutData("groups", "t5YpflZi8F");
        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setReadAccess(personID, true);
        parseACL.setWriteAccess(personID, true);

        group.setACL(parseACL);
        parseObjects.add(group);

        ParseObject peopleInGroup = ParseObject.createWithoutData("peopleInGroup", "fSgMRBonsw");

        ParseACL parseACL2 = new ParseACL(ParseUser.getCurrentUser());
        parseACL2.setReadAccess(personID, true);
        parseACL2.setWriteAccess(personID, true);

        peopleInGroup.setACL(parseACL2);
        parseObjects.add(peopleInGroup);

        ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("", "saved");
            }
        }); */

       //updateACL();
    }

    public void insertSQL(View v) {

        //Log.d("", ParseQueries.queryParse(Tables.TABLENAME_WHO_PAID_SPENT, 0));

        /*for(String tableName : Tables.TABLES) {
            Log.e("", tableName);
            Cursor cursor = ParseAdapter.getSmartCursor(this, tableName, 0);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.d("", cursor.getColumnName(i));
            }
        }*/

    }

    public Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

}
