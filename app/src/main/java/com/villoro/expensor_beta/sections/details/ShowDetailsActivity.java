package com.villoro.expensor_beta.sections.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

/**
 * Created by Arnau on 01/03/2015.
 */
public class ShowDetailsActivity extends ActionBarActivity {

    int whichCase;
    long ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update);

        Bundle extras = getIntent().getExtras();
        ID = extras.getLong(AddOrUpdateActivity.ID_OBJECT);
        Log.d("ShowDetailsActivity", "id= " + ID);

        DetailsGroupFragment detailsGroupFragment = new DetailsGroupFragment();
        detailsGroupFragment.initialize(ID);
        getSupportFragmentManager().beginTransaction().add(R.id.container, detailsGroupFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO make generic
        getMenuInflater().inflate(R.menu.menu_group_details, menu);
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
}
