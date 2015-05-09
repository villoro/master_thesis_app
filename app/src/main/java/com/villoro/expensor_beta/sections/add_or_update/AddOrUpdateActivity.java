package com.villoro.expensor_beta.sections.add_or_update;

import android.os.Bundle;
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
public class AddOrUpdateActivity extends ActionBarActivity implements DialogOkCancel.CommOkCancel {

    public final static String WHICH_LIST = "whichList";
    public final static String ID_OBJECT = "idObject";

    public final static int CASE_EXPENSE = 0;
    public final static int CASE_INCOME = 1;
    public final static int CASE_CATEGORIES = 2;
    public final static int CASE_PEOPLE = 3;
    public final static int CASE_GROUP = 4;
    public final static int CASE_PEOPLE_IN_GROUP = 5;

    int whichCase;
    long ID;

    AddOrUpdateTransactionSimpleFragment transactionSimpleFragment;
    AddOrUpdateGroupFragment groupFragment;
    AddOrUpdatePeopleFragment peopleFragment;
    AddOrUpdatePeopleInGroupFragment peopleInGroupFragment;
    AddOrUpdateCategoriesFragment categoriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update);

        Bundle extras = getIntent().getExtras();
        whichCase = extras.getInt(WHICH_LIST);

        ID = extras.getLong(ID_OBJECT);
        Log.d("AddOrUpdateActivity", "id= " + ID);

        if (savedInstanceState == null){
            switch (whichCase){
                case CASE_EXPENSE:
                    transactionSimpleFragment = new AddOrUpdateTransactionSimpleFragment();
                    transactionSimpleFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, transactionSimpleFragment).commit();
                    break;
                case CASE_CATEGORIES:
                    categoriesFragment = new AddOrUpdateCategoriesFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString(Tables.TYPE, extras.getString(Tables.TYPE));
                    categoriesFragment.setArguments(bundle);

                    categoriesFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, categoriesFragment).commit();
                    break;
                case CASE_GROUP:
                    groupFragment = new AddOrUpdateGroupFragment();
                    groupFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, groupFragment).commit();
                    break;
                case CASE_PEOPLE:
                    peopleFragment = new AddOrUpdatePeopleFragment();
                    peopleFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, peopleFragment).commit();
                    break;
                case CASE_PEOPLE_IN_GROUP:
                    peopleInGroupFragment = new AddOrUpdatePeopleInGroupFragment();
                    peopleInGroupFragment.initialize(ID);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, peopleInGroupFragment).commit();
            }

        }
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
                case CASE_EXPENSE:
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
                case CASE_PEOPLE_IN_GROUP:
                    peopleInGroupFragment.add();
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
                case CASE_EXPENSE:
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
                case CASE_PEOPLE_IN_GROUP:
                    peopleInGroupFragment.delete();
                    break;
            }

            finish();
        }
    }
}