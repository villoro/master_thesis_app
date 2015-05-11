package com.villoro.expensor_beta.sections;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.adapters.CategoryGraphAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.lang.Override;

/**
 * Created by Arnau on 28/02/2015.
 */
public class DashboardFragmentSection extends Fragment{

    Context context;
    double maxWidth;

    View g_income, g_expense, g_result;
    TextView tv_income, tv_expense, tv_result;

    ListView lv_expense, lv_income;

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

        g_income = rootView.findViewById(R.id.bar_income);
        g_expense = rootView.findViewById(R.id.bar_expense);
        g_result = rootView.findViewById(R.id.bar_result);

        tv_income = (TextView) rootView.findViewById(R.id.tv_income_value);
        tv_expense = (TextView) rootView.findViewById(R.id.tv_expense_value);
        tv_result = (TextView) rootView.findViewById(R.id.tv_result_value);

        lv_expense = (ListView) rootView.findViewById(R.id.lv_expenses);
        lv_income = (ListView) rootView.findViewById(R.id.lv_incomes);

        Cursor cursor = getActivity().getContentResolver().query(ExpensorContract.GraphEntry.buildExpenseGraphUri("2015", "5"),
                null, null, null, null);
        cursor.moveToFirst();
        double expense= 0;
        double income = 0;
        double result = 0;
        do{
            String type = cursor.getString(cursor.getColumnIndex(Tables.TYPE));
            if(type.equals(Tables.TYPE_EXPENSE)){
                expense = cursor.getDouble(cursor.getColumnIndex(Tables.SUM_AMOUNT));
            } else if (type.equals(Tables.TYPE_INCOME)) {
                income = cursor.getDouble(cursor.getColumnIndex(Tables.SUM_AMOUNT));
            }

        } while (cursor.moveToNext());

        double max = Math.max(expense, income);
        result = income - expense;
        setWidth(g_expense, expense/max);
        setWidth(g_income, income/max);
        setWidth(g_result,  Math.abs(result)/ max);

        if(expense > income) {
            int colorRed = getResources().getColor(R.color.red_expense);
            g_result.setBackgroundColor(colorRed);
            tv_result.setTextColor(colorRed);
        } else {
            int colorGreen = getResources().getColor(R.color.green_income);
            g_result.setBackgroundColor(colorGreen);
            tv_result.setTextColor(colorGreen);
        }

        tv_expense.setText(expense + " €");
        tv_income.setText(income + " €");
        tv_result.setText(result + " €");

        setLists();

        return rootView;
    }

    private double getAvailableWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return (metrics.widthPixels - dpToPx(32));
    }

    private double dpToPx(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void setWidth(View view, double percentage){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) Math.round(maxWidth * percentage);

        view.setLayoutParams(params);
    }

    public void setLists(){
        Cursor cursorExpense = getActivity().getContentResolver().query(ExpensorContract.GraphEntry.buildExpenseGraphAllUri("2015", "5"),
                null, null, null, null);
        double maxExpense = 0;
        cursorExpense.moveToFirst();
        do{
            if(cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT)) > maxExpense)
                maxExpense = cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT));
        } while (cursorExpense.moveToNext());
        CategoryGraphAdapter expenseAdapter = new CategoryGraphAdapter(context, cursorExpense, 0, maxWidth, maxExpense);
        lv_expense.setAdapter(expenseAdapter);
        Utility.setListViewHeightBasedOnChildren(lv_expense);

        Cursor cursorIncome = getActivity().getContentResolver().query(ExpensorContract.GraphEntry.buildIncomeGraphAllUri("2015", "5"),
                null, null, null, null);
        double maxIncome = 0;
        cursorExpense.moveToFirst();
        do{
            if(cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT)) > maxIncome)
                maxIncome = cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT));
        } while (cursorExpense.moveToNext());
        CategoryGraphAdapter incomeAdapter = new CategoryGraphAdapter(context, cursorIncome, 0, maxWidth, maxIncome);
        lv_income.setAdapter(incomeAdapter);
        Utility.setListViewHeightBasedOnChildren(lv_income);
    }

    //TODO probably I'll use a loader
}
