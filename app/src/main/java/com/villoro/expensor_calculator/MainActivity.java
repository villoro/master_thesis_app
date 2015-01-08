package com.villoro.expensor_calculator;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends ActionBarActivity {

    Button result;
    String LOG_TAG = "calculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (Button) findViewById(R.id.result);
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

    public void click(View v)
    {
        Button buttonClicked = (Button) v;
        result.setText(result.getText().toString() + buttonClicked.getText().toString());
    }

    public void delete(View v)
    {
        result.setText("");
    }

    public void findResult(View v)
    {
        String originalText = result.getText().toString();
        String text = originalText;

        String firstNumber = text.substring(0,1);
        String secondNumber = "";

        int whichNumber = 1;
        String operation = "";
        double valueResult;

        for (int i = 1; i < text.length(); i++)
        {
            String letter = text.substring(i, i+1);

            if(letter.equals("x")){
                whichNumber = 2;
                operation = "x";
            }
            else if(letter.equals("%") || letter.equals("/")){
                whichNumber = 2;
                operation = "/";
            }
            else{
                if(whichNumber == 1){
                    firstNumber += letter;
                }
                else if (whichNumber == 2){
                    secondNumber += letter;
                }
            }
        }

        Log.e(LOG_TAG, "first num: " + firstNumber + " second num: " + secondNumber);
        Log.d(LOG_TAG, "operation: " + operation);

        if(operation.equals("x")) {
            valueResult =Double.parseDouble(firstNumber)*Double.parseDouble(secondNumber);
        }
        else if(operation.equals("/")){
            valueResult = Double.parseDouble(firstNumber)/Double.parseDouble(secondNumber);
        }
        else {valueResult = 0; }

        text = Double.toString(round(valueResult));
        result.setText(originalText + " = " + text);
    }

    public static double round(double amount)
    {
        return round(amount, 2);
    }
    public static double round(double amount, int decimals)
    {
        if (decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
