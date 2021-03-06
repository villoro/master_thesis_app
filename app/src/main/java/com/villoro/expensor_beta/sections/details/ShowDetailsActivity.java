package com.villoro.expensor_beta.sections.details;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class ShowDetailsActivity extends ActionBarActivity implements DetailsInterfaces.CommDetailsGroup,
            DetailsInterfaces.CommSetName{

    public final static String WHICH_LIST = "whichList";
    public final static String ID_OBJECT = "idObject";

    public final static int SECTION_HISTORY = 1;
    public final static int SECTION_SUMMARY = 2;

    public final static int CASE_PEOPLE = 2;
    public final static int CASE_GROUP = 3;

    int whichCase;
    long ID;

    DetailsGroupSummaryFragment detailsGroupSummaryFragment;
    DetailsGroupHistoryFragment detailsGroupHistoryFragment;
    DetailsPeopleFragment detailsPeopleFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.expensor_blue)));

        Bundle extras = getIntent().getExtras();
        whichCase = extras.getInt(WHICH_LIST);

        ID = extras.getLong(ID_OBJECT);

        if (savedInstanceState == null) {
            switch (whichCase) {
                case CASE_GROUP:
                    detailsGroupSummaryFragment = new DetailsGroupSummaryFragment();
                    detailsGroupSummaryFragment.initialize(ID, this, this);

                    detailsGroupHistoryFragment = new DetailsGroupHistoryFragment();
                    detailsGroupHistoryFragment.initialize(ID, this, this);

                    getSupportFragmentManager().beginTransaction().add(R.id.container, detailsGroupSummaryFragment).commit();
                    break;
                case CASE_PEOPLE:
                    detailsPeopleFragment = new DetailsPeopleFragment();
                    detailsPeopleFragment.initialize(ID, this);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, detailsPeopleFragment).commit();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO make generic
        //getMenuInflater().inflate(R.menu.menu_group_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, ID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_GROUP);

            startActivity(intent);
            return true;
        }
        if (id == R.id.action_discard) {

            //

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeToSection(int section) {
        if(SECTION_SUMMARY == section) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailsGroupSummaryFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailsGroupHistoryFragment).commit();
        }
    }

    @Override
    public void changeTitle(String name) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }
}
