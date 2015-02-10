package com.villoro.expensor_beta.parse;

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
    private static final String IS_NULL = " IS NULL ";

    public static final String PARSE = "_Parse";

    public static String queryParse(String tableName){

        Tables table = new Tables(tableName);
        String query = "";
        int count = 0;
        ArrayList<String> columns = new ArrayList<String>(Arrays.asList(table.columns));

        for(int i=0; i < table.columns.length ; i++){
            if(table.origin[i] != null){
                String from;
                String[] arrayListToArray = new String[columns.size()];
                arrayListToArray = columns.toArray(arrayListToArray);

                if(count == 0){
                    query = innerQuery(
                            tableName,
                            arrayListToArray,
                            table.origin[i],
                            tableName,
                            table.columns[i]);
                } else {
                    query = innerQuery(
                            AUX + count,
                            arrayListToArray,
                            table.origin[i],
                            PARENTHESIS_OPEN + query + PARENTHESIS_CLOSE + AS + AUX + count,
                            table.columns[i]);
                }
                columns.add(table.columns[i] + PARSE);
                count++;
            }
        }

        return query;
    }

    private static String innerQuery(String tableName, String[] columns, String secondTable, String from, String whichColumn){
     StringBuilder sb = new StringBuilder();
        sb.append(SELECT + tableName + "." + Tables.ID + COMA);
        for(String column : columns){
            sb.append(tableName + "." + column + COMA);
        }
        sb.append(secondTable + "." + Tables.PARSE_ID_NAME + AS + whichColumn + PARSE);
        sb.append(FROM + from + JOIN + secondTable);
        sb.append(ON + tableName + "." + whichColumn + EQUAL + secondTable + "." + Tables.ID);
        return sb.toString();
    }

}
