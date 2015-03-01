package com.villoro.expensor_beta.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogDatePicker;

/**
 * Created by Arnau on 01/03/2015.
 */
public class TransactionSimpleFragment extends Fragment implements DialogDatePicker.CommDatePicker, AddOrUpdateInterface{

    Context context;
    long currentID;
    Button b_date;

    EditText e_amount, e_comments;
    Spinner sp_categories;

    ImageView iv_categories;

    SimpleCursorAdapter sp_adapter;
    DialogDatePicker dialogDate;

    Cursor cursorCategories;
    int[] to;

    String date, comments;
    double amount;

    public TransactionSimpleFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSpinner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_expense, container, false);

        bindButtonDate(rv);
        bindIVCategories(rv);

        e_comments = (EditText) rv.findViewById(R.id.et_comments);
        e_amount = (EditText) rv.findViewById(R.id.et_amount);
        sp_categories = (Spinner) rv.findViewById(R.id.sp_categories);
        setSpinner();

        if (currentID >0)
        {
            setValues();
        }
        return rv;
    }

    private void bindButtonDate(View rv)
    {
        b_date = (Button) rv.findViewById(R.id.b_date);
        b_date.setText(Utility.dateToString(Utility.getDate()));
        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogDate.setPreviusDate((String) b_date.getText());
                dialogDate.show(getFragmentManager(), "datePicker");
            }
        });

        dialogDate = new DialogDatePicker();
        dialogDate.setCommunicator(this);
    }

    private void setSpinner(){
        to = new int[]{android.R.id.text1};
        cursorCategories = context.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI, null, null, null, null);
        sp_adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, cursorCategories, new String[]{Tables.NAME},
                to, 0);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_categories.setAdapter(sp_adapter);
    }

    private void bindIVCategories(View rv)
    {
        iv_categories = (ImageView) rv.findViewById(R.id.iv_edit_categories);
        iv_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Work on that
                /*Intent intent = new Intent(getActivity(), ShowList.class);
                intent.putExtra(Utils.WHICH_LIST, Utils.CASE_CATEGORIES);
                startActivity(intent); */
            }
        });
    }

    @Override
    public void setDate(int year, int month, int day) {
        b_date.setText(Utility.dateToString(new int[]{day, month, year}));
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
        date = b_date.getText().toString().trim();
        comments = e_comments.getText().toString().trim();
        amount = Double.parseDouble(Utility.formatDoubleToSQLite(e_amount.getText().toString().trim()));

        String from = "";

        long categoryID = sp_categories.getSelectedItemId();

        //TODO check if values are possible
        ContentValues values = new ContentValues();
        //TODO values.put(Tables.DATE, date);
        values.put(Tables.COMMENTS, comments);
        values.put(Tables.AMOUNT, amount);
        values.put(Tables.CATEGORY_ID, categoryID);
        context.getContentResolver().insert(
                ExpensorContract.ExpenseEntry.CONTENT_URI, values
        );
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                ExpensorContract.ExpenseEntry.CONTENT_URI, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        Log.d("TransactionSimpleFragment", "cursorCategories count= " + tempCursor.getCount() + ", columns= " + tempCursor.getColumnCount());

        //TODO b_date.setText();
        int categoryID = tempCursor.getInt(tempCursor.getColumnIndex(Tables.CATEGORY_ID));
        sp_categories.setSelection(categoryID);
        e_amount.setText("" + tempCursor.getDouble(tempCursor.getColumnIndex(Tables.AMOUNT)));
        e_comments.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.COMMENTS)));
    }

    @Override
    public void delete() {
        Log.d("TransactionSimpleFragment", "trying to delete ");
        context.getContentResolver().delete(ExpensorContract.ExpenseEntry.CONTENT_URI, Tables.ID + " = '" + currentID + "'", null);
    }
}
