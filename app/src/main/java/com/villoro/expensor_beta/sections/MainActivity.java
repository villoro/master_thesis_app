package com.villoro.expensor_beta.sections;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.parse.ParseUser;
import com.villoro.expensor_beta.mainActivitiesAndApp.LoginActivity;
import com.villoro.expensor_beta.mainActivitiesAndApp.ParseActivity;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.sections.navigationDrawer.NavigationDrawerFragment;
import com.villoro.expensor_beta.sync.parse.ParseAdapter;
import com.villoro.expensor_beta.sections.mainSections.DashboardFragmentSection;
import com.villoro.expensor_beta.sections.mainSections.GroupFragmentSection;
import com.villoro.expensor_beta.sections.mainSections.HistoryFragmentSection;
import com.villoro.expensor_beta.sections.mainSections.PeopleFragmentSection;
import com.villoro.expensor_beta.sync.ExpensorSyncAdapter;

import java.lang.CharSequence;
import java.lang.Override;
import java.lang.String;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, DashboardFragmentSection.CommDashboard,
                HistoryFragmentSection.CommColorChanger{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final int SECTION_DASHBOARD = 0;
    public static final int SECTION_HISTORY = 1;
    public static final int SECTION_PEOPLE = 2;
    public static final int SECTION_GROUPS = 3;
    public static final int SECTION_SETTINGS = 4;

    ActionBar actionBar;
    int colorActionBar, colorDefault;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        colorDefault = getResources().getColor(R.color.expensor_blue);
        colorActionBar = colorDefault;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        goToSection(position, null);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case SECTION_DASHBOARD:
                mTitle = getString(R.string.title_section_dashboard);
                break;
            case SECTION_HISTORY:
                mTitle = getString(R.string.title_section_history);
                break;
            case SECTION_PEOPLE:
                mTitle = getString(R.string.title_section_people);
                break;
            case SECTION_GROUPS:
                mTitle = getString(R.string.title_section_groups);
                break;
            case SECTION_SETTINGS:
                mTitle = getString(R.string.title_section_settings);
                break;
        }
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setBackgroundDrawable(new ColorDrawable(colorActionBar));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_sync:
                ExpensorSyncAdapter.syncImmediately(this);
                return true;
            case R.id.action_settings:
                //TODO make real settings activity
                Intent intent2 = new Intent(this, ParseActivity.class);
                startActivity(intent2);

                return true;
            case R.id.action_log_out:
                //Logout and delete database
                ParseUser.logOut();
                ParseAdapter.deleteAll(this);
                finish();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToSection(int position, String typeTransaction) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        //decide which fragment is needed
        switch (position){
            case SECTION_DASHBOARD:
                setColor(colorDefault);
                DashboardFragmentSection dashboardFragmentSection = DashboardFragmentSection.newDashboardFragment(position);
                dashboardFragmentSection.setCommunicator(this);
                fragmentManager.beginTransaction().
                        replace(R.id.container, dashboardFragmentSection).commit();
                break;
            case SECTION_HISTORY:
                HistoryFragmentSection historyFragmentSection = HistoryFragmentSection.newHistoryFragment(position, typeTransaction);
                historyFragmentSection.setCommunicator(this);
                fragmentManager.beginTransaction().replace(R.id.container, historyFragmentSection).commit();
                break;
            case SECTION_PEOPLE:
                setColor(colorDefault);
                fragmentManager.beginTransaction().
                        replace(R.id.container, PeopleFragmentSection.newPeopleFragment(position)).commit();
                break;
            case SECTION_GROUPS:
                setColor(colorDefault);
                fragmentManager.beginTransaction().
                        replace(R.id.container, GroupFragmentSection.newGroupFragment(position)).commit();
                break;
            case SECTION_SETTINGS:
                /*fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newDashboardFragment(position)).commit();*/
                break;
        }
    }

    @Override
    public void setColor(int color) {
        actionBar = getSupportActionBar();
        colorActionBar = color;
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
    }
}
