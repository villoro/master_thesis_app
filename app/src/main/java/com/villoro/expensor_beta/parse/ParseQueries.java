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

    public static String queryParse(String tableName, long updatedAt){

        Tables table = new Tables(tableName);
        String query = "";
        int count = 0;
        ArrayList<String> columnsArrayList = new ArrayList<String>(Arrays.asList(table.columns));

        for(int i=0; i < table.columns.length ; i++){
            if(table.origin[i] != null){
                String from;
                String[] arrayListToArray = new String[columnsArrayList.size()];
                arrayListToArray = columnsArrayList.toArray(arrayListToArray);

                if(count == 0){
                    query = innerQuery(
                            tableName,
                            arrayListToArray,
                            table.origin[i],
                            tableName,
                            table.columns[i],
                            updatedAt);
                } else {
                    query = innerQuery(
                            AUX + count,
                            arrayListToArray,
                            table.origin[i],
                            PARENTHESIS_OPEN + query + PARENTHESIS_CLOSE + AS + AUX + count,
                            table.columns[i],
                            updatedAt);
                }
                columnsArrayList.add(table.columns[i] + PARSE);
                count++;
            }
        }

        switch (table.acl){
            case Tables.ACL_ONE_PERSON:
                if(! query.contains(Tables.PEOPLE_ID + PARSE) && query.length() > 0){
                    for(int i = 0; i < table.columns.length ; i++){
                        if(containsGroupOrPeople(table.origin[i], Tables.TABLENAME_PEOPLE)){
                            String replacedText = forcedInnerACL
                                    (table.origin[i], Tables.TABLENAME_PEOPLE, Tables.PEOPLE_ID);
                            query = query.replace(table.origin[i], AUX0);
                            query = query.replace(" " + AUX0 + " ", replacedText);
                        } break;
                    }
                }
                break;
            case Tables.ACL_GROUP:
                if(! query.contains(Tables.GROUP_ID + PARSE) && query.length() > 0){
                    for(int i = 0; i < table.columns.length; i++){
                        if(containsGroupOrPeople(table.origin[i], Tables.TABLENAME_GROUPS)){
                            String replacedText = forcedInnerACL
                                    (table.origin[i], Tables.TABLENAME_GROUPS, Tables.GROUP_ID);
                            query = query.replace(table.origin[i], AUX0);
                            query = query.replace(" " + AUX0 + " ", replacedText);
                        } break;
                    }
                }
                break;
            //other cases no need to do nothing
        }

        return query;
    }

    public static String innerQuery(String tableName, String[] columns, String secondTable, String from, String whichColumn, long updatedAt){
     StringBuilder sb = new StringBuilder();
        boolean firstInner = !from.contains(SELECT);
        sb.append(SELECT + tableName + "." + Tables.ID + COMA);
        for(String column : columns){
            sb.append(tableName + "." + column + COMA);
        }

        if(firstInner){
            sb.append(tableName + "." + Tables.LAST_UPDATE + COMA);
            sb.append(tableName + "." + Tables.PARSE_ID_NAME + COMA);
        }

        sb.append(secondTable + "." + Tables.PARSE_ID_NAME + AS + whichColumn + PARSE);
        sb.append(FROM + from + JOIN + secondTable);
        sb.append(ON + tableName + "." + whichColumn + EQUAL + secondTable + "." + Tables.ID);

        if(firstInner){
            sb.append(WHERE + tableName + "." +Tables.LAST_UPDATE + GREATER_THAN + updatedAt);
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

        return SELECT + people + "." + Tables.POINTS +
                FROM + PARENTHESIS_OPEN +
                    SELECT + peopleInGroup + "." + Tables.PEOPLE_ID +
                    FROM + peopleInGroup + JOIN + group +
                    ON + peopleInGroup + "." + Tables.GROUP_ID + EQUAL + group + "." + Tables.ID +
                    WHERE + peopleInGroup + "." + Tables.GROUP_ID + EQUAL + groupID +
                    PARENTHESIS_CLOSE + AS + AUX +
                JOIN + people +
                ON + AUX + "." + Tables.PEOPLE_ID + EQUAL + people + "." + Tables.ID;
    }
}
