package com.villoro.expensor_beta.parse;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arnau on 02/02/2015.
 */
public class ParseSync {

    public final String LOG_TAG = ParseSync.class.getSimpleName();
    //TODO make private
    private static String LAST_SYNC_EXPENSOR = "last_sync_expensor";
    private static String UPDATED_AT = "updatedAt";
    private static final String WHO_PAID_ID = "whoPaid";
    private static final String WHO_SPENT_ID = "whoSpent";
    private static long DEFAULT_DATE = 0;
    private static String PARSE_USER_TYPE = "_User";
    private static String PARSE_PUBLIC_PERSON_TYPE = "PublicPeople";

    Context mContext;
    Date startSync;
    Date dateSync;

    int objectsDownloaded, objectsUploaded;

    boolean finish;

    List<String> idsDownloaded;
    List<String> needACL;
    ListParseObjectsWithId objectsToUpload;
    ListParseObjectsWithId previousUploaded;

    //--------------PUBLIC PART-------------------------

    public ParseSync(Context context){
        mContext = context;
    }

    public void sync(){

        objectsToUpload = new ListParseObjectsWithId();
        previousUploaded = new ListParseObjectsWithId();

        int p = 0;
        while (true){
            //prepare iteration
            dateSync = readDate();
            startSync = new Date();
            Log.e("", "DATE QUERY= " + dateSync.getTime());
            objectsDownloaded = 0;
            objectsUploaded = 0;
            idsDownloaded = new ArrayList<>();

            //download
            parseDownloadAll();

            //prepare upload
            previousUploaded = objectsToUpload;
            objectsToUpload = new ListParseObjectsWithId();

            //upload
            parseUpload();
            //prepare next iteration
            saveDate(startSync);
            Log.e("", "DATE SAVED= " + startSync.getTime());
            Log.d("", "iteracio= "+ p+" ,downloaded= " + objectsDownloaded + " ,uploaded= " + objectsUploaded);

            //finish iterations if needed
            if(objectsDownloaded == 0 && objectsUploaded == 0){
                break; //finish iterations
            }
            p++;
        }
    }





    //--------------DATES LOGIC-------------------------

    public Date readDate(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LAST_SYNC_EXPENSOR,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(LAST_SYNC_EXPENSOR, DEFAULT_DATE);
        return new Date(time);
    }

    private void saveDate(Date date){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LAST_SYNC_EXPENSOR,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(LAST_SYNC_EXPENSOR, date.getTime());
        editor.commit();
    }

