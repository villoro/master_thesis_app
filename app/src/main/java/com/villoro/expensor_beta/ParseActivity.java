package com.villoro.expensor_beta;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.parse.ParseAdapter;
import com.villoro.expensor_beta.parse.ParseQueries;
import com.villoro.expensor_beta.parse.ParseSync;
import com.villoro.expensor_beta.sync.ExpensorSyncAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ParseActivity extends ActionBarActivity {

    private static String LAST_UPDATE_EXPENSOR = "last_update_expensor";
    private static long DEFAULT_DATE = 0;
    ListView listView;
    TextView lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_temp);

        listView = (ListView) findViewById(R.id.tempListView);
        lastUpdated = (TextView) findViewById(R.id.lastUpdated);

        //TODO uncomment
        //setList();
    }

    public void setList() {
        Cursor cursor = getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI, null,null, null, null);
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

        lastUpdated.setText(Utility.getStringFromDateUTC(readLastUpdateDate()) +
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

        Log.e("", "starting to do something");
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
/*
        for(String tableName : Tables.TABLES){
            Log.d("", "table= " + tableName);
            Log.d("", "query= " + ParseQueries.queryParse(tableName, time));
        } */

        Cursor cursor = ParseAdapter.getSmartQuery(this, Tables.TABLENAME_TRANSACTION_SIMPLE, time);
        Log.d("", "cursor size= " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                StringBuilder sb = new StringBuilder();
                sb.append(Tables.ID + "= ").append("" + cursor.getInt(cursor.getColumnIndex(Tables.ID))+ ", ");
                sb.append(Tables.DATE + "= ").append(cursor.getString(cursor.getColumnIndex(Tables.DATE))+ ", ");
                sb.append(Tables.CATEGORY_ID + "= ").append("" + cursor.getInt(cursor.getColumnIndex(Tables.CATEGORY_ID))+ ", ");
                sb.append(Tables.CATEGORY_ID + ParseQueries.PARSE + "= ").append(cursor.getString(cursor.getColumnIndex(Tables.CATEGORY_ID + ParseQueries.PARSE))+ ", ");
                sb.append(Tables.AMOUNT + "= ").append("" + cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT))+ ", ");
                sb.append(Tables.COMMENTS + "= ").append(cursor.getString(cursor.getColumnIndex(Tables.COMMENTS))+ ", ");
                sb.append(Tables.TYPE + "= ").append(cursor.getString(cursor.getColumnIndex(Tables.TYPE))+ ", ");
                sb.append(Tables.LAST_UPDATE + "= ").append("" + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE))+ ", ");

                Log.d("", "cursor 1= " + sb.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    public void insertSQL(View v) {

        InsertExampleValues insertExampleValues = new InsertExampleValues(this);
        insertExampleValues.insert();
    }

    public Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

}
