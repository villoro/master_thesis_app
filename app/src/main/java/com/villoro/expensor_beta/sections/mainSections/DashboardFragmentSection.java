package com.villoro.expensor_beta.sections.mainSections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import com.villoro.expensor_beta.adapters.CategoryGraphAdapter;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.ExpensorQueries;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.sections.MainActivity;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;

import java.lang.Override;

/**
 * Created by Arnau on 28/02/2015.
 */
public class DashboardFragmentSection extends Fragment{

    public static final double EPSILON = 0.000001;

    CommDashboard comm;

    Context context;
    double maxWidth;

    View g_income, g_expense, g_result, g_I_owe, g_owe_me;
    TextView tv_income, tv_expense, tv_result, tv_I_owe, tv_owe_me;
    LinearLayout ll_income, ll_expense, ll_I_owe, ll_owe_me;

    ListView lv_expense, lv_income;

    String typeTransaction;
    ImageView b_previous, b_next;
    int[] date;
    TextView tv_month;

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
    public void onResume() {
        super.onResume();
        setMainGraphs();
        setList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        context = getActivity();

        typeTransaction = Tables.TYPE_EXPENSE;
        maxWidth = getAvailableWidth();
        date = UtilitiesDates.getDate();

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
        switch (id){
            case R.id.action_add_transaction:
                Intent intent = new Intent(getActivity(), AddOrUpdateActivity.class);
                intent.putExtra(AddOrUpdateActivity.ID_OBJECT, -1);
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_TRANSACTION_SIMPLE);
                intent.putExtra(Tables.TYPE, typeTransaction);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_section_dashboard, container, false);

        g_income = rootView.findViewById(R.id.bar_income);
        g_expense = rootView.findViewById(R.id.bar_expense);
        g_result = rootView.findViewById(R.id.bar_result);

        g_I_owe = rootView.findViewById(R.id.bar_i_owe);
        g_owe_me = rootView.findViewById(R.id.bar_owe_me);

        tv_income = (TextView) rootView.findViewById(R.id.tv_income_value);
        tv_expense = (TextView) rootView.findViewById(R.id.tv_expense_value);
        tv_result = (TextView) rootView.findViewById(R.id.tv_result_value);

        tv_I_owe = (TextView) rootView.findViewById(R.id.tv_i_owe);
        tv_owe_me = (TextView) rootView.findViewById(R.id.tv_owe_me);

        lv_expense = (ListView) rootView.findViewById(R.id.lv_expenses);
        lv_income = (ListView) rootView.findViewById(R.id.lv_incomes);

        b_previous = (ImageView) rootView.findViewById(R.id.iv_previous);
        b_next = (ImageView) rootView.findViewById(R.id.iv_next);

        tv_month = (TextView) rootView.findViewById(R.id.tv_month);

        ll_income = (LinearLayout) rootView.findViewById(R.id.container_income);
        ll_expense = (LinearLayout) rootView.findViewById(R.id.container_expense);
        ll_I_owe = (LinearLayout) rootView.findViewById(R.id.container_i_owe);
        ll_owe_me = (LinearLayout) rootView.findViewById(R.id.container_owe_me);


