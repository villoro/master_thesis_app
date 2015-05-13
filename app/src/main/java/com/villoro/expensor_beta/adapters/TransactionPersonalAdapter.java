package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 13/05/2015.
 */
public class TransactionPersonalAdapter extends CursorAdapter{

    private LayoutInflater mInflater;

    public TransactionPersonalAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_trans_people, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_date = (TextView) view.findViewById(R.id.row_trans_people_date);
        TextView tv_comments = (TextView) view.findViewById(R.id.row_trans_people_comments);
        TextView tv_amount = (TextView) view.findViewById(R.id.row_trans_people_amount);

        tv_date.setText(cursor.getString(cursor.getColumnIndex(Tables.DATE)));
        tv_comments.setText(cursor.getString(cursor.getColumnIndex(Tables.COMMENTS)));
        tv_amount.setText(cursor.getString(cursor.getColumnIndex(Tables.AMOUNT)));
    }
}
