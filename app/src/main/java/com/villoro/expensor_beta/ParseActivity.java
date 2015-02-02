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
        setList();
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
        updateCategory();
        setList();
    }


    public void insertSQL(View v) {

        insertCategory();
        //insertExpense();
        setList();
    }

    public void insertCategory(){

        ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "O");
        testValues.put(Tables.NAME, "Obli");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 2);

        Log.e("", "Insertant cv= " + testValues.toString());

        Uri uri = this.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, testValues);
    }

    public void insertExpense(){

        ContentValues testValues = new ContentValues();
        testValues.put(Tables.DATE, "2015-01-23");
        testValues.put(Tables.CATEGORY_ID, "1");
        testValues.put(Tables.AMOUNT, 21);
        testValues.put(Tables.COMMENTS, "hola");


        Log.e("", "Insertant cv= " + testValues.toString());

        Uri uri = this.getContentResolver().insert(ExpensorContract.ExpenseEntry.CONTENT_URI, testValues);
    }

    public void updateCategory(){
        ContentValues testValues = new ContentValues();
        testValues.put(Tables.COLOR, 100);
        this.getContentResolver().update(
                ExpensorContract.CategoriesEntry.CONTENT_URI, testValues, Tables.ID + " = 1",
                null);
        Log.d("", "update " + testValues.toString());
    }

    public Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

}
