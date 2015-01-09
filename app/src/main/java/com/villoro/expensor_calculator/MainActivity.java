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

    public void deleteOne(View v){
        if(result.length() > 1) {
            String text = result.getText().toString();
            result.setText(text.substring(0, text.length() - 1));
        } else {
            delete(v);
        }
    }

    public void findResult(View v) {
        String originalText = result.getText().toString();

        Calculator calculator = new Calculator(originalText);

        result.setText(originalText + " = " + calculator.doIt());
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

    public class Calculator {
        String originalText;
        String text;
        boolean error;

        String firstNumber;
        String secondNumber;
        int initialPoint;
        int i;
        int whichNumber;
        String operation;

        private final double EPSILON = 0.00001;

        public Calculator(String originalText){
            this.originalText = originalText;
            text = originalText;
        }

        private void initialize(){
            reset();
            firstNumber = "";
            readNumber(text.substring(0,1));
            initialPoint = 0;
        }

        private void reset(){
            secondNumber = "";
            whichNumber = 1;
            operation = "";
        }

        public String doIt(){

            error = false;

            text = text.replace(",", ".");

            secondStep();
            Log.e(LOG_TAG, "text pas 2= " + text);

            thirdStep();
            Log.e(LOG_TAG, "text pas 3= " + text);

            if (!error) {
                text = Double.toString(round( Double.parseDouble(text)));
                return text.replace(".", ",");
            } else {
                return "Math error";
            }
        }

        private void secondStep(){
            initialize();
            for (i = 1; i < text.length(); i++) {
                if (!error) {
                    String letter = text.substring(i, i + 1);

                    if (letter.equals("x")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "x";
                        } else {
                            reduce();
                            Log.e("", "firstNum= " + firstNumber);
                        }
                    } else if (letter.equals("%") || letter.equals("/")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "/";
                        } else {
                            reduce();
                        }
                    } else if (letter.equals("+") || letter.equals("-")) {

                        if (whichNumber == 2) {
                            reduce();
                        } else {
                            firstNumber = "";
                            reset();
                            if (i < text.length()) {
                                initialPoint = i + 1;
                            }
                        }
                    } else
                    {
                        readNumber(letter);
                    }

                }
                else
                {
                    i = text.length();
                }
            }
            finalizeStep();
        }

        private void thirdStep(){
            initialize();
            for (i = 1; i < text.length(); i++) {
                if(!error) {
                    String letter = text.substring(i, i + 1);

                    if (letter.equals("+")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "+";
                        } else {
                            reduce();
                        }
                    } else if (letter.equals("-")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "-";
                        } else {
                            reduce();
                        }
                    } else
                    {
                        readNumber(letter);
                    }
                }
            }
            finalizeStep();
        }

        private void finalizeStep(){
            if(whichNumber == 2)
            {
                reduce();
            }
        }

        private boolean equalsNumber(String letter) {
            return (letter.equals(".")
                    || letter.equals("0")
                    || letter.equals("1")
                    || letter.equals("2")
                    || letter.equals("3")
                    || letter.equals("4")
                    || letter.equals("5")
                    || letter.equals("6")
                    || letter.equals("7")
                    || letter.equals("8")
                    || letter.equals("9"));
        }

        private void readNumber(String letter){
            Log.e("", "letter= " + letter + " ,first initial= " + firstNumber);
            if (equalsNumber(letter)) {
                if (whichNumber == 1) {
                    firstNumber += letter;
                } else if (whichNumber == 2) {
                    secondNumber += letter;
                }
            } else {
                error = true;
            }
            Log.e("", "letter= " + letter + " ,first final= " + firstNumber);
        }

        private void reduce(){
            String output;

            if (initialPoint > 0) {
                output = text.substring(0,initialPoint);
            } else {
                output = "";
            }

            firstNumber = calculate();
            Log.e("", "inside reduce= " + firstNumber);
            reset();
            output += firstNumber;

            if (i < text.length()){
                output += text.substring(i, text.length());
            }
            Log.e(LOG_TAG, "text= " + text);
            Log.e(LOG_TAG, "output= " + output);

            i = firstNumber.length() + initialPoint - 1;
            text = output;
        }

        private String calculate(){

            double output;
            if (operation.equals("x"))
            {
                output = Double.parseDouble(firstNumber) * Double.parseDouble(secondNumber);
            }
            else if (operation.equals("/"))
            {
                Log.e("", "firstNum= " + firstNumber);
                if ( Double.parseDouble(firstNumber) > EPSILON )
                {
                    Log.d("", "argh");
                    output = Double.parseDouble(firstNumber) / Double.parseDouble(secondNumber);
                }
                else
                {
                    Log.d("", "ok");
                    error = true;
                    output = 0;
                }
            }
            else if (operation.equals("+"))
            {
                output = Double.parseDouble(firstNumber) + Double.parseDouble(secondNumber);
            }
            else if (operation.equals("-"))
            {
                output = Double.parseDouble(firstNumber) - Double.parseDouble(secondNumber);
            }
            else
            {
                output = 0;
            }

            return Double.toString(output);
        }

    }
}
