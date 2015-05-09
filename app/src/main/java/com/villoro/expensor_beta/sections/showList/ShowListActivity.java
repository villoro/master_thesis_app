package com.villoro.expensor_beta.sections.showList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.adapters.CategoryAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 09/05/2015.
 */
public class ShowListActivity extends ActionBarActivity {

    ListView lv_colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_listview);

        lv_colors = (ListView) findViewById(R.id.m_listview);
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
            Intent intent = new Intent(this, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_CATEGORIES);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setList(){
        Cursor cursor = getContentResolver().query(ExpensorContract.CategoriesEntry.CONTENT_URI, null, null, null, null);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, cursor, 0);
        lv_colors.setAdapter(categoryAdapter);
    }

}
