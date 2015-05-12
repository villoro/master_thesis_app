package com.villoro.expensor_beta.sections.showList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.adapters.CategoryAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 09/05/2015.
 */
public class ShowListActivity extends ActionBarActivity implements DialogLongClickList.CommGetChoice, DialogOkCancel.CommOkCancel {

    ListView listView;

    String typeCategory;
    Button b_expense, b_income;
    Uri uri;

    long listID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_categories);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.expensor_blue)));

        Bundle bundle = getIntent().getExtras();
        typeCategory = bundle.getString(Tables.TYPE);
        if(typeCategory.equals(Tables.TYPE_EXPENSE)){
            uri = ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI;
        } else {
            uri = ExpensorContract.CategoriesEntry.CATEGORIES_INCOME_URI;
        }
        Log.e("", "type= " + typeCategory);

        listView = (ListView) findViewById(R.id.m_listview);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLongClickList(id);
                return true;
            }
        });

        b_expense = (Button) findViewById(R.id.b_expense);
        b_income = (Button) findViewById(R.id.b_income);
        setButtonExpense();
        setButtonIncome();

        setList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_transaction) {
            addCategory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setList(){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, cursor, 0);
        listView.setAdapter(categoryAdapter);
    }

    public void setButtonExpense(){
        b_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeCategory.equals(Tables.TYPE_EXPENSE)){
                    typeCategory = Tables.TYPE_EXPENSE;
                    uri = ExpensorContract.CategoriesEntry.CATEGORIES_EXPENSE_URI;
                    Log.e("", "typeTransaction= " + typeCategory);
                    Log.e("", uri.toString());
                    setList();
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
                    uri = ExpensorContract.CategoriesEntry.CATEGORIES_INCOME_URI;
                    Log.e("", "typeTransaction= " + typeCategory);
                    Log.e("", uri.toString());
                    setList();
                }
            }
        });
    }

    public void showLongClickList(long id){
        DialogLongClickList dialog = new DialogLongClickList();
        dialog.setCommunicator(this);
        dialog.show(getSupportFragmentManager(), null);
        listID = id;
    }

    @Override
    public void getChoice(int choice) {
        if (choice == DialogLongClickList.CASE_EDIT)
        {
            Intent intent = new Intent(this, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, listID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_EXPENSE);
            intent.putExtra(Tables.TYPE, typeCategory);

            startActivity(intent);
        }
        if (choice == DialogLongClickList.CASE_DELETE)
        {
            DialogOkCancel dialog = new DialogOkCancel();
            dialog.setCommunicator(this, dialog.CASE_FROM_LONG_CLICK);
            dialog.show(getSupportFragmentManager(), null);

        }
    }

    @Override
    public void ifOkDo(boolean ok, int whichCase) {
        if(ok){
            Log.d("TransactionSimpleFragment", "trying to delete id= " + listID);
            getContentResolver().delete(uri, Tables.ID + " = '" + listID + "'", null);
            setList();
        }
    }

    public void addCategory(){
        Intent intent = new Intent(this, AddOrUpdateActivity.class);
        intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
        intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_CATEGORIES);
        intent.putExtra(Tables.TYPE, typeCategory);
        startActivity(intent);
    }
}
