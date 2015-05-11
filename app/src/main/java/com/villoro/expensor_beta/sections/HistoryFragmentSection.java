package com.villoro.expensor_beta.sections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.Utility;
import com.villoro.expensor_beta.adapters.TransactionSimpleAdapter;
import com.villoro.expensor_beta.sections.add_or_update.AddOrUpdateActivity;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;
import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;

/**
 * Created by Arnau on 28/02/2015.
 */

public class HistoryFragmentSection extends Fragment implements DialogLongClickList.CommGetChoice, DialogOkCancel.CommOkCancel{

    ListView listView;
    Context context;
    long listID;

    String typeTransaction;
    Button b_expense, b_income;
    ImageView b_previous, b_next;
    int[] date;
    TextView tv_month;
    RelativeLayout month_container;

    int colorGreen, colorRed;
    Uri uri;


    TransactionSimpleAdapter transactionSimpleAdapter;

    public HistoryFragmentSection(){};

    public static HistoryFragmentSection newHistoryFragment(int sectionNumber){
        HistoryFragmentSection fragment = new HistoryFragmentSection();
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

        typeTransaction = Tables.TYPE_EXPENSE;
        date = Utility.getDate();
        uri = ExpensorContract.ExpenseEntry.buildExpenseUri(date[0], date[1]);
        colorGreen = context.getResources().getColor(R.color.green_income);
        colorGreen = context.getResources().getColor(R.color.red_expense);
        Log.e("", "typeTransaction= " + typeTransaction);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
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
                intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_EXPENSE);
                intent.putExtra(Tables.TYPE, typeTransaction);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_section_history, container, false);
        listView = (ListView) rootView.findViewById(R.id.m_listview);

        b_expense = (Button) rootView.findViewById(R.id.b_expense);
        b_income = (Button) rootView.findViewById(R.id.b_income);
        setButtonExpense();
        setButtonIncome();

        b_previous = (ImageView) rootView.findViewById(R.id.iv_previous);
        b_next = (ImageView) rootView.findViewById(R.id.iv_next);

        setButtonPreviousNext();

        tv_month = (TextView) rootView.findViewById(R.id.tv_month);
        month_container = (RelativeLayout) rootView.findViewById(R.id.month_container);

        setListView();
        return rootView;
    }

    public void setListView(){
        tv_month.setText(Utility.setFancyMonthName(date));

        Uri uri;
        if(typeTransaction.equals(Tables.TYPE_INCOME)){
            uri = ExpensorContract.IncomeEntry.buildIncomeUri(date[0], date[1]);
            month_container.setBackgroundColor(Color.GREEN);
            Log.e("HistoryFragment", "trying to put color(green)= " + colorGreen);
 //TODO NEEEEEEEEEEDS WOOOOOOOOOOOOOOOOOORK
        } else {
            uri = ExpensorContract.ExpenseEntry.buildExpenseUri(date[0], date[1]);
            month_container.setBackgroundColor(Color.RED);
            Log.e("HistoryFragment", "trying to put color(red)= " + colorRed);
 //TODO NEEEEEEEEEEDS WOOOOOOOOOOOOOOOOOORK
        }

        Cursor cursor = getActivity().getContentResolver().query(
                uri, null, null, null, null);
        transactionSimpleAdapter = new TransactionSimpleAdapter(context, cursor, 0);

        listView.setAdapter(transactionSimpleAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showLongClickList(id);
                Log.d("HistoryFragmentSection", "id= " + id);
                return true;
            }
        });
    }

    public void showLongClickList(long id){
        DialogLongClickList dialog = new DialogLongClickList();
        dialog.setCommunicator(this);
        dialog.show(getFragmentManager(), null);
        listID = id;
    }

    @Override
    public void getChoice(int choice) {
        if (choice == DialogLongClickList.CASE_EDIT)
        {
            Intent intent = new Intent(context, AddOrUpdateActivity.class);
            intent.putExtra(AddOrUpdateActivity.ID_OBJECT, listID);
            intent.putExtra(AddOrUpdateActivity.WHICH_LIST, AddOrUpdateActivity.CASE_EXPENSE);
            intent.putExtra(Tables.TYPE, typeTransaction);

            startActivity(intent);
        }
        if (choice == DialogLongClickList.CASE_DELETE)
        {
            DialogOkCancel dialog = new DialogOkCancel();
            dialog.setCommunicator(this, dialog.CASE_FROM_LONG_CLICK);
            dialog.show(getFragmentManager(), null);

        }
    }

    @Override
    public void ifOkDo(boolean ok, int whichCase) {
        if(ok){
            Log.d("HistoryFragmentSection", "trying to delete id= " + listID);
            context.getContentResolver().delete(uri, Tables.ID + " = '" + listID + "'", null);
            setListView();
        }
    }

    public void setButtonExpense(){
        b_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeTransaction.equals(Tables.TYPE_EXPENSE)){
                    typeTransaction = Tables.TYPE_EXPENSE;
                    setListView();
                }
            }
        });
    }

    public void setButtonIncome(){
        b_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!typeTransaction.equals(Tables.TYPE_INCOME)){
                    typeTransaction = Tables.TYPE_INCOME;
                    setListView();
                }
            }
        });
    }

    public void setButtonPreviousNext(){
        b_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Utility.reduceMonth(date);
                setListView();
            }
        });

        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Utility.incrementMonth(date);
                setListView();
            }
        });
    }
}
