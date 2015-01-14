package com.villoro.expensor_split_expense;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private final static double EPSILON = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Double> deutors = new ArrayList<Double>();
        ArrayList<Double> creditors = new ArrayList<Double>();

        deutors.add(25.27);
        deutors.add(14.53);
        deutors.add(7.54);

        creditors.add(18.74);
        creditors.add(12.72);
        creditors.add(12.72);
        creditors.add(10.47);


        creditors.add(14.8);

        split(deutors, creditors);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void split(ArrayList<Double> deutors , ArrayList<Double> creditors){

        thereIsABank(deutors, creditors);

        for(Double deutor : deutors){
            for(Double creditor : creditors)
            {
                if(Math.abs(deutor - creditor) < EPSILON){
                    Log.v("", "deutor " + deutor + " = creditor " + creditor);
                }
            }
        }


    }

    public void thereIsABank(ArrayList<Double> deutors, ArrayList<Double> creditors){
        if(deutors.size() == 1)
        {
            Log.v("", "un sol deutor, assignem!");
        }
        if(creditors.size() == 1)
        {
            Log.v("", "un sol creditor, assignem!");
        }
    }

    public void onlyTwo(ArrayList<Double> deutors, ArrayList<Double> creditors){
        if(deutors.size() == 2)
        {
            Log.v("", "un sol deutor, assignem!");
        }
        if(creditors.size() == 2)
        {
            Log.v("", "un sol creditor, assignem!");
        }
    }
}
