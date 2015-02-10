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
        Uri uriCategories = mContext.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, categoryValues);

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(Tables.DATE, "2015_02_10");
        expenseValues.put(Tables.AMOUNT, 22);
        expenseValues.put(Tables.COMMENTS, "hola");
        expenseValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        expenseValues.put(Tables.CATEGORY_ID, 1);
        Log.e("", "Insertant cv= " + categoryValues.toString());
        Uri uriExpense = mContext.getContentResolver().insert(ExpensorContract.ExpenseEntry.CONTENT_URI, expenseValues);
    }

    public void query(){

    }
}
