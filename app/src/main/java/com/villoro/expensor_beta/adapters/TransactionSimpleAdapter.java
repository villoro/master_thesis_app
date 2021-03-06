package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 09/05/2015.
 */
public class TransactionSimpleAdapter extends CursorAdapter{

    private LayoutInflater mInflater;

    public TransactionSimpleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_transaction_simple, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_category = (TextView) view.findViewById(R.id.tv_category);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_comments = (TextView) view.findViewById(R.id.tv_comments);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);

        String aux_date = cursor.getString(cursor.getColumnIndex(Tables.DATE));
        tv_date.setText(UtilitiesDates.getFancyDate(aux_date));

        tv_comments.setText(cursor.getString(cursor.getColumnIndex(Tables.COMMENTS)));
        tv_amount.setText(UtilitiesNumbers.getFancyDouble(cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT))));

        tv_category.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));
        tv_category.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)).substring(0,1));

    }
}
