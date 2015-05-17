package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 13/05/2015.
 */
public class PeopleWithBalanceAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    int colorRed, colorGreen;

    public PeopleWithBalanceAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        colorRed = context.getResources().getColor(R.color.red_expense);
        colorGreen = context.getResources().getColor(R.color.green_income);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_people_with_amount, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_name = (TextView) view.findViewById(R.id.row_name);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));
        double amount = cursor.getDouble(cursor.getColumnIndex(Tables.SUM_AMOUNT));
        tv_amount.setText(UtilitiesNumbers.getFancyDouble(amount));

        if(amount > 0) {
            tv_amount.setTextColor(colorGreen);
        } else {
            tv_amount.setTextColor(colorRed);
        }
    }
}
