package com.villoro.expensor_beta;

import android.util.Log;

import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Arnau on 21/01/2015.
 */
public class Utility {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Used when saving a data
    public static String getStringFromDateUTC(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        return dateFormat.format(date);
    }


    //String format "yyyy-MM-dd HH:mm:ss";
    private static int[] dateFromString(String date, int to)
    {
        String[] aux = {"","","","","",""};
        int size = to;
        int[] output = new int[size + 1];
        int i = 0;
        while(date.length()>0 && i < size)
        {
            String letter = date.substring(0,1);
            date = date.substring(1, date.length());
            if(letter.equals("-") || letter.equals(" ") || letter.equals(":"))
            {
                output[i] = Integer.parseInt(aux[i]);
                i++;
            }
            else
            {
                aux[i] += letter;
            }
        }
        output[size] = Integer.parseInt(date);
        return output;
    }

    public static int[] onlyDateFromString(String date){
        return dateFromString(date, 2);
    }

    public static int[] completeDateFromString(String date){
        return dateFromString(date, 5);
    }

    public static String completeDateToString(int[] date)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(Integer.toString(date[0])).append("-");
        sb.append(numberTo2values(date[1])).append("-");
        sb.append(numberTo2values(date[2])).append(" ");
        sb.append(numberTo2values(date[3])).append(":");
        sb.append(numberTo2values(date[4])).append(":");
        sb.append(numberTo2values(date[5]));

        return sb.toString();
    }

    public static String dateOnlyToString(int[] date)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(Integer.toString(date[0])).append("-");
        sb.append(numberTo2values(date[1])).append("-");
        sb.append(numberTo2values(date[2]));

        return sb.toString();
    }

    public static String timeOnlyToString(int[] date)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(numberTo2values(date[0])).append(":");
        sb.append(numberTo2values(date[1])).append(":");
        sb.append(numberTo2values(date[2]));

        return sb.toString();
    }

    public static String numberTo2values(int date){
        if(date <= 9){
            return "0" + Integer.toString(date);
        } else {
            return Integer.toString(date);
        }
    }

    //TODO improve that
    public static int[] getDate()
    {
        final Calendar c = Calendar.getInstance();
        return new int[]
                {c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)};
    }

    //TODO improve that
    public static String formatDoubleToSQLite(String amount)
    {
        return amount.replace(",", ".");
    }
}