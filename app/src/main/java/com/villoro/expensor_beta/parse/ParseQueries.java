package com.villoro.expensor_beta.parse;

import android.database.Cursor;
import android.util.Log;

import com.parse.Parse;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arnau on 10/02/2015.
 */
public class ParseQueries {

    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String GROUP_BY = " GROUP BY ";
    private static final String JOIN = " JOIN ";
    private static final String LEFT_JOIN = " LEFT OUTER JOIN ";
    private static final String ON = " ON ";
    private static final String EQUAL = " = ";
    private static final String WHERE = " WHERE ";
    private static final String WHERE_CLAUSE_NAME = Tables.NAME + " = ";
    private static final String GREATER_THAN = " > ";
    private static final String APOSTROPHE = "'";
    private static final String COMA = ", ";
    private static final String SECOND_WHERE = " =? and ";
    private static final String FIRST_WHERE = " =?";
    private static final String CLOSE = ";";
    private static final String PARENTHESIS_OPEN = "(";
    private static final String PARENTHESIS_CLOSE = ")";
    private static final String SUM = "SUM";
    private static final String AS = " AS ";
    private static final String AUX = "aux";
    private static final String AUX0 = "aux0";
    private static final String IS_NULL = " IS NULL ";

    public static final String PARSE = "_Parse";

    public static String queryParse(String tableName, long updatedAt, long peopleID){

        Tables table = new Tables(tableName);
        String query = "";
        int count = 0;
        ArrayList<String> columnsArrayList = new ArrayList<>(Arrays.asList(table.columns));
        columnsArrayList.add(Tables.PARSE_ID_NAME);
        columnsArrayList.add(Tables.LAST_UPDATE);

        if(table.acl.equals(Tables.ACL_GROUP)){
            boolean hasGroupID = false;
            for (String origin: table.origin){
                if(origin != null) {
                    if (origin.equals(Tables.TABLENAME_GROUPS)) {
                        hasGroupID = true;
                    }
                }
            }
            if(!hasGroupID){
                columnsArrayList.add(Tables.GROUP_ID + PARSE);
            }
        }

        for(int i=0; i < table.columns.length ; i++){
            if(table.origin[i] != null){
                String from;
                String[] arrayListToArray = new String[columnsArrayList.size()];
                arrayListToArray = columnsArrayList.toArray(arrayListToArray);

                //add if needed the column pointsTo
                boolean addContains = false;
                if(query.contains(Tables.POINTS + PARSE)){
                    addContains = true;
                }

                if(count == 0){

                    query = innerQuery(
                            tableName,
                            arrayListToArray,
                            table.origin[i],
                            tableName,
                            table.columns[i],
                            updatedAt, peopleID);
                } else {
                    query = innerQuery(
                            AUX + count,
                            arrayListToArray,
                            table.origin[i],
                            PARENTHESIS_OPEN + query + PARENTHESIS_CLOSE + AS + AUX + count,
                            table.columns[i],
                            updatedAt, peopleID);
                }

                //add pointsTo_Parse to columns
                if(query.contains(Tables.POINTS + PARSE) && addContains){
                    columnsArrayList.add(Tables.POINTS + PARSE);
                } else {
                    addContains = false;
                }

                //add the foreign key to the columns
                columnsArrayList.add(table.columns[i] + PARSE);
                count++;
            }
        }

        if(table.acl.equals(Tables.ACL_GROUP)) {
            boolean needACL = true;
            for(int j = 0; j < table.columns.length; j++){
                if(table.origin != null) {
                    if (table.origin.equals(Tables.TABLENAME_GROUPS)) {
                        needACL = false;
                    }
                }
            }
            if (needACL && query.length() > 0) {
                for (int i = 0; i < table.columns.length; i++) {
                    if (containsGroupOrPeople(table.origin[i], Tables.TABLENAME_GROUPS)) {
                        String replacedText = forcedInnerACL
                                (table.origin[i], Tables.TABLENAME_GROUPS, Tables.GROUP_ID);
                        query = query.replace(table.origin[i], AUX0);
                        query = query.replace(" " + AUX0 + " ", replacedText);
                        query = query.replace(tableName + "." + Tables.GROUP_ID + PARSE,
                                AUX0 + "." + Tables.GROUP_ID + PARSE);
                    }
                    break;
                }
            }
        }

        if(query.length() > 0) {
            //delete name "aux"
            return SELECT + "*" + FROM + "(" + query + ")" + AS + "finalTable";
        } else {
            //return a empty String
            return query;
        }
    }

