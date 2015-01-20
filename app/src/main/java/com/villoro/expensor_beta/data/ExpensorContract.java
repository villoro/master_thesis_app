package com.villoro.expensor_beta.data;

import android.content.ContentUris;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arnau on 19/01/2015.
 */
public class ExpensorContract {

    public static final String CONTENT_AUTHORITY = "com.villoro.expensor_beta";

    private static final String DIRECTORY = "vnd.android.cursor.dir/";
    private static final String ITEM = "vnd.android.cursor.item/";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";


    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }


    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class ExpenseEntry {
        private static final String tableName = Tables.TABLENAME_EXPENSE;

        public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY + "/" + tableName;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class IncomeEntry {
        private static final String tableName = Tables.TABLENAME_INCOME;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY + "/" + tableName;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CategoriesEntry {
        private static final String tableName = Tables.TABLENAME_CATEGORIES;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY + "/" + tableName;

        public static Uri buildCategoriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PeopleEntry {
        private static final String tableName = Tables.TABLENAME_PEOPLE;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(tableName).build();

        public static final String CONTENT_TYPE =
                DIRECTORY + CONTENT_AUTHORITY + "/" + tableName;
        public static final String CONTENT_ITEM_TYPE =
                ITEM + CONTENT_AUTHORITY + "/" + tableName;

        public static Uri buildExpenseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
