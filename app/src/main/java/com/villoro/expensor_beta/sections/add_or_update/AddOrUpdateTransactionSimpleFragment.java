package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.adapters.CategoryRadioAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogDatePicker;
import com.villoro.expensor_beta.sections.showList.ShowListActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class AddOrUpdateTransactionSimpleFragment extends Fragment implements DialogDatePicker.CommDatePicker, AddOrUpdateInterface{

    Context context;
    long currentID;
    Button b_date;

    EditText e_amount, e_comments;

    ListView lv_categories;

    ImageView iv_categories;

    CategoryRadioAdapter categoryRadioAdapter;
    DialogDatePicker dialogDate;

    Cursor cursorCategories;
    int[] date;

    String comments;
    double amount;

    public AddOrUpdateTransactionSimpleFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_expense, container, false);

        bindButtonDate(rv);
        bindIVCategories(rv);

        e_comments = (EditText) rv.findViewById(R.id.et_comments);
        e_amount = (EditText) rv.findViewById(R.id.et_amount);
        lv_categories = (ListView) rv.findViewById(R.id.lv_categories);

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    private void bindButtonDate(View rv)
    {
        date = Utility.getDate();

        b_date = (Button) rv.findViewById(R.id.b_date);
        b_date.setText(Utility.getFancyDate(date));

        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogDate.setPreviousDate(date);
                dialogDate.show(getFragmentManager(), "datePicker");
            }
        });

        dialogDate = new DialogDatePicker();
        dialogDate.setCommunicator(this);
    }

    private void setCategories(){
        cursorCategories = context.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI, null, null, null, null);
        categoryRadioAdapter = new CategoryRadioAdapter(context, cursorCategories, 0);
        lv_categories.setAdapter(categoryRadioAdapter);

        Utility.setListViewHeightBasedOnChildren(lv_categories);
        lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryRadioAdapter.setPositionSelected(position, id);
            }
        });
    }

    private void bindIVCategories(View rv)
    {
        iv_categories = (ImageView) rv.findViewById(R.id.iv_edit_categories);
        iv_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ShowListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setDate(int year, int month, int day) {
        date[0] = year;
        date[1] = month;
        date[2] = day;

        b_date.setText(Utility.getFancyDate(date));
    }



    //Methods of AddOrUpdateInterface
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
    public void add() {
        comments = e_comments.getText().toString().trim();
        amount = Double.parseDouble(Utility.formatDoubleToSQLite(e_amount.getText().toString().trim()));

        String from = "";

        long categoryID = categoryRadioAdapter.getIdSelected();

        //TODO check if values are possible
        ContentValues values = new ContentValues();
        values.put(Tables.DATE, Utility.completeDateToString(date));
        values.put(Tables.COMMENTS, comments);
        values.put(Tables.AMOUNT, amount);
        values.put(Tables.CATEGORY_ID, categoryID);
        if (currentID > 0){
            context.getContentResolver().update(ExpensorContract.ExpenseEntry.CONTENT_URI, values, Tables.ID + " = '" + currentID + "'", null);
        } else {
            context.getContentResolver().insert(ExpensorContract.ExpenseEntry.CONTENT_URI, values);
        }
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                ExpensorContract.ExpenseEntry.CONTENT_URI, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        Log.d("TransactionSimpleFragment", "cursorCategories count= " + tempCursor.getCount() + ", columns= " + tempCursor.getColumnCount());

        //TODO b_date.setText();
        int categoryID = tempCursor.getInt(tempCursor.getColumnIndex(Tables.CATEGORY_ID));
        //TODO sp_categories.setSelection(categoryID);
        e_amount.setText("" + tempCursor.getDouble(tempCursor.getColumnIndex(Tables.AMOUNT)));
        e_comments.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.COMMENTS)));
    }

    @Override
    public void delete() {
        Log.d("TransactionSimpleFragment", "trying to delete ");
        context.getContentResolver().delete(ExpensorContract.ExpenseEntry.CONTENT_URI, Tables.ID + " = '" + currentID + "'", null);
    }
}