        ll_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.goToSection(MainActivity.SECTION_HISTORY, Tables.TYPE_INCOME);
            }
        });
        ll_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.goToSection(MainActivity.SECTION_HISTORY, Tables.TYPE_EXPENSE);
            }
        });
        ll_I_owe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.goToSection(MainActivity.SECTION_PEOPLE, null);
            }
        });
        ll_owe_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.goToSection(MainActivity.SECTION_PEOPLE, null);
            }
        });

        setMainGraphs();
        setButtonPreviousNext();
        setList();

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

    public void setMainGraphs(){
        Cursor cursorGraph = getActivity().getContentResolver().query(
                ExpensorContract.GraphTransactionEntry.buildExpenseGraphUri(date[0], date[1]),
                null, null, null, null);
        double expense= 0;
        double income = 0;
        double result = 0;
        if(cursorGraph.moveToFirst()) {
            do {
                String type = cursorGraph.getString(cursorGraph.getColumnIndex(Tables.TYPE));
                if (type.equals(Tables.TYPE_EXPENSE)) {
                    expense = cursorGraph.getDouble(cursorGraph.getColumnIndex(Tables.SUM_AMOUNT));
                } else if (type.equals(Tables.TYPE_INCOME)) {
                    income = cursorGraph.getDouble(cursorGraph.getColumnIndex(Tables.SUM_AMOUNT));
                }

            } while (cursorGraph.moveToNext());
        }

        double maxTrans = Math.max(expense, income);
        result = income - expense;
        setWidth(g_expense, expense/maxTrans);
        setWidth(g_income, income/maxTrans);
        setWidth(g_result,  Math.abs(result)/ maxTrans);

        tv_expense.setText(UtilitiesNumbers.getFancyDouble(expense));
        tv_income.setText(UtilitiesNumbers.getFancyDouble(income));

        if(result < -EPSILON) {
            int colorRed = getResources().getColor(R.color.red_expense);
            g_result.setBackgroundColor(colorRed);
            tv_result.setText(UtilitiesNumbers.getFancyDouble(result));
        } else if (Math.abs(result) <= EPSILON) {
            tv_result.setText(UtilitiesNumbers.getFancyDouble(0));
        } else if (result > EPSILON){
            int colorGreen = getResources().getColor(R.color.green_income);
            g_result.setBackgroundColor(colorGreen);
            tv_result.setText("+ " + UtilitiesNumbers.getFancyDouble(result));
        }


        Cursor cursorGraphPeopleOweMe = getActivity().getContentResolver().query(
                ExpensorContract.GraphPersonalEntry.buildIncomeGraphAllUri(
                        ExpensorContract.PeopleEntry.CASE_BALANCE_POSITIVE, date[0], date[1] ),
                null, null, null, null);
        Cursor cursorGraphPeopleIOwe = getActivity().getContentResolver().query(
                ExpensorContract.GraphPersonalEntry.buildIncomeGraphAllUri(
                        ExpensorContract.PeopleEntry.CASE_BALANCE_NEGATIVE, date[0], date[1] ),
                null, null, null, null);

        double oweMe = 0;
        double IOwe = 0;
        if(cursorGraphPeopleOweMe.moveToFirst()) {
            oweMe = cursorGraphPeopleOweMe.getDouble(cursorGraphPeopleOweMe.getColumnIndex(Tables.SUM_AMOUNT2));
        }
        if(cursorGraphPeopleIOwe.moveToFirst()){
            IOwe = - cursorGraphPeopleIOwe.getDouble(cursorGraphPeopleIOwe.getColumnIndex(Tables.SUM_AMOUNT2));
        }
        double maxPeople = Math.max(oweMe, IOwe);
        setWidth(g_owe_me, oweMe/maxPeople);
        setWidth(g_I_owe, IOwe/maxPeople);

        tv_owe_me.setText(UtilitiesNumbers.getFancyDouble(oweMe));
        tv_I_owe.setText(UtilitiesNumbers.getFancyDouble(IOwe));


    }

    public void setList(){
        tv_month.setText(UtilitiesDates.setFancyMonthName(date));

        Cursor cursorExpense = getActivity().getContentResolver().query(
                ExpensorContract.GraphTransactionEntry.buildExpenseGraphAllUri(date[0], date[1]),
                null, null, null, null);
        double maxExpense = 0;
        if(cursorExpense.moveToFirst()) {
            do {
                if (cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT)) > maxExpense)
                    maxExpense = cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT));
            } while (cursorExpense.moveToNext());
        }
        CategoryGraphAdapter expenseAdapter = new CategoryGraphAdapter(context, cursorExpense, 0, maxWidth, maxExpense);
        lv_expense.setAdapter(expenseAdapter);
        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_expense);

        Cursor cursorIncome = getActivity().getContentResolver().query(
                ExpensorContract.GraphTransactionEntry.buildIncomeGraphAllUri(date[0], date[1]),
                null, null, null, null);
        double maxIncome = 0;
        if(cursorExpense.moveToFirst()) {
            do {
                if (cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT)) > maxIncome)
                    maxIncome = cursorExpense.getDouble(cursorExpense.getColumnIndex(Tables.SUM_AMOUNT));
            } while (cursorExpense.moveToNext());
        }
        CategoryGraphAdapter incomeAdapter = new CategoryGraphAdapter(context, cursorIncome, 0, maxWidth, maxIncome);
        lv_income.setAdapter(incomeAdapter);
        UtilitiesNumbers.setListViewHeightBasedOnChildren(lv_income);
    }

    public void setButtonPreviousNext(){
        b_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = UtilitiesDates.reduceMonth(date);
                setList();
                setMainGraphs();
            }
        });

        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = UtilitiesDates.incrementMonth(date);
                setList();
                setMainGraphs();
            }
        });
    }

    public void setCommunicator(CommDashboard comm)
    {
        this.comm = comm;

    }

    public interface CommDashboard {
        public void goToSection(int position, String typeTransaction);
    }

    //TODO probably I'll use a loader
}
