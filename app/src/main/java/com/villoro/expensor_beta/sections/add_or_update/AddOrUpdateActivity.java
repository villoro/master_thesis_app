package com.villoro.expensor_beta.sections.add_or_update;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;

/**
 * Created by Arnau on 28/02/2015.
 */
public class AddOrUpdateActivity extends ActionBarActivity implements DialogOkCancel.CommOkCancel,
            ColorChangerInterface {

    public final static String WHICH_LIST = "whichList";
    public final static String ID_OBJECT = "idObject";

    public final static int CASE_TRANSACTION_SIMPLE = 0;
    public final static int CASE_CATEGORIES = 1;
    public final static int CASE_PEOPLE = 2;
    public final static int CASE_GROUP = 3;
    public final static int CASE_TRANSACTION_PERSONAL = 5;
    public final static int CASE_TRANSACTION_GROUP = 6;


    int whichCase;
    long ID;

    ActionBar actionBar;
    String actionBarTitle;
    int colorDefault;

    AddOrUpdateTransactionSimpleFragment transactionSimpleFragment;
    AddOrUpdateGroupFragment groupFragment;
    AddOrUpdatePeopleFragment peopleFragment;
    AddOrUpdateCategoriesFragment categoriesFragment;
    AddOrUpdateTransactionPersonalFragment transactionPersonalFragment;
    AddOrUpdateTransactionGroupFragment transactionGroupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update);

        colorDefault = getResources().getColor(R.color.expensor_blue);

        Bundle extras = getIntent().getExtras();
        whichCase = extras.getInt(WHICH_LIST);

        ID = extras.getLong(ID_OBJECT);
        Log.d("AddOrUpdateActivity", "id= " + ID);

        Bundle output;

        if (savedInstanceState == null){
            switch (whichCase){
                case CASE_TRANSACTION_SIMPLE:
                    actionBarTitle = getResources().getString(R.string.ab_add_expense);
                    transactionSimpleFragment = new AddOrUpdateTransactionSimpleFragment();

                    output = new Bundle();
                    output.putString(Tables.TYPE, extras.getString(Tables.TYPE));
                    transactionSimpleFragment.setArguments(output);
                    transactionSimpleFragment.setCommunicator(this);

                    transactionSimpleFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, transactionSimpleFragment).commit();
                    break;
                case CASE_CATEGORIES:
                    actionBarTitle = getResources().getString(R.string.ab_add_category);
                    categoriesFragment = new AddOrUpdateCategoriesFragment();

                    output = new Bundle();
                    output.putString(Tables.TYPE, extras.getString(Tables.TYPE));
                    categoriesFragment.setArguments(output);

                    categoriesFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, categoriesFragment).commit();
                    break;
                case CASE_GROUP:
                    actionBarTitle = getResources().getString(R.string.ab_add_group);
                    groupFragment = new AddOrUpdateGroupFragment();
                    groupFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, groupFragment).commit();
                    break;
                case CASE_PEOPLE:
                    actionBarTitle = getResources().getString(R.string.ab_add_person);
                    peopleFragment = new AddOrUpdatePeopleFragment();
                    peopleFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, peopleFragment).commit();
                    break;
                case CASE_TRANSACTION_PERSONAL:
                    actionBarTitle = getResources().getString(R.string.ab_add_debt);
                    transactionPersonalFragment = new AddOrUpdateTransactionPersonalFragment();
                    transactionPersonalFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, transactionPersonalFragment).commit();
                    break;
                case CASE_TRANSACTION_GROUP:
                    actionBarTitle = getResources().getString(R.string.ab_add_group_transaction);
                    transactionGroupFragment = new AddOrUpdateTransactionGroupFragment();
                    transactionGroupFragment.initialize(ID);

                    output = new Bundle();
                    output.putLong(Tables.GROUP_ID, extras.getLong(Tables.GROUP_ID));
                    transactionGroupFragment.setArguments(output);

                    getSupportFragmentManager().beginTransaction().add(R.id.container, transactionGroupFragment).commit();
                    break;
            }

        }
        restoreActionBar(actionBarTitle, colorDefault);
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
            switch (whichCase){
                case CASE_TRANSACTION_SIMPLE:
                    transactionSimpleFragment.add();
                    break;
                case CASE_CATEGORIES:
                    categoriesFragment.add();
                    break;
                case CASE_GROUP:
                    groupFragment.add();
                    break;
                case CASE_PEOPLE:
                    peopleFragment.add();
                    break;
                case CASE_TRANSACTION_PERSONAL:
                    transactionPersonalFragment.add();
                    break;
                case CASE_TRANSACTION_GROUP:
                    transactionGroupFragment.add();
                    break;
            }

            finish();
            return true;
        }
        if (id == R.id.action_cancel) {
            finish();
            return true;
        }
        if (id == R.id.action_discard) {

            DialogOkCancel dialog = new DialogOkCancel();
            dialog.setCommunicator(this, DialogOkCancel.CASE_DIRECT);
            dialog.show(getSupportFragmentManager(), null);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void ifOkDo(boolean ok, int whichCase) {
        if(ok)
        {
            switch (whichCase){
                case CASE_TRANSACTION_SIMPLE:
                    transactionSimpleFragment.delete();
                    break;
                case CASE_CATEGORIES:
                    categoriesFragment.delete();
                    break;
                case CASE_GROUP:
                    groupFragment.delete();
                    break;
                case CASE_PEOPLE:
                    peopleFragment.delete();
                    break;
                case CASE_TRANSACTION_PERSONAL:
                    transactionPersonalFragment.delete();
                    break;
                case CASE_TRANSACTION_GROUP:
                    transactionGroupFragment.delete();
                    break;
            }

            finish();
        }
    }

    @Override
    public void restoreActionBar(String title, int color) {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
    }
}
