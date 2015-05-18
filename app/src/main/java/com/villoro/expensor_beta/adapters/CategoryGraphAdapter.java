package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 11/05/2015.
 */
public class CategoryGraphAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    double maxWidth, higherValue;

    public CategoryGraphAdapter(Context context, Cursor c, int flags, double maxWidth, double higherValue) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.maxWidth = maxWidth;
        this.higherValue = higherValue;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_category_graph, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv_name = (TextView) view.findViewById(R.id.row_grapgh_categories_name);
        View line = view.findViewById(R.id.row_graph_categories_line);

        double amount = cursor.getDouble(cursor.getColumnIndex(Tables.SUM_AMOUNT));

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)) +
        " " + UtilitiesNumbers.getFancyDouble(amount));
        tv_name.setTextColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));

        line.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));
        setWidth(line, amount / higherValue);

    }

    private void setWidth(View view, double percentage){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) Math.round(maxWidth * percentage);

        view.setLayoutParams(params);
    }

}
