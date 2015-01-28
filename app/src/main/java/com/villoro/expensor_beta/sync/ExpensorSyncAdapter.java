package com.villoro.expensor_beta.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.parse.ParseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arnau on 28/01/2015.
 */
public class ExpensorSyncAdapter extends AbstractThreadedSyncAdapter{

    public final String LOG_TAG = ExpensorSyncAdapter.class.getSimpleName();
    private static String LAST_UPDATE_EXPENSOR = "last_update_expensor";
    private static long DEFAULT_DATE = 0;

    //TODO: https://www.udacity.com/course/viewer#!/c-ud853/l-1614738811/e-1664298713/m-1664298714

    public ExpensorSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //Here goes the sync
        Log.e(LOG_TAG, "starting to sync");
        Date finishDownload = readLastUpdateDate();

        //TODO download all objects in one step
        for (String tableName : Tables.TABLES) {
            Date auxDate = parseDownload(tableName);
            if (auxDate.after(finishDownload)){
                finishDownload = auxDate;
            }
        }

        Date finishUpload = parseUpload();

        Log.d(LOG_TAG, "finish Download= " + finishDownload.getTime() + ", finish Upload = " + finishUpload.getTime() );
        if(finishDownload.after(finishUpload)){
            saveLastUpdateDate(finishDownload);
        } else {
            saveLastUpdateDate(finishUpload);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */


        }
        return newAccount;
    }

    //--------------DATES LOGIC-------------------------

    public Date readLastUpdateDate(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_UPDATE_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

    public void saveLastUpdateDate(Date date){

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(LAST_UPDATE_EXPENSOR,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(LAST_UPDATE_EXPENSOR, date.getTime());
        editor.commit();
    }





    //--------------PARSE DOWNLOAD-------------------------

    public Date parseDownload(String tableName){

        Date lastUpdate = readLastUpdateDate();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan("updatedAt", readLastUpdateDate() );
        List<ParseObject> downloadedParseObjects = null;
        try {
            downloadedParseObjects = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(downloadedParseObjects != null){
            Log.d(LOG_TAG, "downloaded (" + tableName + ") " + downloadedParseObjects.size());
            Log.d(LOG_TAG, "size (" + tableName + ") " + downloadedParseObjects.size());

            for(ParseObject parseObject : downloadedParseObjects){
                Date updatedAt = parseObject.getUpdatedAt();

                Log.e(LOG_TAG, "upadated at= " + Utility.getStringFromDateUTC(updatedAt) + " > last update = "  + Utility.getStringFromDateUTC(lastUpdate) );
                Log.e(LOG_TAG, "updated at (long)= "+ updatedAt.getTime() + "> last update (long)= " + lastUpdate.getTime());
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

        ParseAdapter.tryToInsertSQLite(getContext(), contentValues, tableName);
    }






    //--------------PARSE UPLOAD-------------------------
    //TODO don't reupload objects downloaded

    public Date parseUpload(){

        Date lastUpdate = readLastUpdateDate();
        //TODO ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ListParseObjectsWithId auxCustomParseObjects = new ListParseObjectsWithId();

        for (String tableName : Tables.TABLES )
        {
            auxCustomParseObjects = parseTable(auxCustomParseObjects, tableName);
        }

        final List<ParseObject> parseObjects = auxCustomParseObjects.parseObjects;
        final List<Long> _ids = auxCustomParseObjects._ids;

        try {
            ParseObject.saveAll(parseObjects);
            Log.e(LOG_TAG,"objects saved= " + parseObjects.size());
            //update date of last sync
            lastUpdate = readLastUpdateDate();
            for(int i = 0; i < parseObjects.size(); i++) {
                Date updatedAt = parseObjects.get(i).getUpdatedAt();

                updateEntrySQL(parseObjects.get(i), _ids.get(i));

                if (updatedAt.after(lastUpdate)) {
                    lastUpdate = updatedAt;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lastUpdate;
    }

    public void updateEntrySQL(ParseObject parseObject, long _id){

        Log.e(LOG_TAG, "id= " + _id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.LAST_UPDATE, parseObject.getUpdatedAt().getTime() );
        contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());

        Log.d(LOG_TAG, "UPDATING: " + contentValues.toString());

        String tableName = parseObject.getClassName();
        ParseAdapter.updateWithId(getContext(), contentValues, tableName, _id);
        Log.e(LOG_TAG, "updated in SQL after upladint to parse");
    }

    //TODO tractar les dades que s'afegeixen mentre s'esta pujant info a parse.

    private ListParseObjectsWithId parseTable(ListParseObjectsWithId customParseObjects, String tableName){

        final Cursor cursor = ParseAdapter.getParseCursor(getContext(), tableName, readLastUpdateDate().getTime());

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

                //add _id and parseObject to the customParseObjects list
                customParseObjects._ids.add(cursor.getLong(cursor.getColumnIndex(Tables.ID)));
                customParseObjects.parseObjects.add(parseObject);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return customParseObjects;
    }

    class ListParseObjectsWithId {
        public List<ParseObject> parseObjects;
        public List<Long> _ids;

        public ListParseObjectsWithId(){
            parseObjects = new ArrayList<>();
            _ids = new ArrayList<>();
        }
    }






}
