package com.villoro.expensor_beta.PLEM;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

/**
 * Created by Arnau on 18/05/2015.
 */
public class PLEM_Utilities {

    private final static String LAST_ADDED = "last_added_date";
    private final static String LAST_SOLUTION = "last_solution_found_date";

    public static void saveLastAdded(Context context){
        saveDate(context, LAST_ADDED);
    }

    public static void saveLastSolution(Context context){
        saveDate(context, LAST_SOLUTION);
    }

    private static void saveDate(Context context, String whichCase){
        Date date = new Date();
        SharedPreferences sharedPreferences = context.getSharedPreferences(whichCase,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putLong(whichCase, date.getTime());
        editor.commit();
    }

    public static boolean needsToBeSolved(Context context){
        Date lastAdded = readDate(context, LAST_ADDED);
        Date lastSolution = readDate(context, LAST_SOLUTION);

        return lastAdded.after(lastSolution);
    }

    private static Date readDate(Context context, String whichCase){
        SharedPreferences sharedPreferences = context.getSharedPreferences(whichCase,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(whichCase, 0);
        return new Date(time);
    }
}
