package com.villoro.expensor_beta.sections;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;

import java.lang.Override;

/**
 * Created by Arnau on 28/02/2015.
 */
public class DashboardFragmentSection extends Fragment{

    Context context;
    double maxWidth;

    public DashboardFragmentSection(){};

    public static DashboardFragmentSection newDashboardFragment(int sectionNumber){
        DashboardFragmentSection fragment = new DashboardFragmentSection();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //use to set the name of the section in the navigationDrawer inside MainActivity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        context = getActivity();

        maxWidth = getAvailableWidth();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_section_dashboard, container, false);

        View l_income = rootView.findViewById(R.id.bar_income);
        View l_expense = rootView.findViewById(R.id.bar_expense);

        Cursor cursor = getActivity().getContentResolver().query(ExpensorContract.GraphEntry.buildExpenseGraphUri("2015", "5"),
                null, null, null, null);

        Log.d("Dashboard Section", "cursor count= " + cursor.getCount());

        return rootView;
    }

    public double getAvailableWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return (metrics.widthPixels - dpToPx(32));
    }

    private double dpToPx(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setWidth(View view, double percentage){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) Math.round(maxWidth * percentage);

        view.setLayoutParams(params);
    }

    //TODO probably I'll use a loader
}
