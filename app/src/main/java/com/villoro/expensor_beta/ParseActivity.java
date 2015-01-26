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
                sb.append("_id= " + cursor.getLong(cursor.getColumnIndex(Tables.ID)) + ", ");
                sb.append("name= " + cursor.getString(cursor.getColumnIndex(Tables.NAME)) + ", ");
                sb.append("color= " + cursor.getInt(cursor.getColumnIndex(Tables.COLOR)) + ", ");
                if(cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) !=  null){
                    sb.append("parseID= " + cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME)) + ", ");
                }
                sb.append("updatedAt= " + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE)));

                aux[i] = sb.toString();
                i++;
            } while (cursor.moveToNext());
        }

        for (String a : aux) {
            Log.d("", a);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aux);
        listView.setAdapter(arrayAdapter);

        lastUpdated.setText(Utility.getStringFromDateUTC(readLastUpdateDate()) +
                " " +readLastUpdateDate().getTime());
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

    public void upload(View view){
        parseUpload();
        setList();
    }

    public void download(View view){

        for (String tableName : Tables.TABLES)
        {
            parseDownload(tableName);
        }
        setList();
    }

    //--------------DATES LOGIC-------------------------

    private Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR, Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

    private void saveLastUpdateDate(Date date){

        SharedPreferences sharedPreferences = getSharedPreferences(LAST_UPDATE_EXPENSOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(LAST_UPDATE_EXPENSOR, date.getTime());
        editor.commit();
    }

    //--------------PARSE DOWNLOAD-------------------------

    public void parseDownload(String tableName){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan("updatedAt", readLastUpdateDate() );

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null)
                {
                    Log.e("", "it's working, size= " + parseObjects.size() );
                    Log.e("", "size " + parseObjects.size());
                    Date lastUpdate = readLastUpdateDate();

                    for(ParseObject parseObject : parseObjects){
                        Date updatedAt = parseObject.getUpdatedAt();

                        Log.e("", "upadated at= " + Utility.getStringFromDateUTC(updatedAt) + " > last update = "  + Utility.getStringFromDateUTC(lastUpdate) );
                        Log.e("", "updated at (long)= "+ updatedAt.getTime() + "> last update (long)= " + lastUpdate.getTime());
                        if(updatedAt.after(lastUpdate)) {
                            lastUpdate = updatedAt;
                        }
                        insertParseObjectInSQL(parseObject);
                    }
                    saveLastUpdateDate(lastUpdate);
                }
                else {

                    Log.e("", "shit, doesn't work");
                }

                setList();
            }
        });
    }

    private void insertParseObjectInSQL(ParseObject parseObject){

        ContentValues contentValues = new ContentValues();

        String tableName = parseObject.getClassName();
        Tables table = new Tables(tableName);
        String[] columns = table.getColumns();
        String[] types = table.getTypes();
        for(int i = 0; i < columns.length; i++) {
            if (types[i] == Tables.TYPE_DOUBLE) {
                contentValues.put(columns[i], parseObject.getDouble(columns[i]));
            } else if (types[i] == Tables.TYPE_INT) {
                contentValues.put(columns[i], parseObject.getInt(columns[i]));
            } else {
                contentValues.put(columns[i], parseObject.getString(columns[i]));
            }
        }
        contentValues.put(Tables.LAST_UPDATE, parseObject.getUpdatedAt().getTime());
        contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());

        ParseAdapter.tryToInsertSQLite(this, contentValues, tableName);
    }


    //--------------PARSE UPLOAD-------------------------

    public void parseUpload(){

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ListParseObjectsWithId auxParseObjects = new ListParseObjectsWithId();

        for (String tableName : Tables.TABLES )
        {
            auxParseObjects = parseTable(auxParseObjects, tableName);
        }

        final List<ParseObject> parseObjects = auxParseObjects.parseObjects;
        final List<Long> _ids = auxParseObjects._ids;

        ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                Log.e("","done: ");
                //update date of last sync
                Date lastUpdate = readLastUpdateDate();
                for(int i = 0; i < parseObjects.size(); i++){
                    Date updatedAt = parseObjects.get(i).getUpdatedAt();

                    updateEntrySQL(parseObjects.get(i), _ids.get(i));

                    if(updatedAt.after(lastUpdate)) {
                        lastUpdate = updatedAt;
                    }
                }
                saveLastUpdateDate(lastUpdate);

                setList();
            }
        });
    }

    public void updateEntrySQL(ParseObject parseObject, long _id){

        Log.e("", "id= " + _id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.LAST_UPDATE, parseObject.getUpdatedAt().getTime() );
        contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());

        Log.d("", "UPDATING: " + contentValues.toString());

        String tableName = parseObject.getClassName();
        ParseAdapter.updateWithId(this, contentValues, tableName, _id);
        Log.e("", "updated! ");
    }

    //TODO tractar les dades que s'afegeixen mentre s'esta pujant info a parse.

    private ListParseObjectsWithId parseTable(ListParseObjectsWithId parseObjects, String tableName){

        final Cursor cursor = ParseAdapter.getParseCursor(this, tableName, readLastUpdateDate().getTime());

        if(cursor.getCount() > 0){
            Log.e("", "Curor (no null) " + tableName + " count= " + cursor.getCount() + ", columns= " + cursor.getColumnCount());
        }

        if (cursor.moveToFirst()){
            do{
                String parseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                ParseObject parseObject;
                if(parseID != null){
                    parseObject = ParseObject.createWithoutData(tableName, parseID);
                    Log.e("", "intentant update parseID= " + parseID);
                } else {
                    Log.e("", "no hi ha pareID");
                    parseObject = new ParseObject(tableName);
                }

                Tables table = new Tables(tableName);
                String[] columns = table.getColumns();
                String[] types = table.getTypes();

                //add _id
                parseObjects._ids.add(cursor.getLong(cursor.getColumnIndex(Tables.ID)));

                //add values
                for(int i = 0; i < columns.length; i++)
                {
                    int index = cursor.getColumnIndex(columns[i]);
                    if (index >= 0){
                        if( types[i] == Tables.TYPE_DOUBLE)
                        {
                            parseObject.put(columns[i], cursor.getDouble(index));
                        }
                        else if (types[i] == Tables.TYPE_INT)
                        {
                            parseObject.put(columns[i], cursor.getInt(index));
                        }
                        else
                        {
                            parseObject.put(columns[i], cursor.getString(index));
                        }
                    } else {
                        Log.d("", "no existeix la columna");
                    }

                }

                parseObjects.parseObjects.add(parseObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return parseObjects;
    }

    class ListParseObjectsWithId {
        public List<ParseObject> parseObjects;
        public List<Long> _ids;

        public ListParseObjectsWithId(){
            parseObjects = new ArrayList<>();
            _ids = new ArrayList<>();
        }
    }

    /*public void insertExampleParse(){
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
    } */






    public void insertSQL(View v) {

        insertCategory();
        //insertExpense();
        //updateCategory();
        setList();
    }

    public void insertCategory(){

        ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "M");
        testValues.put(Tables.NAME, "Menjar");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 7);

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
        testValues.put(Tables.COLOR, 99999);
        this.getContentResolver().update(
                ExpensorContract.CategoriesEntry.CONTENT_URI, testValues, Tables.ID + " = 1",
                null);
        Log.d("", "update " + testValues.toString());
    }
}
