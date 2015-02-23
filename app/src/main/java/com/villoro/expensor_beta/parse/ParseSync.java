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
    private static String PRIVATE = "Private";

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
                        Log.d("", "user " + parseUser.getObjectId() + ", with email= " + emailPeople + " need ACL");
                        needACL.add(parseUser.getObjectId());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private int parseDownload(String tableName){

        Tables table = new Tables(tableName);
        String parseClassName;
        if(table.lastPrivateColumn < 0){
            parseClassName = tableName;
        } else {
            parseClassName = tableName + PRIVATE;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName);
        query.whereGreaterThan(UPDATED_AT, dateSync);

        //Log.e("", "date query= " + dateSync + " (" + dateSync.getTime() + ")");

        //include related objects if needed
        for(int i = 0; i < table.columns.length; i++){
            if(table.origin[i] != null){
                query.include(table.columns[i]);
            }
        }
        if(table.lastPrivateColumn > 0 && table.lastPrivateColumn < table.columns.length){
            query.include(Tables.POINTS);
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
                    //decide whichs parts exists (private and/or public)
                    String parseID = parseObject.getObjectId();
                    long updatedAt = parseObject.getUpdatedAt().getTime();
                    if(table.lastPrivateColumn < 0){
                        //Log.d("", "calling all shared");
                        insertParseObjectInSQL(null, parseObject, parseID, updatedAt);
                    } else if(table.lastPrivateColumn > 0 && table.lastPrivateColumn < table.columns.length){
                        //Log.d("", "calling partShared and partPrivate");
                        ParseObject sharedPart = parseObject.getParseObject(Tables.POINTS);
                        insertParseObjectInSQL(parseObject, sharedPart, parseID, updatedAt);
                    } else {
                        //Log.d("", "calling all private");
                        insertParseObjectInSQL(parseObject, null, parseID, updatedAt);
                    }
                }
            }
        }
        return downloadedParseObjects.size();
    }

    private void insertParseObjectInSQL(ParseObject privateObject, ParseObject sharedObject, String parseID, long updatedAt){

        //initialize
        ContentValues contentValues = new ContentValues();
        String tableName;
        if(sharedObject != null) {
            tableName = sharedObject.getClassName();
        } else if (privateObject != null) {
            tableName = privateObject.getClassName().replace(PRIVATE, "");
        } else {
            tableName = null;
        }

        Tables table = new Tables(tableName);
        String[] columns = table.columns;
        String[] types = table.getTypes();
        String[] origin = table.origin;

        //save every column
        for(int i = 0; i < columns.length; i++) {
            //aux ParseObject to decide if take the private or public
            ParseObject parseObject;
            if(table.lastPrivateColumn <= i){
                parseObject = sharedObject;
            } else {
                parseObject = privateObject;
            }

            //new user signed in, adding ACL
            if(columns[i] == Tables.USER_ID){
                Log.d("insertParseObjectInSQL", "people " + sharedObject.getObjectId() + " could need ACL");
                String userID = parseObject.getString(Tables.USER_ID);
                if(userID != null && userID.length() > 0) //no null pointer
                {
                    boolean wasPrevious = ParseAdapter.thatPersonHadPreviouslyUserID(mContext, privateObject.getString(Tables.EMAIL));
                    Log.d("insertParseObjectInSQL", "the people was previous= " + wasPrevious + ", with id= " + sharedObject.getObjectId());

                    String myId = ParseAdapter.getMyParseId(mContext);
                    Log.d("insertParseObjectInSQL", "my id= " + myId);
                    if (!ParseAdapter.thatPersonHadPreviouslyUserID(mContext, privateObject.getString(Tables.EMAIL)) && //the pointer to user is new
                            !sharedObject.getObjectId().equals(ParseAdapter.getMyParseId(mContext)))//it's not me
                    {
                        Log.d("insertParseObjectInSQL", "he needs ACL");
                        needACL.add(userID);
                    } else {
                        Log.d("insertParseObjectInSQL", "he don't need ACL");
                    }
                }
            }


            if(origin[i] != null){
                //this is a foreign key, needs to be retrieved
                ParseObject foreignObject;

                //special case transactionPeople
                if(columns[i] == Tables.PEOPLE_ID && tableName.equals(Tables.TABLENAME_TRANSACTIONS_PEOPLE)){
                    String whoPaidId = parseObject.getParseObject(WHO_PAID_ID).getObjectId();
                    String whoSpentId = parseObject.getParseObject(WHO_SPENT_ID).getObjectId();
                    String myId = ParseAdapter.getMyParseId(mContext);
                    if(whoPaidId.equals(myId)){
                        foreignObject = parseObject.getParseObject(WHO_SPENT_ID);
                        contentValues.put(Tables.AMOUNT, parseObject.getDouble(Tables.AMOUNT));
                    } else if (whoSpentId.equals(myId)){
                        foreignObject = parseObject.getParseObject(WHO_PAID_ID);
                        contentValues.put(Tables.AMOUNT, - parseObject.getDouble(Tables.AMOUNT));
                    } else {
                        foreignObject = null;
                    }

                    //usual case
                } else {
                    foreignObject = parseObject.getParseObject(columns[i]);
                }
                String whichColumn;
                Tables originalTable = new Tables(origin[i]);
                if(originalTable.lastPrivateColumn > 0 && originalTable.lastPrivateColumn < originalTable.columns.length){
                    whichColumn = Tables.POINTS;
                } else {
                    whichColumn = Tables.PARSE_ID_NAME;
                }
                long foreignID = ParseAdapter.getIdFromParseId(mContext, origin[i], foreignObject.getObjectId(), whichColumn);
                contentValues.put(columns[i], foreignID);

            } else {
                //it is a normal value, not a foreign key
                if(columns[i] != Tables.POINTS) {
                    if (types[i] == Tables.TYPE_DOUBLE) {
                        if (!tableName.equals(Tables.TABLENAME_TRANSACTIONS_PEOPLE)) {
                            contentValues.put(columns[i], parseObject.getDouble(columns[i]));
                        }
                    } else if (types[i] == Tables.TYPE_INT) {
                        contentValues.put(columns[i], parseObject.getInt(columns[i]));
                    } else {
                        contentValues.put(columns[i], parseObject.getString(columns[i]));
                    }
                } else {
                    contentValues.put(Tables.POINTS, sharedObject.getObjectId());
                }
            }
        }

        //add parse values
        contentValues.put(Tables.LAST_UPDATE, updatedAt);
        contentValues.put(Tables.PARSE_ID_NAME, parseID);

        //save id if it has been saved
        boolean hasBeenDownloaded = ParseAdapter.tryToInsertSQLite(mContext, contentValues, tableName);
        if(hasBeenDownloaded){
            idsDownloaded.add(parseID);
            objectsDownloaded++;
            if(privateObject != null && sharedObject != null){
                idsDownloaded.add(contentValues.getAsString(Tables.POINTS));
                objectsDownloaded++;
            }
        }
    }





    //--------------PARSE UPLOAD-------------------------

    private void parseUpload(){

        //TODO ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Log.d("", "starting to upload, query time= " + dateSync + " (" + dateSync.getTime() + ")");
        Log.d("parseUpload", "objectsToUpload count= " + objectsToUpload.parseObjects.size() + " (should be 0)");

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
        Log.d("parseUpload", "objectsToUpload count= " + objectsToUpload.parseObjects.size() + " (after level 4)");

        Log.e("parseUpload", "there are " + needACL.size() + " needACL");
        Log.e("parseUpload", "my id is " + ParseAdapter.getMyParseId(mContext));

        if(needACL.remove(ParseAdapter.getMyParseId(mContext))){
            Log.d("parseUpload", "my id removed");
        } else {
            Log.d("parseUpload", "my id not removed");
        }

        for (String parsePeopleID : needACL){
            long SQLPeopleID = ParseAdapter.getIdFromParseId(mContext, Tables.TABLENAME_PEOPLE, parsePeopleID, Tables.USER_ID);
            for (String tableName : Tables.TABLES) {
                //updateACL for every table that is not individual and it's not people table
                if(!(new Tables(tableName)).acl.equals(Tables.ACL_INDIVIDUAL) &&
                        !tableName.equals(Tables.TABLENAME_PEOPLE)) {
                    Log.d("parseUpload", "adding " + tableName + " ACL for user " + parsePeopleID + " , and id= " + SQLPeopleID);
                    updateACL(tableName, SQLPeopleID);
                    Log.d("parseUpload", "objectsToUpload count= " + objectsToUpload.parseObjects.size());
                }
            }
        }

        Log.e("parseUpload", "there are " + objectsToUpload.parseObjects.size() + " to upload");
        Log.e("parseUpload", "there are " + objectsToUpload._ids.size() + " to upload");
        Log.e("parseUpload", "there are " + objectsToUpload.parseIDs.size() + " to upload");



        final List<ParseObject> parseObjects = objectsToUpload.parseObjects;
        final List<Long> _ids = objectsToUpload._ids;

        for (ParseObject auxObject : parseObjects){
            Log.d("parseUpload", "object with className= " + auxObject.getClassName() + ", id= " + auxObject.getObjectId());
            Log.d("parseUpload", auxObject.toString());
        }



        try {
            if(parseObjects.size() > 0){
                ParseObject.saveAll(parseObjects);
                Log.e("parseUpload","objects saved= " + parseObjects.size());

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
                Tables table = new Tables(tableName);
                String parseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                String privateID;
                if(0 < table.lastPrivateColumn && table.lastPrivateColumn < table.columns.length){
                    //there is a part shared and a part private with a pointer
                    privateID = cursor.getString(cursor.getColumnIndex(Tables.POINTS));
                    //Log.d("parseTable", "table= " + tableName + " is private and shared");
                } else if (table.lastPrivateColumn < 0){
                    //only shared part
                    privateID = null;
                } else {
                    //only private part
                    privateID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                    //Log.d("parseTable", "table= " + tableName + " is only private");
                } //Log.d("parseTable", "lastPrivateColumn =" + table.lastPrivateColumn + ", columns= " + table.columns.length);

                ParseObject innerObject = null;
                //Log.d("parseTable", "trying to parse " + tableName);

                if(table.lastPrivateColumn < table.columns.length){

                    //work with shared part
                    String innerParseID;
                    innerParseID = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));

                    //Log.d("parseTable", "starting createParseObjcetFromCursor");
                    //Log.e("parseTable", "calling createParseObject for public with id= " +innerParseID);
                    innerObject = createParseObjectFromCursor(
                            cursor, table, innerParseID, false, null);
                    //Log.e("parseTable", "calling addObjectsToUpload for inner");
                    long _id = cursor.getLong(cursor.getColumnIndex(Tables.ID));
                    addToObjectsToUpload(_id, parseID, innerObject);
                }

                if(table.lastPrivateColumn > 0){
                    //work with the private part
                    ParseObject parseObject = createParseObjectFromCursor(
                            cursor, table, privateID, true, innerObject);

                    //add _id and parseObject to the customParseObjects list
                    long _id = cursor.getLong(cursor.getColumnIndex(Tables.ID));

                    addToObjectsToUpload(_id, parseID, parseObject);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void updateACL(String tableName, long peopleID){
        final Cursor cursor = ParseAdapter.getSmartCursor(mContext, tableName, 0, peopleID);
        if (cursor.moveToFirst()){
            do{
                Tables table = new Tables(tableName);
                String parseId;
                //Extract parseObject from cursor
                if(0 < table.lastPrivateColumn && table.lastPrivateColumn < table.columns.length){
                    //there is a part shared and a part private with a pointer
                    parseId = cursor.getString(cursor.getColumnIndex(Tables.POINTS));
                    //Log.d("parseTable", "table= " + tableName + " is private and shared");
                } else if (table.lastPrivateColumn < 0){
                    //only shared part
                    parseId = null;
                } else {
                    //only private part
                    parseId = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                    //Log.d("parseTable", "table= " + tableName + " is only private");
                }
                boolean needUpload = true;
                for(String objectToUploadID : objectsToUpload.parseIDs){
                    if(parseId != null) {
                        if (parseId.equals(objectToUploadID)) {
                            needUpload = false;
                        }
                    }
                }
                if(needUpload) {
                    String idObjectThatNeedACL = cursor.getString(cursor.getColumnIndex(Tables.PARSE_ID_NAME));
                    ParseObject parseObject = getParseObjectInOrderToUpdateACL(cursor, tableName, idObjectThatNeedACL);
                    Log.d("updateACL", "adding object to upload " + parseObject.getClassName() + " for user " + peopleID);
                    objectsToUpload.addObject(parseObject, cursor.getLong(cursor.getColumnIndex(Tables.ID)));
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private ParseObject createParseObjectFromCursor(Cursor cursor, Tables table, String parseID, boolean isPrivatePart, ParseObject publicPart){
        //Initialize table
        String tableName = table.tableName;
        String[] columns = table.columns;
        String[] origin = table.origin;
        String[] types = table.getTypes();

        //Initialize other things
        ParseObject parseObject;
        String parseClass;
        int initialIndex;
        int finalIndex;

        if(isPrivatePart){
            parseClass = tableName + PRIVATE;
            initialIndex = 0;
            finalIndex = table.lastPrivateColumn;
            Log.d("createParseObjectFromCursor", "private object className= " + parseClass + ", initialIndex= " + initialIndex + ", finalIndex= " + finalIndex);
        } else {
            parseClass = tableName;
            initialIndex = Math.max(table.lastPrivateColumn, 0); //don't allow -1, minimum value 0
            finalIndex = columns.length;
            Log.d("createParseObjectFromCursor", "public object className= " + parseClass + ", initialIndex= " + initialIndex + ", finalIndex= " + finalIndex);
        }
        Log.d("createParseObjectFromCursor", "working with= " + tableName + ", private?= " + isPrivatePart);
        if(parseID != null){
            parseObject = ParseObject.createWithoutData(parseClass, parseID);
            Log.e("createParseObjectFromCursor", "intentant update parseID= " + parseID);
        } else {
            Log.e("createParseObjectFromCursor", "no hi ha parseID");
            parseObject = new ParseObject(parseClass);
        }

        //add values
        for(int i = initialIndex; i < finalIndex; i++)
        {
            if(columns[i].equals(Tables.POINTS)){
                parseObject.put(columns[i], publicPart);
            } else {
                //Reading all columns
                int index = cursor.getColumnIndex(columns[i]);
                //Check if it's a foreign key
                if (origin[i] == null) {
                    //Normal value, add to object
                    if (index >= 0) {
                        if (types[i] == Tables.TYPE_DOUBLE) {
                            parseObject.put(columns[i], Math.abs(cursor.getDouble(index)));
                        } else if (types[i] == Tables.TYPE_INT) {
                            parseObject.put(columns[i], cursor.getInt(index));
                        } else {
                            if (cursor.getString(index) != null) {
                                parseObject.put(columns[i], cursor.getString(index));
                            }
                        }
                    } else {
                        Log.d("createParseObjectFromCursor", "no existeix la columna");
                    }
                } else {
                    //It is a foreign key
                    int parseIndex = cursor.getColumnIndex(columns[i] + ParseQueries.PARSE);
                    String parseForeignID = cursor.getString(parseIndex);

                    //special treatment for transaction_people
                    String columnToStore = columns[i];
                    String columnMyId;
                    if ((tableName == Tables.TABLENAME_TRANSACTIONS_PEOPLE) && (columns[i] == Tables.PEOPLE_ID)) {
                        double amount = cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT));
                        if (amount > 0) {
                            columnToStore = WHO_SPENT_ID;
                            columnMyId = WHO_PAID_ID;
                        } else {
                            columnToStore = WHO_PAID_ID;
                            columnMyId = WHO_SPENT_ID;
                        }
                        String myParseId = ParseAdapter.getMyParseId(mContext);
                        //add my id
                        if (myParseId != null && myParseId.length() > 0) {
                            parseObject.put(columnMyId,
                                    ParseObject.createWithoutData(Tables.TABLENAME_PEOPLE, myParseId));
                        } else {
                            long myId = ParseAdapter.getMyId(mContext);
                            for (int j = 0; j < objectsToUpload._ids.size(); j++) {
                                //find the object
                                if (objectsToUpload.type.get(j).equals(Tables.TABLENAME_PEOPLE)) {
                                    if (objectsToUpload._ids.get(j) == myId) {
                                        //add the object here
                                        parseObject.put(columnMyId, objectsToUpload.parseObjects.get(j));
                                    }
                                }
                            }
                        }
                    }

                    if (parseForeignID != null && parseForeignID.length() > 0) {
                        parseObject.put(columnToStore,
                                ParseObject.createWithoutData(origin[i], parseForeignID));
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
        if(isPrivatePart){
            parseObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
        } else {
            if (!table.acl.equals(Tables.ACL_PUBLIC)) {
                parseObject.setACL(getParseACLFromSQLite(table, cursor));
            }
        }
        return parseObject;
    }

    private ParseObject getParseObjectInOrderToUpdateACL(Cursor cursor, String tableName, String idObjectThatNeedACL){
        ParseObject parseObject;
        Log.d("getParseObjectInOrderToUpdateACL", "getting PO to update ACL for table= " + tableName + ", for object with id= " + idObjectThatNeedACL);
        if(idObjectThatNeedACL != null){
            String parseClassName  = tableName;

            parseObject = ParseObject.createWithoutData(parseClassName, idObjectThatNeedACL);
            Log.e("getParseObjectInOrderToUpdateACL", "created ParseObject with class name= " + parseClassName);

            if(!(new Tables(tableName)).acl.equals(Tables.ACL_PUBLIC)) {
                parseObject.setACL(getParseACLFromSQLite(new Tables(tableName), cursor));
            }
            return parseObject;
        } else {
            Log.e("getParseObjectInOrderToUpdateACL", "NOT POSSIBLE CASE");
            return null;
        }
    }

    private ParseACL getParseACLFromSQLite(Tables table, Cursor cursor){
        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        switch (table.acl){
            case Tables.ACL_INDIVIDUAL:
                Log.d("getParseACLFromSQLite", "ACL_INDIVIDUAL doing nothing");
                break;
            case Tables.ACL_ONE_PERSON:
                String personID = cursor.getString(cursor.getColumnIndex(Tables.USER_ID + ParseQueries.PARSE));
                if(personID != null){
                    parseACL.setReadAccess(personID, true);
                    parseACL.setWriteAccess(personID, true);
                }
                Log.d("getParseACLFromSQLite", "ACL_ONE_PERSON adding ACL to= " + personID);
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
                        Log.d("getParseACLFromSQLite", "granting group access to person= " + eachPerson);
                        if (eachPerson != null) {
                            parseACL.setReadAccess(eachPerson, true);
                            parseACL.setWriteAccess(eachPerson, true);
                        }
                    }
                }
                Log.d("getParseACLFromSQLite", "ACL_GROUP adding ACL to people in group= " + groupID);
                break;
            default:
                Log.e("getParseACLFromSQLite", "case null, error");
                parseACL = new ParseACL();
                break;
        }
        return parseACL;
    }

    private void addToObjectsToUpload(long _id, String parseID, ParseObject parseObject){
        //check if it's been downloaded
        if(!idsDownloaded.contains(parseID)){
            Log.e("addToObjectsToUpload", "object= " + parseID + " needs upload");

            //check if it was downloaded in a previous iteration
            if(previousUploaded.parseIDs.contains(parseID)){
                Log.d("addToObjectsToUpload", "has been updated previously id= " + _id);
                for(int k = 0; k < previousUploaded.parseIDs.size(); k++){
                    Log.d("addToObjectsToUpload", "checking if it's been previously uploaded with k= " + k);
                    //check if there are changes from the previous upload

                    Log.d("addToObjectsToUpload", "time 1= " + previousUploaded.parseObjects.get(k).getUpdatedAt().getTime());
                    Log.d("addToObjectsToUpload", "time 2=" + parseObject.getUpdatedAt().getTime());

                    if( (previousUploaded.parseIDs.get(k).equals(parseID)) &&
                            (previousUploaded.parseObjects.get(k).getUpdatedAt().getTime() < parseObject.getUpdatedAt().getTime()) ){
                        Log.d("addToObjectsToUpload", "has change de date");
                        objectsToUpload.addObject(parseObject, _id);
                    }
                }
            } else {
                Log.d("addToObjectsToUpload", "Not uploaded previously");
                objectsToUpload.addObject(parseObject, _id);
            }

        } else {
            Log.e("addToObjectsToUpload", "no need to upload= " + parseID);
        }
    }

    private void updateEntrySQL(ParseObject parseObject, long _id){

        String parseObjectName = parseObject.getClassName();

        if(!parseObjectName.contains(PRIVATE)){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Tables.LAST_UPDATE, startSync.getTime() );
            contentValues.put(Tables.PARSE_ID_NAME, parseObject.getObjectId());
            ParseAdapter.updateWithId(mContext, contentValues, parseObjectName, _id);
        }

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

        public void addObject(ParseObject parseObject, long id){
            Log.d("addObject", "adding object to upload with class= "  + parseObject.getClassName() + ", id= " + id);
            parseObjects.add(parseObject);
            _ids.add(id);
            type.add(parseObject.getClassName());
            parseIDs.add(parseObject.getObjectId());
        }
    }

}
