package com.villoro.expensor_beta.add_or_update;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;

/**
 * Created by Arnau on 28/02/2015.
 */
public class AddOrUpdateActivity extends ActionBarActivity implements DialogOkCancel.CommOkCancel {

    public static String WHICH_LIST = "whichList";
    public static String ID_OBJECT = "idObject";

    public static int CASE_EXPENSE = 0;
    public static int CASE_INCOME = 1;
    public static int CASE_CATEGORIES = 2;
    public static int CASE_PEOPLE = 3;
    public static int CASE_GROUP = 4;

    int whichCase;
    long ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        whichCase = extras.getInt(WHICH_LIST);

        ID = extras.getLong(ID_OBJECT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int whichMenu;
        if (ID > 0)
        {
            whichMenu = R.menu.menu_update_something;
        }
        else
        {
            whichMenu = R.menu.menu_add_something;
        }

        getMenuInflater().inflate(whichMenu , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            //here should go the add logic
            return true;
        }
        if (id == R.id.action_cancel) {
            finish();
            return true;
        }
        if (id == R.id.action_discard) {

            DialogOkCancel dialog = new DialogOkCancel();
            dialog.setCommunicator(this);
            dialog.show(getSupportFragmentManager(), null);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void ifOkDo(boolean ok) {
        // TODO Auto-generated method stub
        if(ok)
        {
            //fragment.delete();
            finish();
        }
    }
}
