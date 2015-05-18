package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 17/05/2015.
 */
public class BalancesInGroupAdapter extends CategoryAdapter {

    private LayoutInflater mInflater;
    int colorRed, colorGreen;

    public BalancesInGroupAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        colorRed = context.getResources().getColor(R.color.red_expense);
        colorGreen = context.getResources().getColor(R.color.green_income);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_balances_in_group, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

        TextView tv_paid = (TextView) view.findViewById(R.id.tv_paid);
        TextView tv_spent = (TextView) view.findViewById(R.id.tv_spent);
        TextView tv_received = (TextView) view.findViewById(R.id.tv_received);
        TextView tv_given = (TextView) view.findViewById(R.id.tv_given);

        TextView tv_balance = (TextView) view.findViewById(R.id.tv_balance);

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));

        double paid = cursor.getDouble(cursor.getColumnIndex(Tables.PAID));
        double spent = cursor.getDouble(cursor.getColumnIndex(Tables.SPENT));
        double received = cursor.getDouble(cursor.getColumnIndex(Tables.RECEIVED));
        double given = cursor.getDouble(cursor.getColumnIndex(Tables.GIVEN));

        double balance = UtilitiesNumbers.round(paid + given - spent - received, 2);

        tv_paid.setText("Paid: " + UtilitiesNumbers.getFancyDouble(paid));
        tv_spent.setText("Spent: " + UtilitiesNumbers.getFancyDouble(spent));
        tv_received.setText("Received: " + UtilitiesNumbers.getFancyDouble(received));
        tv_given.setText("Given: " + UtilitiesNumbers.getFancyDouble(given));

        tv_balance.setText(UtilitiesNumbers.getFancyDouble(balance));

        if(balance > 0) {
            tv_balance.setTextColor(colorGreen);
        } else if (balance < 0) {
            tv_balance.setTextColor(colorRed);
        }

    }
}
