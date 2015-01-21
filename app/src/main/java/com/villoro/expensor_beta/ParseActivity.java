package com.villoro.expensor_beta;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;


public class ParseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_temp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void click(View view){
        /*Log.e("","starting to add info");
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseObject testObject = new ParseObject("Expense");
        testObject.put("amount",300);
        testObject.put("comment","patates");
        testObject.put("categoryID",1);
        testObject.put("date","201501151306");
        testObject.saveInBackground();

        Log.e("", "it works :)");*/

        insert();
    }

    public void insert(){

        /*ContentValues testValues = new ContentValues();
        testValues.put(Tables.LETTER, "P");
        testValues.put(Tables.NAME, "Food");
        testValues.put(Tables.TYPE, Tables.TYPE_EXPENSE);
        testValues.put(Tables.COLOR, 22);

        Log.e("", "Insertant cv= " + testValues.toString());

        Uri uri = this.getContentResolver().insert(ExpensorContract.CategoriesEntry.CONTENT_URI, testValues); */

        Cursor cursor = this.getContentResolver().query(
                ExpensorContract.CategoriesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        Log.e("", "Cursor= " + cursor.toString());
    }
}
