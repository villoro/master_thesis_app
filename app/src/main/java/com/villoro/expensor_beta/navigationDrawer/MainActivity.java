package com.villoro.expensor_beta.navigationDrawer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.parse.ParseUser;
import com.villoro.expensor_beta.LoginActivity;
import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.navigationDrawer.NavigationDrawerFragment;
import com.villoro.expensor_beta.sections.DashboardFragment;
import com.villoro.expensor_beta.sync.ExpensorSyncAdapter;

import java.lang.CharSequence;
import java.lang.Override;
import java.lang.String;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final int SECTION_MAIN = 0;
    private static final int SECTION_HISTORY = 1;
    private static final int SECTION_PEOPLE = 2;
    private static final int SECTION_GROUPS = 3;
    private static final int SECTION_SETTINGS = 4;

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

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        //decide which fragment is needed
        switch (position){
            case SECTION_MAIN:
                fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newMainFragment(position)).commit();
                break;
            case SECTION_HISTORY:
                fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newMainFragment(position)).commit();
                break;
            case SECTION_PEOPLE:
                fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newMainFragment(position)).commit();
                break;
            case SECTION_GROUPS:
                fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newMainFragment(position)).commit();
                break;
            case SECTION_SETTINGS:
                fragmentManager.beginTransaction().
                        replace(R.id.container, DashboardFragment.newMainFragment(position)).commit();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case SECTION_MAIN:
                mTitle = getString(R.string.title_section_main);
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
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
                return true;
            case R.id.action_log_out:
                ParseUser.logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    } */

}
