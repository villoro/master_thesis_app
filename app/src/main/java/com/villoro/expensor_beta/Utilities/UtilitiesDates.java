package com.villoro.expensor_beta.Utilities;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Arnau on 21/01/2015.
 */
public class UtilitiesDates {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    //------------------------------------------- DATE UTILITIES -----------------------------------
    //Used when saving a data
    public static String getStringFromDateUTC(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        return dateFormat.format(date);
    }

    //String format "yyyy-MM-dd HH:mm:ss";
    public static int[] dateFromString(String date)
    {
        String[] aux = {"","","","","",""};
        int size = 5;
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

    public static String getFancyDate(int[] date)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(Integer.toString(date[2])).append("-");
        sb.append(numberTo2values(date[1])).append("-");
        sb.append(numberTo2values(date[0]));

        return sb.toString();
    }

    public static String getFancyDate(String date)
    {
        int[] aux = dateFromString(date);

        StringBuilder sb = new StringBuilder();
        sb.append(aux[2]).append("-");
        sb.append(aux[1]).append("-");
        sb.append(aux[0]);

        return sb.toString();
    }


    public static String numberTo2values(int date){
        if(date <= 9){
            return "0" + Integer.toString(date);
        } else {
            return Integer.toString(date);
        }
    }

    public static int[] getDate()
    {
        final Calendar c = Calendar.getInstance();
        return new int[]
                {c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)};
    }

    public static String getFirstDay(int year, int month){
        int[] output = new int[]{year, month, 1, 00, 00, 00};
        return completeDateToString(output);
    }

    public static String getLastDay(int year, int month){
        Calendar mCal = new GregorianCalendar(year, month, 1);
        int[] output = new int[]{year, month, 1, 23, 59, 59};
        output[2] = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return completeDateToString(output);
    }

    public static int[] reduceMonth(int[] date){
        int[] output = date;
        if(date[1] == 1){
            date[1] = 12;
            date[0]--;
        } else {
            date[1]--;
        }
        return output;
    }

    public static int[] incrementMonth(int[] date){
        int[] output = date;
        if(date[1] == 12){
            date[1] = 1;
            date[0]++;
        } else {
            date[1]++;
        }
        return output;
    }

    public static String setFancyMonthName(int[] date){
        StringBuilder sb = new StringBuilder();

        sb.append(date[1]).append(" - ").append(date[0]); //TODO

        return sb.toString();
    }



    //TODO improve that
    public static String formatDoubleToSQLite(String amount)
    {
        return amount.replace(",", ".");
    }
}