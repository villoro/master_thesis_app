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
import com.villoro.expensor_beta.sections.DashboardFragmentSection;

/**
 * Created by Arnau on 11/05/2015.
 */
public class CategoryGraphAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    double maxWidth;

    View[] lines;
    double[] sum_amounts;
    double maxValue;

    public CategoryGraphAdapter(Context context, Cursor c, int flags, double maxWidth) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.maxWidth = maxWidth;
        lines = new View[c.getCount()];
        sum_amounts = new double[c.getCount()];
        maxValue = 0;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_category_graph, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_name = (TextView) view.findViewById(R.id.row_grapgh_categories_name);
        lines[cursor.getPosition()] = view.findViewById(R.id.row_graph_categories_line);

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));
        tv_name.setTextColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));

        int position = cursor.getPosition();

        lines[position].setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));

        sum_amounts[position] = cursor.getDouble(cursor.getColumnIndex(Tables.SUM_AMOUNT));

        if(sum_amounts[position] > maxValue){
            maxValue = sum_amounts[position];
        }
    }

}
