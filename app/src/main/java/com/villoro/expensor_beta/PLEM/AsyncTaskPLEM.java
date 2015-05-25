package com.villoro.expensor_beta.PLEM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.sections.details.DetailsGroupSummaryFragment;

import java.util.ArrayList;

/**
 * Created by Arnau on 18/05/2015.
 */
public class AsyncTaskPLEM extends AsyncTask<Object, Void, Boolean> implements PLEM_Solver.CommPLEM_Solver{

    private DetailsGroupSummaryFragment.FragmentCallback fragmentCallback;

    Context context;
    long groupID;
    long[] ids;
    double[] balances;
    double totalAbsBalance;

    public AsyncTaskPLEM(DetailsGroupSummaryFragment.FragmentCallback callback){
        this.fragmentCallback = callback;
    }


    @Override
    protected Boolean doInBackground(Object... params) {
        context = (Context) params[0];
        groupID = (long) params[1];

        totalAbsBalance = 0;

        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildFromGroupIdWithBalancesFromCaseUri(groupID),
                null, null, null, null);

        if(cursor.moveToFirst()){
            ids = new long[cursor.getCount()];
            balances = new double[cursor.getCount()];
            do{
                ids[cursor.getPosition()] = cursor.getLong(cursor.getColumnIndex(Tables.ID));
                balances[cursor.getPosition()] = cursor.getDouble(cursor.getColumnIndex(Tables.PAID))
                        + cursor.getDouble(cursor.getColumnIndex(Tables.GIVEN))
                        - cursor.getDouble(cursor.getColumnIndex(Tables.SPENT))
                        - cursor.getDouble(cursor.getColumnIndex(Tables.RECEIVED));
                totalAbsBalance += Math.abs(balances[cursor.getPosition()]);
            } while (cursor.moveToNext());

            if(totalAbsBalance > UtilitiesNumbers.EPSILON) {
                PLEM_Solver plem_solver = new PLEM_Solver(ids, balances);
                plem_solver.setCommunicator(this);
                plem_solver.solve();
            } else {
                deletePreviousSolution();
            }
        }
        return false;
    }

    public void deletePreviousSolution(){
        context.getContentResolver().delete(ExpensorContract.HowToSettleEntry.buildFromGroupId(groupID), null, null);
    }

    @Override
    public void saveSolution(ArrayList<Long> from, ArrayList<Long> to, ArrayList<Double> money) {

        deletePreviousSolution();

        for(int i = 0; i < from.size(); i++){
            ContentValues values = new ContentValues();

            values.put(Tables.GROUP_ID, groupID);
            values.put(Tables.FROM, from.get(i));
            values.put(Tables.TO, to.get(i));
            values.put(Tables.AMOUNT, money.get(i));

            context.getContentResolver().insert(ExpensorContract.HowToSettleEntry.HOW_TO_SETTLE_URI, values);
        }
        PLEM_Utilities.saveLastSolution(context);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        fragmentCallback.onTaskDone();
    }
}
