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

    public static final Uri contentUri(String tableName){
        return BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();
    }

    public static final class ExpenseEntry {
        private static final String tableName = Tables.TABLENAME_TRANSACTION_SIMPLE;

        public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY_EXPENSOR + "/" + tableName;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CategoriesEntry {
        private static final String tableName = Tables.TABLENAME_CATEGORIES;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

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

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
