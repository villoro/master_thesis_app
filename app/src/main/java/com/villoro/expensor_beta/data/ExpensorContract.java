package com.villoro.expensor_beta.data;

import android.content.ContentUris;
import android.net.Uri;

import java.util.Date;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorContract {

    public static final String CONTENT_AUTHORITY_EXPENSOR = "com.villoro.expensor_beta";


    private static final String DIRECTORY = "vnd.android.cursor.dir/";
    private static final String ITEM = "vnd.android.cursor.item/";

    // Use CONTENT_AUTHORITY_EXPENSOR to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_EXPENSOR);

    public static Date getDateUTC(){
        return new Date();
    }

    public static final class ExpenseEntry {
        private static final String tableName = Tables.TABLENAME_TRANSACTION_SIMPLE;
        private static final String type = Tables.TYPE_EXPENSE;

        public static final Uri EXPENSE_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(type).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName + "/" + type ;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName + "/" + type;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(EXPENSE_URI, id);
        }

        public static Uri buildExpenseUri(String year, String month){
            return EXPENSE_URI.buildUpon().appendPath(year).appendPath(month).build();
        }
    }

    public static final class TransactionSimple {
        public static String getTypeTransaction(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static int getYearFromUriAll(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(2) );
        }

        public static int getMonthFromUriAll(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(3) );
        }
    }

    public static final class IncomeEntry {
        private static final String tableName = Tables.TABLENAME_TRANSACTION_SIMPLE;
        private static final String type = Tables.TYPE_INCOME;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).appendPath(type).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName + "/" + type ;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName + "/" + type;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GraphEntry {
        public static final String GRAPH = "graph";
        public static final String ALL = "all";

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + GRAPH;

        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + GRAPH;

        public static final Uri INCOME_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(GRAPH).appendPath(Tables.TYPE_INCOME).build();
        public static final Uri EXPENSE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(GRAPH).appendPath(Tables.TYPE_EXPENSE).build();
        public static final Uri INCOME_ALL_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(GRAPH).appendPath(Tables.TYPE_INCOME).appendPath(ALL).build();
        public static final Uri EXPENSE_ALL_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(GRAPH).appendPath(Tables.TYPE_EXPENSE).appendPath(ALL).build();

        public static Uri buildIncomeGraphUri(String year, String month){
            return INCOME_URI.buildUpon().appendPath(year).appendPath(month).build();
        }
        public static Uri buildExpenseGraphUri(String year, String month){
            return EXPENSE_URI.buildUpon().appendPath(year).appendPath(month).build();
        }

        public static int getYearFromUri(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(2) );
        }
        public static int getMonthFromUri(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(3) );
        }

        public static Uri buildIncomeGraphAllUri(String year, String month){
            return INCOME_ALL_URI.buildUpon().appendPath(year).appendPath(month).build();
        }
        public static Uri buildExpenseGraphAllUri(String year, String month){
            return EXPENSE_ALL_URI.buildUpon().appendPath(year).appendPath(month).build();
        }

        public static int getYearFromUriAll(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(3) );
        }
        public static int getMonthFromUriAll(Uri uri){
            return Integer.parseInt( uri.getPathSegments().get(4) );
        }
    }

    public static final class CategoriesEntry {
        private static final String tableName = Tables.TABLENAME_CATEGORIES;

        private static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final Uri CATEGORIES_EXPENSE_URI =
                CONTENT_URI.buildUpon().appendPath(Tables.TYPE_EXPENSE).build();

        public static final Uri CATEGORIES_INCOME_URI =
                CONTENT_URI.buildUpon().appendPath(Tables.TYPE_INCOME).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildCategoriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PeopleEntry {
        private static final String tableName = Tables.TABLENAME_PEOPLE;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildPeopleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PeopleInGroupEntry {
        private static final String tableName = Tables.TABLENAME_PEOPLE_IN_GROUP;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildPeopleInGroupUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GroupEntry {
        private static final String tableName = Tables.TABLENAME_GROUPS;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildGroupUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TransactionPeopleEntry {
        private static final String tableName = Tables.TABLENAME_TRANSACTIONS_PEOPLE;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildTransactionPeopleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TransactionGroupEntry {
        private static final String tableName = Tables.TABLENAME_TRANSACTIONS_GROUP;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildTransactionGroupUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class WhoPaidSpentEntry {
        private static final String tableName = Tables.TABLENAME_WHO_PAID_SPENT;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildWhoPaidSpentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class HowToSettleEntry {
        private static final String tableName = Tables.TABLENAME_HOW_TO_SETTLE;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildHowToSettleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
