package com.villoro.expensor_calculator;

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

        result.setText(originalText + " = " + calculator.calculate());
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
        boolean error;

        String firstNumber;
        String secondNumber;
        int initialPoint;
        int i;
        int whichNumber;
        String operation;

        private final double EPSILON = 0.00001;

        public Calculator(String text){
            this.originalText = text.replace(",", ".").replace(" ","");
        }

        private void initialize(String input){
            reset();
            firstNumber = "";
            readNumber(input.substring(0,1));
            initialPoint = 0;
        }

        private void reset(){
            secondNumber = "";
            whichNumber = 1;
            operation = "";
        }

        public String calculate(){

            error = false;

            Log.d("", "to do: " + originalText);
            int numberParenthesis = originalText.length() - originalText.replace("(", "").length();

            if (numberParenthesis != originalText.length() - originalText.replace(")", "").length())
            {
                error = true;
            }
            if(originalText.contains(".(") || originalText.contains(".)") || originalText.contains("(.")|| originalText.contains(")."))
            {
                error = true;
            }

            String text = originalText;


            if(!error) {
                while (text.contains("(") && !error) {
                    Log.d("", "next it: " + text);
                    text = firstStep(text);
                }
            }

            text = thirdStep(secondStep(text));


            if (!error) {
                return Double.toString(round( Double.parseDouble(text))).replace(".", ",");
            } else {
                return "Math error";
            }
        }

        private String firstStep(String input)
        {
            String output = input;
            boolean opened = false;
            boolean stop = false;
            int whereOpened = -1;
            for (int j = input.length() - 1; j >= 0 && !stop; j--) {
                if (!error) {
                    String letter = output.substring(j, j + 1);
                    if (letter.equals(")")) {
                        opened = true;
                        whereOpened = j;
                    } else if (letter.equals("(")) {
                        if (opened) {
                            String insideParenthesis = output.substring(j + 1, whereOpened);
                            if (!insideParenthesis.equals("") && insideParenthesis != null) {
                                Log.d(LOG_TAG, "inside parenthesis= " + insideParenthesis);

                                String first = secondStep(insideParenthesis);

                                Log.d(LOG_TAG, "text pas 2= " + first);

                                String second = thirdStep(first);

                                Log.d(LOG_TAG, "text pas 3= " + second);
                                output = output.replace(output.substring(j, whereOpened + 1), second);
                                if (j > 0) {
                                    if (equalsNumber(output.substring(j - 1, j))) {
                                        output = output.substring(0, j) + "x" + output.substring(j, output.length());
                                    }
                                }
                                opened = false;
                                stop = true;
                                Log.d("", "text2= " + output);
                            } else {
                                error = true;
                            }
                        } else {
                            error = true;
                        }
                    }
                }
            }
            return output;
        }


        private String secondStep(String input){
            initialize(input);
            String output = input;
            for (i = 1; i < output.length(); i++) {
                if (!error) {
                    String letter = output.substring(i, i + 1);

                    if (letter.equals("x")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "x";
                        } else {
                            output = reduce(output);
                            Log.e("", "firstNum= " + firstNumber);
                        }
                    } else if (letter.equals("%") || letter.equals("/")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "/";
                        } else {
                            output = reduce(output);
                        }
                    } else if (letter.equals("+") || letter.equals("-")) {

                        if (whichNumber == 2) {
                            output = reduce(output);
                        } else {
                            firstNumber = "";
                            reset();
                            if (i < output.length()) {
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
                    i = output.length();
                }
            }
            return finalizeStep(output);
        }

        private String thirdStep(String input){
            initialize(input);
            String output = input;
            for (i = 1; i < output.length(); i++) {
                if(!error) {
                    String letter = output.substring(i, i + 1);

                    if (letter.equals("+")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "+";
                        } else {
                            output = reduce(output);
                        }
                    } else if (letter.equals("-")) {
                        if (whichNumber == 1) {
                            whichNumber = 2;
                            operation = "-";
                        } else {
                            output = reduce(output);
                        }
                    } else
                    {
                        readNumber(letter);
                    }
                }
            }
            return finalizeStep(output);
        }

        private String finalizeStep(String input){
            if(whichNumber == 2)
            {
                return reduce(input);
            }
            else
            {
                return input;
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
            if (equalsNumber(letter)) {
                if (whichNumber == 1) {
                    firstNumber += letter;
                } else if (whichNumber == 2) {
                    secondNumber += letter;
                }
            } else {
                error = true;
            }
        }

        private String reduce(String input){
            String output;

            if (initialPoint > 0) {
                output = input.substring(0,initialPoint);
            } else {
                output = "";
            }

            firstNumber = operate();
            Log.e("", "inside reduce= " + firstNumber);
            reset();
            output += firstNumber;

            if (i < input.length()){
                output += input.substring(i, input.length());
            }
            Log.e(LOG_TAG, "input= " + input);
            Log.e(LOG_TAG, "output= " + output);

            i = firstNumber.length() + initialPoint - 1;
            return output;
        }

        private String operate(){

            double output;
            if(!secondNumber.equals("") || secondNumber != null) {
                if (operation.equals("x")) {
                    output = Double.parseDouble(firstNumber) * Double.parseDouble(secondNumber);
                } else if (operation.equals("/")) {
                    if (Double.parseDouble(firstNumber) > EPSILON) {
                        output = Double.parseDouble(firstNumber) / Double.parseDouble(secondNumber);
                    } else {
                        error = true;
                        output = 0;
                    }
                } else if (operation.equals("+")) {
                    output = Double.parseDouble(firstNumber) + Double.parseDouble(secondNumber);
                } else if (operation.equals("-")) {
                    output = Double.parseDouble(firstNumber) - Double.parseDouble(secondNumber);
                } else {
                    output = 0;
                }
            } else {
                error = true;
                output = 0;
            }

            return Double.toString(output);
        }

    }
}
