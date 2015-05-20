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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.util.ArrayList;
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
    Button b_expense, b_income;

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
        Log.e("AddOrUpdateCategory", "type= " + typeCategory);

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

        b_expense = (Button) rv.findViewById(R.id.b_expense);
        b_income = (Button) rv.findViewById(R.id.b_income);
        setButtonExpense();
        setButtonIncome();

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    @Override
    public boolean add() {

        name = et_name.getText().toString().trim();
        if (name.length() > 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        }
        color = colors.get(sp_color.getSelectedItemPosition());

        if(valuesAreCorrect()) {
            ContentValues values = new ContentValues();
            values.put(Tables.NAME, name);
            values.put(Tables.COLOR, color);

            Log.d("addCategory", "type= " + typeCategory);

            if (currentID > 0) {
                context.getContentResolver().update(uriCategories, values, Tables.ID + " = '" + currentID + "'", null);
            } else {
                context.getContentResolver().insert(uriCategories, values);
            }
            return true;
        } else {
            return false;
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
        Log.d("AddCategories", "id= " + whichID);
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                uriCategories, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        Log.d("AddCategories", "cursor count= " + tempCursor.getCount());

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

    public void setButtonExpense(){
        b_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeCategory.equals(Tables.TYPE_EXPENSE)){
                    typeCategory = Tables.TYPE_EXPENSE;
                    uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI;
                    Log.e("AddOrUpdateCategory", "typeTransaction= " + typeCategory);
                }
            }
        });
    }

    public void setButtonIncome(){
        b_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeCategory.equals(Tables.TYPE_INCOME)){
                    typeCategory = Tables.TYPE_INCOME;
                    uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_INCOME_URI;
                    Log.e("AddOrUpdateCategory", "typeTransaction= " + typeCategory);
                }
            }
        });
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

    @Override
    public boolean valuesAreCorrect() {
        Log.d("AddCategories", "name length= " + name.length());
        if(name.length() == 0){
            et_name.setError(getString(R.string.error_name));
            return false;
        } else {
            return true;
        }
    }
}
