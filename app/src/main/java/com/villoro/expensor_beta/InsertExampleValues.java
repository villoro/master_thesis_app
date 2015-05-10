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
        insertSQL(categoryValues, ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI);

        ContentValues categoryValues2 = new ContentValues();
        categoryValues2.put(Tables.NAME, "Transport");
        categoryValues2.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        categoryValues2.put(Tables.COLOR, 37);
        insertSQL(categoryValues2, ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI);

        ContentValues categoryValues3 = new ContentValues();
        categoryValues3.put(Tables.NAME, "Salari");
        categoryValues3.put(Tables.TYPE, Tables.TYPE_INCOME);
        categoryValues3.put(Tables.COLOR, 317);
        insertSQL(categoryValues3, ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI);

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(Tables.DATE, "2015_02_10");
        expenseValues.put(Tables.AMOUNT, 22);
        expenseValues.put(Tables.COMMENTS, "hola");
        expenseValues.put(Tables.CATEGORY_ID, 1);
        insertSQL(expenseValues, ExpensorContract.ExpenseEntry.CONTENT_URI);

        ContentValues expenseValues2 = new ContentValues();
        expenseValues2.put(Tables.DATE, "2015_02_11");
        expenseValues2.put(Tables.AMOUNT, 100);
        expenseValues2.put(Tables.COMMENTS, "T10");
        expenseValues2.put(Tables.CATEGORY_ID, 2);
        insertSQL(expenseValues2, ExpensorContract.ExpenseEntry.CONTENT_URI);

        ContentValues incomeValues = new ContentValues();
        incomeValues.put(Tables.DATE, "2015_02_16");
        incomeValues.put(Tables.AMOUNT, 500);
        incomeValues.put(Tables.COMMENTS, "Gestkom");
        incomeValues.put(Tables.CATEGORY_ID, 3);
        insertSQL(incomeValues, ExpensorContract.IncomeEntry.CONTENT_URI);

        ContentValues peopleValues = new ContentValues();
        peopleValues.put(Tables.NAME, "Villoro");
        peopleValues.put(Tables.EMAIL, "villoro@villoro.com");
        insertSQL(peopleValues, ExpensorContract.PeopleEntry.CONTENT_URI);

        ContentValues peopleValues2 = new ContentValues();
        peopleValues2.put(Tables.NAME, "luke");
        peopleValues2.put(Tables.EMAIL, "soy@tu.padre");
        insertSQL(peopleValues2, ExpensorContract.PeopleEntry.CONTENT_URI);

        ContentValues groupValues = new ContentValues();
        groupValues.put(Tables.NAME, "los patatos");
        insertSQL(groupValues, ExpensorContract.GroupEntry.CONTENT_URI);

        ContentValues peopleInGroupValues = new ContentValues();
        peopleInGroupValues.put(Tables.GROUP_ID, 1);
        peopleInGroupValues.put(Tables.PEOPLE_ID, 2);
        insertSQL(peopleInGroupValues, ExpensorContract.PeopleInGroupEntry.CONTENT_URI);

        ContentValues transactionGroupValues = new ContentValues();
        transactionGroupValues.put(Tables.DATE, "2015_02_16");
        transactionGroupValues.put(Tables.GROUP_ID, 1);
        transactionGroupValues.put(Tables.AMOUNT, 10);
        transactionGroupValues.put(Tables.COMMENTS, "jojo");
        insertSQL(transactionGroupValues, ExpensorContract.TransactionGroupEntry.CONTENT_URI);

        ContentValues transactionPeopleValues = new ContentValues();
        transactionPeopleValues.put(Tables.DATE, "2015_02_16");
        transactionPeopleValues.put(Tables.AMOUNT, 21.21);
        transactionPeopleValues.put(Tables.COMMENTS, "cine");
        transactionPeopleValues.put(Tables.PEOPLE_ID, 2);
        insertSQL(transactionPeopleValues, ExpensorContract.TransactionPeopleEntry.CONTENT_URI);

        ContentValues transactionPeopleValues2 = new ContentValues();
        transactionPeopleValues2.put(Tables.DATE, "2015_02_16");
        transactionPeopleValues2.put(Tables.AMOUNT, -12);
        transactionPeopleValues2.put(Tables.COMMENTS, "T10");
        transactionPeopleValues2.put(Tables.PEOPLE_ID, 2);
        insertSQL(transactionPeopleValues2, ExpensorContract.TransactionPeopleEntry.CONTENT_URI);

        ContentValues whoPaidSpentValues = new ContentValues();
        whoPaidSpentValues.put(Tables.TRANSACTION_ID, 1);
        whoPaidSpentValues.put(Tables.PEOPLE_ID, 2);
        whoPaidSpentValues.put(Tables.SPENT, 10);
        whoPaidSpentValues.put(Tables.PAID, 0);
        insertSQL(whoPaidSpentValues, ExpensorContract.WhoPaidSpentEntry.CONTENT_URI);

        /* TODO
        ContentValues howToSettleValues = new ContentValues();
        howToSettleValues.put(Tables.GROUP_ID, 1);
        howToSettleValues.put(Tables.FROM);*/
    }

    public void insertSQL(ContentValues values, Uri uri){
        Log.d("", "Insertant cv= " + values.toString());
        mContext.getContentResolver().insert(uri, values);
    }

    public void query(){

    }
}