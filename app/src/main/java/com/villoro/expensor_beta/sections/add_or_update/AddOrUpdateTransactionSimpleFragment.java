package com.villoro.expensor_beta.sections.add_or_update;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.CategoryRadioAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogDatePicker;
import com.villoro.expensor_beta.sections.showList.ShowCategoriesActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class AddOrUpdateTransactionSimpleFragment extends Fragment implements DialogDatePicker.CommDatePicker, AddOrUpdateInterface{

    Context context;
    long currentID;
    Button b_date;

    TextView header_categories;

    EditText e_amount, e_comments;
    ListView lv_categories;
    ImageView iv_categories;

    CategoryRadioAdapter categoryRadioAdapter;
    DialogDatePicker dialogDate;

    Cursor cursorCategories;
    int[] date;

    String comments, stringAmount;
    double amount;
    long categoryID;

    String typeTransaction;
    Button b_expense, b_income;
    Uri uriCategories, uriTransaction;

    ColorChangerInterface comm;
    int colorGreen, colorRed, actualColor;
    String titleExpense, titleIncome, actualTitle;

    public AddOrUpdateTransactionSimpleFragment(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        colorGreen = context.getResources().getColor(R.color.green_income);
        colorRed = context.getResources().getColor(R.color.red_expense);
        titleIncome = context.getResources().getString(R.string.ab_add_income);
        titleExpense = context.getResources().getString(R.string.ab_add_expense);

        Bundle bundle = this.getArguments();
        typeTransaction = bundle.getString(Tables.TYPE);

        Log.e("", "type= " + typeTransaction);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("AddOrUpadateTransactionSimpleFragment", "setCategories onResume");
        setCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_transaction_simple, container, false);

        bindButtonDate(rv);
        bindIVCategories(rv);

        e_comments = (EditText) rv.findViewById(R.id.et_comments);
        e_amount = (EditText) rv.findViewById(R.id.et_amount);
        lv_categories = (ListView) rv.findViewById(R.id.lv_categories);

        header_categories = (TextView) rv.findViewById(R.id.header_categories);

        b_expense = (Button) rv.findViewById(R.id.b_expense);
        b_income = (Button) rv.findViewById(R.id.b_income);
        setButtonExpense();
        setButtonIncome();

        return rv;
    }

    private void bindButtonDate(View rv)
    {
        date = UtilitiesDates.getDate();

        b_date = (Button) rv.findViewById(R.id.b_date);
        b_date.setText(UtilitiesDates.getFancyDate(date));

        dialogDate = new DialogDatePicker();
        dialogDate.setCommunicator(this);

        b_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate.setPreviousDate(date);
                dialogDate.show(getFragmentManager(), "datePicker");
            }
        });
    }


    private void setCategories(){
        if(typeTransaction.equals(Tables.TYPE_EXPENSE)){
            uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI;
            uriTransaction = ExpensorContract.ExpenseEntry.EXPENSE_URI;
            actualColor = colorRed;
            actualTitle = titleExpense;
        } else {
            uriCategories = ExpensorContract.CategoriesEntry.CATEGORIES_INCOME_URI;
            uriTransaction = ExpensorContract.IncomeEntry.INCOME_URI;
            actualColor = colorGreen;
            actualTitle = titleIncome;
        }

        cursorCategories = context.getContentResolver().query(
                uriCategories, null, null, null, null);
        categoryRadioAdapter = new CategoryRadioAdapter(context, cursorCategories, 0);
        lv_categories.setAdapter(categoryRadioAdapter);

        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_categories);
        lv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryRadioAdapter.setPositionSelected(position, id);
            }
        });

        if (currentID >0)
        {
            setValues();
        }

        comm.restoreActionBar(actualTitle, actualColor);
    }

    private void bindIVCategories(View rv)
    {
        iv_categories = (ImageView) rv.findViewById(R.id.iv_edit_categories);
        iv_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategories();
            }
        });
    }

    @Override
    public void setDate(int year, int month, int day) {
        date[0] = year;
        date[1] = month;
        date[2] = day;

        b_date.setText(UtilitiesDates.getFancyDate(date));
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
    public boolean add() {

        comments = e_comments.getText().toString().trim();
        stringAmount = e_amount.getText().toString().trim();
        if(stringAmount != null && stringAmount.length() > 0) {
            amount = Double.parseDouble(stringAmount);
        }

        //TODO String from = "";

        long categoryID = categoryRadioAdapter.getIdSelected();

        if(valuesAreCorrect()) {
            ContentValues values = new ContentValues();
            values.put(Tables.DATE, UtilitiesDates.completeDateToString(date));
            values.put(Tables.COMMENTS, comments);
            values.put(Tables.AMOUNT, amount);
            values.put(Tables.CATEGORY_ID, categoryID);

            Log.d("", "current id= " + currentID);
            if (currentID > 0) {
                context.getContentResolver().update(uriTransaction, values, Tables.ID + " = '" + currentID + "'", null);
            } else {
                context.getContentResolver().insert(uriTransaction, values);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValues() {
        Cursor tempCursor = context.getContentResolver().query(
                uriTransaction, null, Tables.ID + " = '" + currentID + "'", null, null);
        tempCursor.moveToFirst();

        date = UtilitiesDates.dateFromString(tempCursor.getString(tempCursor.getColumnIndex(Tables.DATE)));
        b_date.setText( UtilitiesDates.getFancyDate(date) );
        categoryID = tempCursor.getLong(tempCursor.getColumnIndex(Tables.CATEGORY_ID));

        if(cursorCategories.moveToFirst()){
            do{
                long tempId = cursorCategories.getLong(cursorCategories.getColumnIndex(Tables.ID));
                if(categoryID == tempId){
                    categoryRadioAdapter.setPositionSelected(cursorCategories.getPosition(), categoryID);
                    break;
                }
            } while (cursorCategories.moveToNext());
        }
        e_amount.setText("" + tempCursor.getDouble(tempCursor.getColumnIndex(Tables.AMOUNT)));
        e_comments.setText(tempCursor.getString(tempCursor.getColumnIndex(Tables.COMMENTS)));
    }

    @Override
    public void delete() {
        Uri deleteUri;
        if(typeTransaction.equals(Tables.TYPE_EXPENSE)){
            deleteUri = ExpensorContract.ExpenseEntry.EXPENSE_URI;
        } else {
            deleteUri = ExpensorContract.IncomeEntry.INCOME_URI;
        }

        context.getContentResolver().delete(deleteUri, Tables.ID + " = '" + currentID + "'", null);
    }

    public void setButtonExpense(){
        b_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeTransaction.equals(Tables.TYPE_EXPENSE)){
                    typeTransaction = Tables.TYPE_EXPENSE;
                    setCategories();
                }
            }
        });
    }

    public void setButtonIncome(){
        b_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeTransaction.equals(Tables.TYPE_INCOME)){
                    typeTransaction = Tables.TYPE_INCOME;
                    setCategories();
                }
            }
        });
    }

    public void showCategories(){
        Intent intent = new Intent(getActivity(), ShowCategoriesActivity.class);
        intent.putExtra(Tables.TYPE, typeTransaction);
        startActivity(intent);
    }

    public void setCommunicator(ColorChangerInterface comm)
    {
        this.comm = comm;
    }

    @Override
    public boolean valuesAreCorrect() {
        boolean output = true;
        if(stringAmount == null || stringAmount.length() == 0) {
            e_amount.setError(getString(R.string.error_amount));
            output = false;
        } else {
            if (amount < UtilitiesNumbers.EPSILON) {
                e_amount.setError(getString(R.string.error_amount));
                output = false;
            }
        }
        return output;
    }
}
