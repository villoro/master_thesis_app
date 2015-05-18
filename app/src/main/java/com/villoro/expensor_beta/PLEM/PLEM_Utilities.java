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
        Log.e("PLEM_Utilities", "saving last added");
        saveDate(context, LAST_ADDED);
    }

    public static void saveLastSolution(Context context){
        Log.e("PLEM_Utilities", "saving last solution");
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
        Log.e("PLEM_Utilities", "added= " + lastAdded.getTime() + ", solution= " + lastSolution.getTime());

        return lastAdded.after(lastSolution);
    }

    private static Date readDate(Context context, String whichCase){
        SharedPreferences sharedPreferences = context.getSharedPreferences(whichCase,
                Context.MODE_PRIVATE);
        Long time = sharedPreferences.getLong(whichCase, 0);
        return new Date(time);
    }
}