    public static String innerQuery(String tableName, String[] columns, String secondTable, String from,
                                    String whichColumn, long updatedAt, long peopleID){
     StringBuilder sb = new StringBuilder();
        boolean firstInner = !from.contains(SELECT);
        sb.append(SELECT + tableName + "." + Tables.ID + COMA);
        for(String column : columns){
            sb.append(tableName + "." + column + COMA);
        }

        sb.append(secondTable + "." + Tables.PARSE_ID_NAME + AS + whichColumn + PARSE);
        if(secondTable.equals(Tables.TABLENAME_PEOPLE)){
            sb.append(COMA + secondTable + "." + Tables.POINTS + AS + Tables.POINTS + PARSE);
        }

        sb.append(FROM + from + JOIN + secondTable);
        sb.append(ON + tableName + "." + whichColumn + EQUAL + secondTable + "." + Tables.ID);

        if(firstInner && peopleID <= 0){
            sb.append(WHERE + tableName + "." +Tables.LAST_UPDATE + GREATER_THAN + updatedAt);
        } else if(peopleID > 0 && secondTable.equals(Tables.TABLENAME_PEOPLE)) {
            sb.append(WHERE + secondTable + "." + Tables.ID + EQUAL + "'" + peopleID + "'");
        }
        return sb.toString();
    }



    private static final String PEOPLE_PARSE = Tables.PEOPLE_ID + PARSE;
    private static final String GROUP_PARSE = Tables.GROUP_ID + PARSE;

    private static boolean containsGroupOrPeople(String innerTableName, String tableACL){
        if(innerTableName == null){
            return false;
        } else {
            Tables innerTable = new Tables(innerTableName);
            for(String origin : innerTable.origin){
                if(origin != null){
                    if(origin.equals(tableACL)){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static final String forcedInnerACL(String innerTable, String tableACL, String which){
        return (" " + PARENTHESIS_OPEN + SELECT + innerTable + "." + Tables.ID + COMA +
                innerTable + "." + Tables.PARSE_ID_NAME + COMA +
                tableACL + "." + Tables.PARSE_ID_NAME + AS + which + PARSE +
                FROM + innerTable +
                JOIN + tableACL +
                ON + innerTable + "." + which + EQUAL + tableACL + "." + Tables.ID + PARENTHESIS_CLOSE + " ") +
                AS + AUX0 + " ";
    }

    public static final String queryPeopleInGroup(String groupID){
        String peopleInGroup = Tables.TABLENAME_PEOPLE_IN_GROUP;
        String group = Tables.TABLENAME_GROUPS;
        String people = Tables.TABLENAME_PEOPLE;

        String query = SELECT + people + "." + Tables.POINTS +
                FROM + PARENTHESIS_OPEN +
                    SELECT + peopleInGroup + "." + Tables.PEOPLE_ID +
                    FROM + peopleInGroup + JOIN + group +
                    ON + peopleInGroup + "." + Tables.GROUP_ID + EQUAL + group + "." + Tables.ID +
                    WHERE + group + "." + Tables.PARSE_ID_NAME + EQUAL + "'" + groupID + "'" +
                    PARENTHESIS_CLOSE + AS + AUX +
                JOIN + people +
                ON + AUX + "." + Tables.PEOPLE_ID + EQUAL + people + "." + Tables.ID;
        return SELECT + "*" + FROM + "(" + query + ")" + AS + "finalTable";
    }
}
