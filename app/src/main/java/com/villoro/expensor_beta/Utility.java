package com.villoro.expensor_beta;

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static int[] getDate()
    {
        final Calendar c = Calendar.getInstance();
        return new int[]
                {c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)};
    }



    //TODO improve that
    public static String formatDoubleToSQLite(String amount)
    {
        return amount.replace(",", ".");
    }
}