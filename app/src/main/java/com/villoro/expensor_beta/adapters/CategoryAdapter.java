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
 * Created by Arnau on 09/05/2015.
 */
public class CategoryAdapter extends CursorAdapter{

    private LayoutInflater mInflater;

    public CategoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_categories, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_color = (TextView) view.findViewById(R.id.row_category_color);
        TextView tv_name = (TextView) view.findViewById(R.id.row_category_name);

        tv_color.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));

        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));
    }
}
