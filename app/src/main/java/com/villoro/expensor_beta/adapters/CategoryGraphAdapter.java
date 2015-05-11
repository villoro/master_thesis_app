package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 11/05/2015.
 */
public class CategoryGraphAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    double maxWidth;

    public CategoryGraphAdapter(Context context, Cursor c, int flags, double maxWidth) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.maxWidth = maxWidth;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_category_graph, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_name = (TextView) view.findViewById(R.id.row_grapgh_categories_name);
        View line = view.findViewById(R.id.row_graph_categories_line);

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));
        tv_name.setTextColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));

        line.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));
    }


}
