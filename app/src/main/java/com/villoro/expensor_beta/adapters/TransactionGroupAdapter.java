package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.ExpensorQueries;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 20/05/2015.
 */
public class TransactionGroupAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public TransactionGroupAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_transaction_group, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv_paid = (TextView) view.findViewById(R.id.tv_paid);
        TextView tv_spent = (TextView) view.findViewById(R.id.tv_spent);
        TextView tv_comments = (TextView) view.findViewById(R.id.tv_comments);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);

        String aux_date = cursor.getString(cursor.getColumnIndex(Tables.DATE));
        tv_date.setText(UtilitiesDates.getFancyDate(aux_date));
        tv_comments.setText(cursor.getString(cursor.getColumnIndex(Tables.COMMENTS)));
        tv_amount.setText(UtilitiesNumbers.getFancyDouble(cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT))));

        long transactionId = cursor.getLong(cursor.getColumnIndex(Tables.ID));
        long groupId = cursor.getLong(cursor.getColumnIndex(Tables.GROUP_ID));

        Cursor cursorPaid = context.getContentResolver().query(
                ExpensorContract.WhoPaidSpentEntry.buildUriFromTransactionId(transactionId, Tables.PAID),
                null, null, null, null);
        Cursor cursorSpent = context.getContentResolver().query(
                ExpensorContract.WhoPaidSpentEntry.buildUriFromTransactionId(transactionId, Tables.SPENT),
                null, null, null, null);
        Cursor cursorPIG = context.getContentResolver().query(
                ExpensorContract.PeopleInGroupEntry.buildUriFromGroupId(groupId), null, null, null, null);


        String whoPaid = context.getString(R.string.title_pay);
        String whoSpent = context.getString(R.string.title_spend);
        whoPaid += appendPeople(cursorPaid);

        if(cursorSpent.getCount() == cursorPIG.getCount()){
            whoSpent += context.getString(R.string.everybody);
        } else {
            whoSpent += appendPeople(cursorSpent);
        }
        tv_paid.setText(whoPaid);
        tv_spent.setText(whoSpent);
    }

    private String appendPeople(Cursor cursor){
        StringBuilder sb = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                sb.append(cursor.getString(cursor.getColumnIndex(Tables.NAME)) + ", ");
            } while (cursor.moveToNext());
        }

        String output = sb.toString();

        if(output.endsWith(", ")) {
            return output.substring(0, output.length() - 2);
        }
        return output;
    }
}
