package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 18/05/2015.
 */
public class HowToSettleAdapter extends CursorAdapter{

    private LayoutInflater mInflater;

    public HowToSettleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_how_to_solve, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_from = (TextView) view.findViewById(R.id.tv_from);
        TextView tv_to = (TextView) view.findViewById(R.id.tv_to);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);

        tv_from.setText(cursor.getString(cursor.getColumnIndex(Tables.FROM)));
        tv_to.setText(cursor.getString(cursor.getColumnIndex(Tables.TO)));
        tv_amount.setText(UtilitiesNumbers.getFancyDouble(cursor.getDouble(cursor.getColumnIndex(Tables.AMOUNT))));
    }
}
