package com.villoro.expensor_beta.PLEM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;

/**
 * Created by Arnau on 18/05/2015.
 */
public class AsyncTaskPLEM extends AsyncTask<Object, Void, Boolean> implements PLEM_Solver.CommPLEM_Solver{

    Context context;
    long groupID;
    long[] ids;
    double[] balances;

    @Override
    protected Boolean doInBackground(Object... params) {
        context = (Context) params[0];
        groupID = (long) params[1];

        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildFromGroupIdWithFourSubBalancesUri(groupID,
                        ExpensorContract.PeopleInGroupEntry.ONLY_BALANCE), null, null, null, null);

        if(cursor.moveToFirst()){
            ids = new long[cursor.getCount()];
            balances = new double[cursor.getCount()];
            do{
                ids[cursor.getPosition()] = cursor.getLong(cursor.getColumnIndex(Tables.PEOPLE_ID));
                balances[cursor.getPosition()] = cursor.getDouble(cursor.getColumnIndex(Tables.BALANCE));
            } while (cursor.moveToNext());

            PLEM_Solver plem_solver = new PLEM_Solver(ids, balances);
            plem_solver.setCommunicator(this);
            plem_solver.solve();
        }
        return false;
    }

    @Override
    public void saveSolution(ArrayList<Long> from, ArrayList<Long> to, ArrayList<Double> money) {
        for(int i = 0; i < from.size(); i++){
            ContentValues values = new ContentValues();

            values.put(Tables.GROUP_ID, groupID);
            values.put(Tables.FROM, from.get(i));
            values.put(Tables.TO, to.get(i));
            values.put(Tables.AMOUNT, money.get(i));

            context.getContentResolver().insert(ExpensorContract.HowToSettleEntry.CONTENT_URI, values);
        }
    }
}
