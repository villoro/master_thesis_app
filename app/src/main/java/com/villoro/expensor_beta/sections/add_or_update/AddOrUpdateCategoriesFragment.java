package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arnau on 09/05/2015.
 */
public class AddOrUpdateCategoriesFragment extends Fragment implements AddOrUpdateInterface{

    Context context;
    long currentID;
    EditText et_name;
    Spinner sp_color;
    List<Integer> colors;

    String typeCategory;
    Uri uriCategories;

    String name;
    int color;

    public AddOrUpdateCategoriesFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        Bundle bundle = this.getArguments();
        typeCategory = bundle.getString(Tables.TYPE);
        if(typeCategory.equals(Tables.TYPE_EXPENSE)){
            uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI;
        } else {
            uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_INCOME_URI;
        }
        Log.e("", "type= " + typeCategory);

        int[] auxColors = getResources().getIntArray(R.array.categories_colors);
        colors = new ArrayList<>();
        for (int color: auxColors) {
            colors.add(color);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setSpinner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_categories, container, false);

        sp_color = (Spinner) rv.findViewById(R.id.sp_color);
        setSpinner();
        et_name = (EditText) rv.findViewById(R.id.et_categories_name);

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    @Override
    public void add() {
        name = et_name.getText().toString().trim();
        if(name.length() > 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        }
        color = colors.get( sp_color.getSelectedItemPosition() );
        ContentValues values = new ContentValues();
        values.put(Tables.NAME, name);
        values.put(Tables.TYPE, typeCategory);
        values.put(Tables.COLOR, color);

        if (currentID > 0){
            context.getContentResolver().update(uriCategories, values, Tables.ID + " = '" + currentID + "'", null);
        } else {
            context.getContentResolver().insert(uriCategories, values);
        }
    }

    @Override
    public void initialize(long whichID) {
        if (whichID > 0)
        {
            this.currentID = whichID;
        }
        else
        {
            this.currentID = 0;
        }
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                ExpensorContract.GroupEntry.CONTENT_URI, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        et_name.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.NAME)));
        int actualColor = tempCursor.getInt(tempCursor.getColumnIndex(Tables.COLOR));
        sp_color.setSelection(colors.indexOf(actualColor));
    }

    @Override
    public void delete() {
        context.getContentResolver().delete(uriCategories, Tables.ID + " = '" + currentID + "'", null);
    }

    public void setSpinner(){

        ColorAdapter colorAdapter = new ColorAdapter(context, 0, colors);
        sp_color.setAdapter(colorAdapter);
    }

    private class ColorAdapter extends ArrayAdapter<Integer>{

        List<Integer> list;
        Context context;

        public ColorAdapter(Context context, int resource, List<Integer> objects) {
            super(context, resource, objects);
            this.context = context;
            list = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_color, parent, false);

            View color = rowView.findViewById(R.id.color);
            color.setBackgroundColor(list.get(position));

            return rowView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = getView(position, convertView, parent);

            return view;
        }

        @Override
        public Integer getItem(int position) {
            return list.get(position);
        }
    }

}