    public static void resetLastSync(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(LAST_SYNC_EXPENSOR,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(LAST_SYNC_EXPENSOR, 0);
        editor.commit();
    }




    //--------------PARSE DOWNLOAD-------------------------

    //TODO download all objects in one step
    private void parseDownloadAll(){

        Log.d(LOG_TAG, "starting to download at " + startSync.getTime());

        parseCheckNewUser();

        //level 1
        parseDownload(Tables.TABLENAME_GROUPS);
        parseDownload(Tables.TABLENAME_CATEGORIES);
        //level 2
        parseDownload(Tables.TABLENAME_TRANSACTIONS_GROUP);
        parseDownload(Tables.TABLENAME_TRANSACTION_SIMPLE);
        //level 3
        parseDownload(Tables.TABLENAME_PEOPLE);
        //level 4
        parseDownload(Tables.TABLENAME_WHO_PAID_SPENT);
        parseDownload(Tables.TABLENAME_PEOPLE_IN_GROUP);
        parseDownload(Tables.TABLENAME_HOW_TO_SETTLE);
        parseDownload(Tables.TABLENAME_TRANSACTIONS_PEOPLE);
    }

    private void parseCheckNewUser(){
        //TODO don't do if first download
        needACL = new ArrayList<>();
        ArrayList<String> peopleWithNoPoints = ParseAdapter.getEmailsPeopleWithNoPoints(mContext);
        for(String emailPeople : peopleWithNoPoints){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", emailPeople);
            try {
                List<ParseUser> parseUsers = query.find();
                for (ParseUser parseUser : parseUsers) {
                    if(ParseAdapter.updatePeoplePointsTo(mContext, emailPeople, parseUser.getObjectId()) > 0){
                        needACL.add(parseUser.getObjectId());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private int parseDownload(String tableName){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereGreaterThan(UPDATED_AT, dateSync);

        //Log.e("", "date query= " + dateSync + " (" + dateSync.getTime() + ")");

        //include related objects if needed
        Tables table = new Tables(tableName);
        for(int i = 0; i < table.columns.length; i++){
            if(table.origin[i] != null){
                if(table.origin[i] == Tables.TABLENAME_PEOPLE){
                    query.include(PARSE_PUBLIC_PERSON_TYPE);
                } else {
                    query.include(table.columns[i]);
                }
            }
        }

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
                if(!objectsToUpload.parseIDs.contains(parseObject.getObjectId())){
                    insertParseObjectInSQL(parseObject);
                }
            }
        }
        return downloadedParseObjects.size();
    }

    private long insertParseObjectInSQL(ParseObject parseObject){

        //initialize
        ContentValues contentValues = new ContentValues();
        String tableName = parseObject.getClassName();
        Tables table = new Tables(tableName);
        String[] columns = table.columns;
        String[] types = table.getTypes();
        String[] origin = table.origin;
        long output; //id in SQLite of the object inserted

        //save every column
        for(int i = 0; i < columns.length; i++) {
            if(origin[i] != null){
                //this is a foreign key, needs to be retrieved
                ParseObject foreignObject;

                //special case transactionPeople
                if(columns[i] == Tables.PEOPLE_ID && tableName.equals(Tables.TABLENAME_TRANSACTIONS_PEOPLE)){
                    String whoPaidId = parseObject.getParseObject(WHO_PAID_ID).getObjectId();
                    String whoSpentId = parseObject.getParseObject(WHO_SPENT_ID).getObjectId();
                    String myId = ParseAdapter.getMyParsePublicId(mContext);
                    if(whoPaidId.equals(myId)){
                        foreignObject = parseObject.getParseObject(WHO_SPENT_ID);
                        contentValues.put(Tables.AMOUNT, parseObject.getDouble(Tables.AMOUNT));
                    } else if (whoSpentId.equals(myId)){
                        foreignObject = parseObject.getParseObject(WHO_PAID_ID);
                        contentValues.put(Tables.AMOUNT, - parseObject.getDouble(Tables.AMOUNT));
                    } else {
                        foreignObject = null;
                    }

                //usual foreign case
                } else {
                    foreignObject = parseObject.getParseObject(columns[i]);
                }
                Log.d("", "working in= " + parseObject.getClassName() + " with colum= " + columns[i] + " with origin= " + origin[i]);
                Log.d("", "at column= " + foreignObject.getClassName());
                String foreignParseID = foreignObject.getObjectId();
                long foreignID;
                if(foreignParseID == null || foreignParseID.equals("")){
                    Log.e("", "CALLING NESTED INSERT");
                    foreignID = insertParseObjectInSQL(foreignObject);
                } else {
                    foreignID = ParseAdapter.getIdFromParseId(mContext, origin[i], foreignObject.getObjectId(), Tables.PARSE_ID_NAME);
                }

                contentValues.put(columns[i], foreignID);

            } else {

                //it is a normal value, not a foreign key
                if (types[i] == Tables.TYPE_DOUBLE) {
                    if(!tableName.equals(Tables.TABLENAME_TRANSACTIONS_PEOPLE)){
                        contentValues.put(columns[i], parseObject.getDouble(columns[i]));
                    }
                } else if (types[i] == Tables.TYPE_INT) {
                    contentValues.put(columns[i], parseObject.getInt(columns[i]));
                } else {
                    contentValues.put(columns[i], parseObject.getString(columns[i]));
                }
            }
        }

        //add parse values
        contentValues.put(Tables.LAST_UPDATE, parseObject.getUpdatedAt().getTime());
        contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());

        //save id if it has been saved
        output = ParseAdapter.tryToInsertSQLite(mContext, contentValues, tableName);
        if(output >= 0){
            idsDownloaded.add(parseObject.getObjectId());
            objectsDownloaded++;
        }
        return output;
    }





    //--------------PARSE UPLOAD-------------------------

    private void parseUpload(){

        //TODO ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Log.d("", "starting to upload, query time= " + dateSync + " (" + dateSync.getTime() + ")");

        //level 1
        parseTable(Tables.TABLENAME_GROUPS);
        parseTable(Tables.TABLENAME_CATEGORIES);
        //level 2
        parseTable(Tables.TABLENAME_TRANSACTIONS_GROUP);
        parseTable(Tables.TABLENAME_TRANSACTION_SIMPLE);
        //level 3
        parseTable(Tables.TABLENAME_PEOPLE);
        //level 4
        parseTable(Tables.TABLENAME_WHO_PAID_SPENT);
        parseTable(Tables.TABLENAME_PEOPLE_IN_GROUP);
        parseTable(Tables.TABLENAME_HOW_TO_SETTLE);
        parseTable(Tables.TABLENAME_TRANSACTIONS_PEOPLE);

        for (String parsePeopleID : needACL){
            long SQLPeopleID = ParseAdapter.getIdFromParseId(mContext, Tables.TABLENAME_PEOPLE, parsePeopleID, Tables.REAL_USER_ID);
            for (String tableName : Tables.TABLES) {
                //updateACL for every table that is not individual
                if(!(new Tables(tableName)).acl.equals(Tables.ACL_INDIVIDUAL)) {
                    Log.d("", "adding " + tableName + " ACL for user " + parsePeopleID);
                    updateACL(tableName, SQLPeopleID);
                }
            }
        }

        final List<ParseObject> parseObjects = objectsToUpload.parseObjects;
        final List<Long> _ids = objectsToUpload._ids;

        /*Log.e("", "trying to upload= " + parseObjects.size());
        for (ParseObject parseObject : parseObjects){
            Log.d("", "trying to uplad= " + parseObject.getClassName());
        }*/

        try {
            if(parseObjects.size() > 0){
                ParseObject.saveAll(parseObjects);
                Log.e(LOG_TAG,"objects saved= " + parseObjects.size());

                //update date of last sync
                for(int i = 0; i < parseObjects.size(); i++) {
                    updateEntrySQL(parseObjects.get(i), _ids.get(i));
                    objectsToUpload.parseIDs.add(parseObjects.get(i).getObjectId());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseTable(String tableName){

        final Cursor cursor = ParseAdapter.getSmartCursor(mContext, tableName, dateSync.getTime(), 0);

        if (cursor.moveToFirst()){
            do{
                //Extract parseObject from cursor
                String parseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                ParseObject parseObject = createParseObjectFromCursor(cursor, tableName, parseID);

                //add _id and parseObject to the customParseObjects list
                addToObjectsToUpload(cursor, tableName, parseID, parseObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void updateACL(String tableName, long peopleID){
        final Cursor cursor = ParseAdapter.getSmartCursor(mContext, tableName, 0, peopleID);
        if (cursor.moveToFirst()){
            do{
                //Extract parseObject from cursor
                String parseId = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                boolean needUpload = true;
                for(String objectToUploadID : objectsToUpload.parseIDs){
                    if(parseId.equals(objectToUploadID)){
                        needUpload = false;
                    }
                }
                if(needUpload) {
                    String parseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                    ParseObject parseObject = getParseObjectInOrderToUpdateACL(cursor, tableName, parseID);
                    objectsToUpload.addObject(parseObject, cursor.getLong(cursor.getColumnIndex(Tables.ID)), tableName);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private ParseObject createParseObjectFromCursor(Cursor cursor, String tableName, String parseID){
        ParseObject parseObject;
        //Log.d(LOG_TAG, "object to upload with time= " + cursor.getLong(cursor.getColumnIndex(Tables.LAST_UPDATE)));
        if(parseID != null){
            parseObject = ParseObject.createWithoutData(tableName, parseID);
            Log.e(LOG_TAG, "intentant update parseID= " + parseID);
        } else {
            Log.e(LOG_TAG, "no hi ha pareID");
            parseObject = new ParseObject(tableName);
        }

        Log.e("", "creating parseObject for table= " + tableName);
        //Initialize table
        Tables table = new Tables(tableName);
        String[] columns = table.columns;
        String[] origin = table.origin;
        String[] types = table.getTypes();

        //add values
        for(int i = 0; i < columns.length; i++)
        {
            //Reading all columns
            int index = cursor.getColumnIndex(columns[i]);
            //Check if it's a foreign key
            if(origin[i] == null){
                //Normal value, add to object
                if (index >= 0){
                    if( types[i] == Tables.TYPE_DOUBLE)
                    {
                        parseObject.put(columns[i], Math.abs(cursor.getDouble(index)));
                    }
                    else if (types[i] == Tables.TYPE_INT)
                    {
                        parseObject.put(columns[i], cursor.getInt(index));
                    }
                    else
                    {
                        if(cursor.getString(index) != null) {
                            parseObject.put(columns[i], cursor.getString(index));
                        }
                    }
                } else {
                    Log.d(LOG_TAG, "no existeix la columna");
                }
            } else {
                //It is a foreign key
                int parseIndex = cursor.getColumnIndex(columns[i] + ParseQueries.PARSE);
                String parseForeignID = cursor.getString(parseIndex);

                //special treatment for transaction_people
                String columnToStore = columns[i];
                String columnMyId;
                if((tableName == Tables.TABLENAME_TRANSACTIONS_PEOPLE) && (columns[i] == Tables.PEOPLE_ID)){
                    double amount = cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT));
                    if(amount > 0){
                        columnToStore = WHO_SPENT_ID;
                        columnMyId = WHO_PAID_ID;
                    } else {
                        columnToStore = WHO_PAID_ID;
                        columnMyId = WHO_SPENT_ID;
                    }
                    String myParseId = ParseAdapter.getMyParsePublicId(mContext);
                    //add my id
                    if(myParseId != null && myParseId.length() > 0){
                        parseObject.put(columnMyId,
                                ParseObject.createWithoutData(PARSE_PUBLIC_PERSON_TYPE, myParseId));
                    } else {
                        ParseObject innerObject = createNewPublicUser(ParseUser.getCurrentUser().getEmail());
                        parseObject.put(columnMyId, innerObject);
                        objectsToUpload.addObject(innerObject, 0, PARSE_PUBLIC_PERSON_TYPE);
                    }
                }

                if(origin[i] == Tables.TABLENAME_PEOPLE){
                    ParseObject innerObject;
                    parseForeignID = cursor.getString(cursor.getColumnIndex(Tables.PUBLIC_USER_ID + ParseQueries.PARSE));
                    if (parseForeignID != null && parseForeignID.length() > 0){
                        innerObject = ParseObject.createWithoutData(PARSE_PUBLIC_PERSON_TYPE, parseForeignID);
                    } else {
                        String email = cursor.getString(cursor.getColumnIndex(Tables.EMAIL + ParseQueries.PARSE));
                        innerObject = createNewPublicUser(email);
                        objectsToUpload.addObject(innerObject, 0, PARSE_PUBLIC_PERSON_TYPE);
                    }
                    parseObject.put(columnToStore, innerObject);
                } else {
                    if (parseForeignID != null && parseForeignID.length() > 0) {
                        ParseObject innerObject;
                        innerObject = ParseObject.createWithoutData(origin[i], parseForeignID);
                        parseObject.put(columnToStore, innerObject);
                    } else {
                        for (int j = 0; j < objectsToUpload._ids.size(); j++) {
                            //find the object
                            if (objectsToUpload.type.get(j).equals(origin[i])) {
                                if (objectsToUpload._ids.get(j) == cursor.getLong(index)) {
                                    //add the object here
                                    parseObject.put(columnToStore, objectsToUpload.parseObjects.get(j));
                                }
                            }
                        }
                    }
                }
            }
        }

        //set ACL
        parseObject.setACL(getParseACLFromSQLite(table, cursor));

        return parseObject;
    }

    private ParseObject getParseObjectInOrderToUpdateACL(Cursor cursor, String tableName, String parseID){
        ParseObject parseObject;
        if(parseID != null){
            parseObject = ParseObject.createWithoutData(tableName, parseID);
            parseObject.setACL(getParseACLFromSQLite(new Tables(tableName), cursor));
            return parseObject;
        } else {
            Log.e("", "NOT POSSIBLE CASE");
            return null;
        }
    }

    private ParseACL getParseACLFromSQLite(Tables table, Cursor cursor){
        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        switch (table.acl){
            case Tables.ACL_INDIVIDUAL:
                break;
            case Tables.ACL_ONE_PERSON:
                String personID = cursor.getString(cursor.getColumnIndex(Tables.REAL_USER_ID + ParseQueries.PARSE));
                if(personID != null){
                    parseACL.setReadAccess(personID, true);
                    parseACL.setWriteAccess(personID, true);
                }
                break;
            case Tables.ACL_GROUP:

                //get groupParseID
                String groupID = null;
                if(table.tableName == Tables.TABLENAME_GROUPS){
                    groupID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                } else {
                    groupID = cursor.getString(cursor.getColumnIndex(Tables.GROUP_ID + ParseQueries.PARSE));
                }

                //get and setACL for people in the group
                if(groupID != null) {
                    ArrayList<String> peopleID = ParseAdapter.getPeopleInGroup(mContext, groupID);
                    for (String eachPerson : peopleID) {
                        if (eachPerson != null) {
                            parseACL.setReadAccess(eachPerson, true);
                            parseACL.setWriteAccess(eachPerson, true);
                        }
                    }
                }
                break;
        }
        return parseACL;
    }

    private void addToObjectsToUpload(Cursor cursor, String tableName, String parseID, ParseObject parseObject){
        Log.d("", "trying to upload " + parseObject.getClassName());
        //check if it's been downloaded
        if(!idsDownloaded.contains(parseID)){
            Log.e(LOG_TAG, "object= " + parseID + " needs upload");

            //check if it was downloaded in a previous iteration
            if(previousUploaded.parseIDs.contains(parseID)){
                Log.d("", "has been updated previously");
                for(int k = 0; k < previousUploaded.parseIDs.size(); k++){

                    //check if there are changes from the previous upload
                    if( (previousUploaded.parseIDs.get(k).equals(parseID)) &&
                            (previousUploaded.parseObjects.get(k).getUpdatedAt().getTime() < parseObject.getUpdatedAt().getTime()) ){
                        Log.d("", "has change de date");
                        objectsToUpload.addObject(parseObject, cursor.getLong(cursor.getColumnIndex(Tables.ID)), tableName);
                    }
                }
            } else {
                Log.d("", "Not uploaded previously");
                objectsToUpload.addObject(parseObject, cursor.getLong(cursor.getColumnIndex(Tables.ID)), tableName);
            }

        } else {
            Log.e(LOG_TAG, "no need to upload= " + parseID);
        }
    }

    private void updateEntrySQL(ParseObject parseObject, long _id){

        ContentValues contentValues = new ContentValues();
        contentValues.put(Tables.LAST_UPDATE, startSync.getTime() );

        String tableName = parseObject.getClassName();
        if(tableName.equals(PARSE_PUBLIC_PERSON_TYPE)){
            contentValues.put(Tables.PUBLIC_USER_ID, parseObject.getObjectId());
            tableName = Tables.TABLENAME_PEOPLE;
        } else {
            contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());
        }
        ParseAdapter.updateWithId(mContext, contentValues, tableName, _id);

        objectsUploaded++;
    }

    private class ListParseObjectsWithId {
        public List<ParseObject> parseObjects;
        public List<Long> _ids;
        public List<String> type;
        public List<String> parseIDs;

        public ListParseObjectsWithId(){
            parseObjects = new ArrayList<>();
            _ids = new ArrayList<>();
            type = new ArrayList<>();
            parseIDs = new ArrayList<>();
        }

        public void addObject(ParseObject parseObject, long id, String typeParseObject){
            parseObjects.add(parseObject);
            _ids.add(id);
            type.add(typeParseObject);
        }
    }

    private ParseObject createNewPublicUser(String email){
        ParseObject parseObject = new ParseObject(PARSE_PUBLIC_PERSON_TYPE);
        parseObject.put(Tables.EMAIL, email);
        return parseObject;
    }

}
