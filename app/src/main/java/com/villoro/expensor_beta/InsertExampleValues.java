package com.villoro.expensor_beta;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 10/02/2015.
 */
public class InsertExampleValues {

    Context mContext;

    public InsertExampleValues(Context context){
        mContext = context;
    }

    public void insert(){
        ContentValues categoryValues = new ContentValues();
        categoryValues.put(Tables.NAME, "Food");
        categoryValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        categoryValues.put(Tables.COLOR, 2);
        Log.e("", "Insertant cv= " + categoryValues.toString());
        mContext.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, categoryValues);

        ContentValues categoryValues2 = new ContentValues();
        categoryValues2.put(Tables.NAME, "Transport");
        categoryValues2.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        categoryValues2.put(Tables.COLOR, 37);
        Log.e("", "Insertant cv= " + categoryValues2.toString());
        mContext.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, categoryValues2);

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(Tables.DATE, "2015_02_10");
        expenseValues.put(Tables.AMOUNT, 22);
        expenseValues.put(Tables.COMMENTS, "hola");
        expenseValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        expenseValues.put(Tables.CATEGORY_ID, 1);
        Log.e("", "Insertant cv= " + expenseValues.toString());
        mContext.getContentResolver().insert(ExpensorContract.ExpenseEntry.CONTENT_URI, expenseValues);

        ContentValues expenseValues2 = new ContentValues();
        expenseValues2.put(Tables.DATE, "2015_02_11");
        expenseValues2.put(Tables.AMOUNT, 100);
        expenseValues2.put(Tables.COMMENTS, "T10");
        expenseValues2.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        expenseValues2.put(Tables.CATEGORY_ID, 2);
        Log.e("", "Insertant cv= " + expenseValues2.toString());
        mContext.getContentResolver().insert(ExpensorContract.ExpenseEntry.CONTENT_URI, expenseValues2);

        //TODO add people, group, peopleInGroup...
    }

    public void query(){

    }
}
