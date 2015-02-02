package com.villoro.expensor_beta.parse;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arnau on 02/02/2015.
 */
public class ParseSync {

    public final String LOG_TAG = ParseSync.class.getSimpleName();
    private static String LAST_UPDATE_EXPENSOR = "last_update_expensor";
    private static long DEFAULT_DATE = 0;

    Context mContext;
    Date startSync;

    DateWithBoolean dateDownload;
    DateWithBoolean dateUpload;

    ListParseObjectsWithId objectsToUpload;


    //--------------PUBLIC PART-------------------------

    public ParseSync(Context context){
        mContext = context;
        startSync = new Date();
    }

    public void sync(){

        dateDownload = new DateWithBoolean(readLastUpdateDate());
        dateUpload = new DateWithBoolean(dateDownload.date);

        int i = 0;
        do {
            parseDownloadAll();
            parseUpload();

            Log.d(LOG_TAG, "finish download= " + dateDownload.finish + ", finish upload= " + dateUpload.finish);
            Log.e(LOG_TAG, "ITERACIO = " + i);

            if(dateDownload.finish && dateUpload.finish){
                Log.e(LOG_TAG, "finish sync, down = " + dateDownload.date.getTime() + ", upload= " + dateUpload.date.getTime());
                Log.e(LOG_TAG, "finish sync, down = " + dateDownload.date.toString() + ", upload= " + dateUpload.date.toString());
                saveMaxDate(dateDownload.date, dateUpload.date, startSync);
            }
            i++;
        } while (!(dateDownload.finish && dateUpload.finish));

    }





    //--------------DATES LOGIC-------------------------

    private void saveMaxDate(Date... dates){
        Date maxDate = dates[0];
        for (Date date: dates){
            if(date.after(maxDate)){
                maxDate = date;
            }
        }
        Log.d(LOG_TAG, "date saved= " + maxDate.getTime() + " " + maxDate.toString());
        saveLastUpdateDate(maxDate);
    }

    private Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

    private void saveLastUpdateDate(Date date){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(LAST_UPDATE_EXPENSOR, date.getTime());
        editor.commit();
    }

    private class DateWithBoolean{
        boolean finish;
        Date date;

        public DateWithBoolean(Date date){
            finish = false;
            this.date = date;
        }
    }




    //--------------PARSE DOWNLOAD-------------------------

    //TODO download all objects in one step
    private void parseDownloadAll(){

        dateDownload.finish = true;
        Date tempDate = dateDownload.date;

        for (String tableName : Tables.TABLES) {
            Date auxDate = parseDownload(tableName, tempDate);
            if (auxDate.after(dateDownload.date)){
                dateDownload.date = auxDate;
                dateDownload.finish = false;
                Log.d(LOG_TAG, "new max date output= " + dateDownload.date.getTime());
            }
        }
        Log.d(LOG_TAG, "down date= " + dateDownload.date.getTime() + ", finish= " + dateDownload.finish);
    }

    private Date parseDownload(String tableName, Date lastDownload){

        Date lastUpdate = lastDownload;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan("updatedAt", lastDownload );
        List<ParseObject> downloadedParseObjects = null;
        try {
            downloadedParseObjects = query.find();
            Log.e(LOG_TAG, "QUERYYYYYYYYYYYYYY");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(downloadedParseObjects != null){

            //TEMP
            if(downloadedParseObjects.size() > 0){
                Log.d(LOG_TAG, "downloaded (" + tableName + ") " + downloadedParseObjects.size());
                Log.d(LOG_TAG, "size (" + tableName + ") " + downloadedParseObjects.size());
            }

            for(ParseObject parseObject : downloadedParseObjects){
                Date updatedAt = parseObject.getUpdatedAt();

                if(updatedAt.after(lastUpdate)) {
                    lastUpdate = updatedAt;
                }
                insertParseObjectInSQL(parseObject);
            }
        }

        return lastUpdate;
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

        ParseAdapter.tryToInsertSQLite(mContext, contentValues, tableName);
    }






    //--------------PARSE UPLOAD-------------------------
    //TODO don't reupload objects downloaded

    private void parseUpload(){

        Date tempDate = dateUpload.date;
        //TODO ParseAnalytics.trackAppOpenedInBackground(getIntent());
        objectsToUpload = new ListParseObjectsWithId();

        for (String tableName : Tables.TABLES )
        {
            parseTable(tableName, tempDate);
        }

        final List<ParseObject> parseObjects = objectsToUpload.parseObjects;
        final List<Long> _ids = objectsToUpload._ids;

        try {
            Log.e(LOG_TAG, "SAVIIIIIIIIIIIIIIIIING");
            ParseObject.saveAll(parseObjects);
            Log.e(LOG_TAG,"objects saved= " + parseObjects.size());
            //update date of last sync
            dateUpload.finish = true;
            for(int i = 0; i < parseObjects.size(); i++) {
                Date updatedAt = parseObjects.get(i).getUpdatedAt();

                updateEntrySQL(parseObjects.get(i), _ids.get(i));

                if (updatedAt.after(dateUpload.date)) {
                    dateUpload.date = updatedAt;
                    dateUpload.finish = false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateEntrySQL(ParseObject parseObject, long _id){

        Log.e(LOG_TAG, "id= " + _id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.LAST_UPDATE, parseObject.getUpdatedAt().getTime() );
        contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());

        Log.d(LOG_TAG, "UPDATING: " + contentValues.toString());

        String tableName = parseObject.getClassName();
        ParseAdapter.updateWithId(mContext, contentValues, tableName, _id);
        Log.e(LOG_TAG, "updated in SQL after upladint to parse");
    }

    private void parseTable(String tableName, Date lastUpdate){

        final Cursor cursor = ParseAdapter.getParseCursor(mContext, tableName, lastUpdate.getTime());

        if(cursor.getCount() > 0){
            Log.e(LOG_TAG, "Curor (no null) " + tableName + " count= " + cursor.getCount() + ", columns= " + cursor.getColumnCount());
        }

        if (cursor.moveToFirst()){
            do{
                String parseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                ParseObject parseObject;
                if(parseID != null){
                    parseObject = ParseObject.createWithoutData(tableName, parseID);
                    Log.e(LOG_TAG, "intentant update parseID= " + parseID);
                } else {
                    Log.e(LOG_TAG, "no hi ha pareID");
                    parseObject = new ParseObject(tableName);
                }

                Tables table = new Tables(tableName);
                String[] columns = table.getColumns();
                String[] types = table.getTypes();

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
                        Log.d(LOG_TAG, "no existeix la columna");
                    }
                }

                //TODO
                //objectsDownloaded.ids.contains..

                //add _id and parseObject to the customParseObjects list
                objectsToUpload._ids.add(cursor.getLong(cursor.getColumnIndex(Tables.ID)));
                objectsToUpload.parseObjects.add(parseObject);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private class ListParseObjectsWithId {
        public List<ParseObject> parseObjects;
        public List<Long> _ids;

        public ListParseObjectsWithId(){
            parseObjects = new ArrayList<>();
            _ids = new ArrayList<>();
        }
    }

}
