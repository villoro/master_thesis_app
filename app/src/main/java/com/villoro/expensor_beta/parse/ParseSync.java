package com.villoro.expensor_beta.parse;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arnau on 02/02/2015.
 */
public class ParseSync {

    public final String LOG_TAG = ParseSync.class.getSimpleName();
    private static String LAST_UPLOAD_EXPENSOR = "last_update_expensor";
    private static String LAST_DOWNLOAD_EXPENSOR = "last_download_expensor";
    private static long DEFAULT_DATE = 0;

    Context mContext;
    Date startDownload;
    Date startUpload;

    DateWithBoolean dateDownload;
    DateWithBoolean dateUpload;

    List<String> idsDownloaded;
    ListParseObjectsWithId objectsToUpload;

    //--------------PUBLIC PART-------------------------

    public ParseSync(Context context){
        mContext = context;
    }

    public void sync(){

        dateDownload = new DateWithBoolean(readDate(LAST_DOWNLOAD_EXPENSOR));
        dateUpload = new DateWithBoolean(readDate(LAST_UPLOAD_EXPENSOR));

        int i = 0;
        do {
            idsDownloaded = new ArrayList<>();
            parseDownloadAll();

            Log.d(LOG_TAG, "finish download= " + dateDownload.finish + "ITERACIO = " + i);

            if(dateDownload.finish){
                Log.e(LOG_TAG, "finish sync, download = " + dateDownload.date.getTime() + " (string)= " + dateDownload.date.toString());
                saveDate(LAST_DOWNLOAD_EXPENSOR, dateDownload.date);
            }
            i++;
        } while (!dateDownload.finish);

        i = 0;

        do {
            parseUpload();

            Log.d(LOG_TAG, "finish upload= " + dateUpload.finish + "ITERACIO = " + i);

            if(dateUpload.finish){
                Log.e(LOG_TAG, "finish sync, upload = " + dateUpload.date.getTime() + " (string)= " + dateUpload.date.toString());
                saveDate(LAST_UPLOAD_EXPENSOR, dateUpload.date);
            }
            i++;
        } while (!dateUpload.finish);
    }





    //--------------DATES LOGIC-------------------------

    private Date readDate(String whichDate){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(whichDate,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(whichDate, DEFAULT_DATE);
        return new Date(time);
    }

    private void saveDate(String whichDate, Date date){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(whichDate,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(whichDate, date.getTime());
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

        startDownload = new Date();

        Log.d(LOG_TAG, "starting to download at " + startDownload.getTime());
        dateDownload.finish = true;

        for (String tableName : Tables.TABLES) {
            if(parseDownload(tableName) > 0 ) {
                dateDownload.finish = false;
            }
        }
        dateDownload.date = startDownload;

        Log.d(LOG_TAG, "down date= " + dateDownload.date.getTime() + ", finish= " + dateDownload.finish);
    }

    private int parseDownload(String tableName){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan("updatedAt", dateDownload.date );
        List<ParseObject> downloadedParseObjects = null;
        try {
            downloadedParseObjects = query.find();
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
                insertParseObjectInSQL(parseObject);
            }
        }
        return downloadedParseObjects.size();
    }

    private void insertParseObjectInSQL(ParseObject parseObject){

        ContentValues contentValues = new ContentValues();

        String tableName = parseObject.getClassName();
        Tables table = new Tables(tableName);
        String[] columns = table.columns;
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

        boolean hasBeenDownloaded = ParseAdapter.tryToInsertSQLite(mContext, contentValues, tableName);
        if(hasBeenDownloaded){
            idsDownloaded.add(parseObject.getObjectId());
        }
    }






    //--------------PARSE UPLOAD-------------------------

    private void parseUpload(){

        startUpload = new Date();

        Log.d(LOG_TAG, "starting to upload= " + startUpload.getTime());

        //TODO ParseAnalytics.trackAppOpenedInBackground(getIntent());
        objectsToUpload = new ListParseObjectsWithId();

        for (String tableName : Tables.TABLES )
        {
            parseTable(tableName);
        }

        dateUpload.date = startUpload;

        final List<ParseObject> parseObjects = objectsToUpload.parseObjects;
        final List<Long> _ids = objectsToUpload._ids;

        try {
            dateUpload.finish = true;
            if(parseObjects.size() > 0){
                ParseObject.saveAll(parseObjects);
                Log.e(LOG_TAG,"objects saved= " + parseObjects.size());

                //update date of last sync
                for(int i = 0; i < parseObjects.size(); i++) {
                    updateEntrySQL(parseObjects.get(i), _ids.get(i));
                }
                dateUpload.finish = false;
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

    private void parseTable(String tableName){

        final Cursor cursor = ParseAdapter.getParseCursor(mContext, tableName, dateUpload.date.getTime());

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
                String[] columns = table.columns;
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

                //add _id and parseObject to the customParseObjects list
                if(!idsDownloaded.contains(parseID)){
                    Log.e(LOG_TAG, "object= " + parseID + " needs upload");
                    objectsToUpload._ids.add(cursor.getLong(cursor.getColumnIndex(Tables.ID)));
                    objectsToUpload.parseObjects.add(parseObject);
                } else {
                    Log.e(LOG_TAG, "no need to upload= " + parseID);
                }


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
