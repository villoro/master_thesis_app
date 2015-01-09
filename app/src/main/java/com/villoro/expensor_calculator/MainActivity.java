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

    public void findResult(View v) {
        String originalText = result.getText().toString();
        String text = originalText;

        String firstNumber = text.substring(0, 1);
        String secondNumber = "";
        int initialPoint = 0;
        int whichNumber = 1;
        String operation = "";

        for (int i = 1; i < text.length(); i++) {
            String letter = text.substring(i, i + 1);

            if (letter.equals("x")) {
                if(whichNumber == 1)
                {
                    whichNumber = 2;
                    operation = "x";
                } else {
                    text = reduce(text, initialPoint, i, firstNumber, secondNumber, operation);
                    firstNumber = calculate(firstNumber, secondNumber, operation);

                    i = firstNumber.length() + initialPoint - 1;
                    Log.e(LOG_TAG, "text= " + text);

                    secondNumber = "";
                    whichNumber = 1;
                    operation = "";
                }

            } else if (letter.equals("%") || letter.equals("/")) {
                if(whichNumber == 1)
                {
                    whichNumber = 2;
                    operation = "/";
                } else {
                    text = reduce(text, initialPoint, i, firstNumber, secondNumber, operation);
                    firstNumber = calculate(firstNumber, secondNumber, operation);

                    i = firstNumber.length() + initialPoint - 1;
                    Log.e(LOG_TAG, "text= " + text);

                    secondNumber = "";
                    whichNumber = 1;
                    operation = "";
                }
            } else if (letter.equals("+") || letter.equals("-")) {

                if ( whichNumber == 2)
                {
                    text = reduce(text, initialPoint, i, firstNumber, secondNumber, operation);
                    firstNumber = calculate(firstNumber, secondNumber, operation);

                    i = firstNumber.length() + initialPoint - 1;

                    Log.e(LOG_TAG, "result: " + text);
                } else {
                    firstNumber = "";
                    secondNumber = "";
                    operation = "";
                    if (i < text.length()) {
                        initialPoint = i + 1;
                    }
                }
                whichNumber = 1;



                Log.e(LOG_TAG, "TEMP= " + text);

            } else {
                if (whichNumber == 1) {
                    firstNumber += letter;
                } else if (whichNumber == 2) {
                    secondNumber += letter;
                }
            }
        }

        if(whichNumber == 2)
        {
            text = reduce(text, initialPoint, text.length(), firstNumber, secondNumber, operation);
        }

        Log.e(LOG_TAG, "text pas 1= " + text);

        firstNumber = text.substring(0, 1);
        secondNumber = "";
        initialPoint = 0;
        whichNumber = 1;
        operation = "";

        for (int i = 1; i < text.length(); i++) {
            String letter = text.substring(i, i + 1);

            if (letter.equals("+")) {
                if(whichNumber == 1)
                {
                    whichNumber = 2;
                    operation = "+";
                } else {
                    text = reduce(text, initialPoint, i, firstNumber, secondNumber, operation);
                    firstNumber = calculate(firstNumber, secondNumber, operation);

                    i = firstNumber.length() + initialPoint - 1;
                    Log.e(LOG_TAG, "text= " + text);

                    secondNumber = "";
                    whichNumber = 1;
                    operation = "";
                }
            } else if (letter.equals("-")) {
                if(whichNumber == 1)
                {
                    whichNumber = 2;
                    operation = "-";
                } else {
                    text = reduce(text, initialPoint, i, firstNumber, secondNumber, operation);
                    firstNumber = calculate(firstNumber, secondNumber, operation);

                    i = firstNumber.length() + initialPoint - 1;
                    Log.e(LOG_TAG, "text= " + text);

                    secondNumber = "";
                    whichNumber = 1;
                    operation = "";
                }
            } else {
                if (whichNumber == 1) {
                    firstNumber += letter;
                } else if (whichNumber == 2) {
                    secondNumber += letter;
                }
            }
        }

        if(whichNumber == 2)
        {
            text = reduce(text, initialPoint, text.length(), firstNumber, secondNumber, operation);
        }

        Log.e(LOG_TAG, "text pas 2= " + text);

        text = Double.toString(round( Double.parseDouble(text)));
        result.setText(originalText + " = " + text);
    }

    public String reduce(String text, int initialPoint, int finalPoint, String firstNumber, String secondNumber, String operation){
        String output;
        if (initialPoint > 0) {
            output = text.substring(0,initialPoint);
        } else {
            output = "";
        }
        output += calculate(firstNumber, secondNumber, operation);
        if (finalPoint < text.length()){
            output += text.substring(finalPoint, text.length());
        }
        Log.e(LOG_TAG, "text= " + text);
        Log.e(LOG_TAG, "output= " + output);

        return output;
    }

    public String calculate(String firstNumber, String secondNumber, String operation){

        double output;
        if (operation.equals("x")) {
            output = Double.parseDouble(firstNumber) * Double.parseDouble(secondNumber);
        } else if (operation.equals("/")) {
            output = Double.parseDouble(firstNumber) / Double.parseDouble(secondNumber);
        } else if (operation.equals("+")) {
            output = Double.parseDouble(firstNumber) + Double.parseDouble(secondNumber);
        } else if (operation.equals("-")) {
            output = Double.parseDouble(firstNumber) - Double.parseDouble(secondNumber);
        } else {
            output = 0;
        }
        return Double.toString(output);
    }

    public static double round(double amount)
    {
        return round(amount, 2);
    }
    public static double round(double amount, int decimals)
    {
        Log.e("", "input round= " + amount);
        if (decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        Log.e("", "input round= " + bd.doubleValue());
        return bd.doubleValue();
    }
}
