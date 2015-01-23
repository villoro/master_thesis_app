package com.villoro.expensor_beta;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.util.Date;
import java.util.List;


public class ParseActivity extends ActionBarActivity {

    private static String LAST_UPDATE_EXPENSOR = "last_update_expensor";
    private static String DEFAULT_DATE = "1989-04-29 14:10:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_temp);
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

    public void click(View view){

        parseUpload(Tables.TABLENAME_CATEGORIES);
        //readParse(Tables.TABLENAME_CATEGORIES);

        readLastUpdateDate();


    }

    private Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR, Context.MODE_PRIVATE);
        String date = sharedPreferences.getString(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        Log.e("", "date load= " + date + " (long)= " + Utility.getDateUTCFromString(date).getTime());
        return Utility.getDateUTCFromString(date);
    }

    private void saveLastUpdateDate(Date date){

        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString(LAST_UPDATE_EXPENSOR, Utility.getStringFromDateUTC(date) );
        editor.commit();
        Log.e("", "date saved= " + Utility.getStringFromDateUTC(date) + " (long)= " + date.getTime());
    }

    public void readParse(String tableName){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan("updatedAt", readLastUpdateDate() );

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null)
                {
                    Log.e("", "it's working, size= " + parseObjects.size() );
                    Log.e("", "size " + parseObjects.size());
                    for(ParseObject parseObject : parseObjects){
                        Date updatedAt = parseObject.getUpdatedAt();
                        Date lastUpdate = readLastUpdateDate();
                        Log.e("", "upadated at= " + Utility.getStringFromDateUTC(updatedAt) + " > last update = "
                                + Utility.getStringFromDateUTC(lastUpdate) );

                        Log.e("", "updated at (long)= "+ updatedAt.getTime()
                        + "> last update (long)= " + lastUpdate.getTime());

                    }
                }
                else {
                    Log.e("", "shit, doesn't work");
                }
            }
        });
    }

    public void parseUpload(String tableName){

        final Cursor cursor = this.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null, null, null, null);

        Log.e("", "count= " + cursor.getCount() + ", columns= " + cursor.getColumnCount());
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        if (cursor.moveToFirst()){
            do{
                final ParseObject parseObject = new ParseObject(tableName);
                Tables table = new Tables(tableName);
                String[] columns = table.getColumns();
                String[] types = table.getTypes();

                //add _id

                //add concrete values
                for(int i = 0; i < columns.length; i++)
                {
                    if( types[i] == Tables.TYPE_DOUBLE)
                    {
                        parseObject.put(columns[i], String.valueOf(cursor.getDouble(cursor.getColumnIndex(columns[i]))));
                    }
                    else if (types[i] == Tables.TYPE_INT)
                    {
                        parseObject.put(columns[i], String.valueOf(cursor.getInt(cursor.getColumnIndex(columns[i]))));
                    }
                    else
                    {
                        parseObject.put(columns[i], cursor.getString(cursor.getColumnIndex(columns[i])));
                    }
                }

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Date updatedAt = parseObject.getUpdatedAt();
                        Date lastUpdate = readLastUpdateDate();
                        Log.e("", "parseID= " + parseObject.getObjectId());
                        Log.e("", "updatedAt= "+ Utility.getStringFromDateUTC(updatedAt) + " (long)= " + updatedAt.getTime());
                        Log.e("", "date= " + Utility.getStringFromActualDateUTC());
                        if(updatedAt.after(lastUpdate))
                        {
                            Log.d("", "updated at= " + updatedAt.getTime() + " > last update= " + lastUpdate.getTime());
                            Log.d("", "saving last update= " + updatedAt.getTime());
                            saveLastUpdateDate(updatedAt);
                        } else {
                            Log.d("", "updated at= " + updatedAt.getTime() + " < last update= " + lastUpdate.getTime());
                            Log.d("", "no need to save last update");
                        }

                    }
                });

                Log.e("", "it works :)");
                Log.e("", "uploading 1 object to parse");

            } while (cursor.moveToNext());
        }

    }

    public void insertExampleParse(){
        Log.e("","starting to add info");
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        final ParseObject testObject = new ParseObject("prova");
        testObject.put("amount",300);
        testObject.put("comment","patates");
        testObject.put("categoryID",1);
        testObject.put("date", "201501151306");

        testObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.e("", "parseID= " + testObject.getObjectId());
                                        }
        });

        Log.e("", "it works :)");
    }

    public void insertSQL(){

        ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "P");
        testValues.put(Tables.NAME, "Food");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 22);

        Log.e("", "Insertant cv= " + testValues.toString());

        Uri uri = this.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, testValues);

        Cursor cursor = this.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        cursor.moveToFirst();
        Log.e("", "count cursor= " + cursor.getCount());
        Log.e("", "count columns= " + cursor.getColumnCount());
        Log.e("", "last_update= " + cursor.getString(cursor.getColumnIndex(Tables.LAST_UPDATE)));
    }


    /*
    EXPENSOR_LAST_UPDATE = moment de l'ultima actualitzacio
    PARSE_ID = id a sqlite de parse
    LAST_CHANGE = ultim canvi local

    if(LAST_CHANGE > EXPENSOR_LAST_UPDATE){
        uploads
    }
    if(updatedAt > EXPENSOR_LAST_UPDATE) {
        download
    }

     */
}
