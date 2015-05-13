package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 08/05/2015.
 */
public class CategoryRadioAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    RadioButton[] radioButtons;
    int positionSelected;
    long idSelected;

    public CategoryRadioAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        radioButtons = new RadioButton[c.getCount()];

        positionSelected = 0;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_color = (TextView) view.findViewById(R.id.row_category_color);
        TextView tv_name = (TextView) view.findViewById(R.id.row_category_name);

        tv_color.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Tables.COLOR)));
        tv_name.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));

        radioButtons[cursor.getPosition()] = (RadioButton) view.findViewById(R.id.row_category_radio_button);
        if(cursor.getPosition() == positionSelected){
            idSelected = cursor.getLong(cursor.getColumnIndex(Tables.ID));
            radioButtons[positionSelected].setChecked(true);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_categories_radio, viewGroup, false);
    }

    public void setPositionSelected(int position, long id){
        if(positionSelected != -1) {
            radioButtons[positionSelected].setChecked(false);
        }
        positionSelected = position;
        radioButtons[positionSelected].setChecked(true);

        idSelected = id;
    }

    public long getIdSelected(){
        return idSelected;
    }
}
