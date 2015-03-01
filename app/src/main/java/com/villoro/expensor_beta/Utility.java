package com.villoro.expensor_beta;

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

    public static String getStringFromActualDateUTC() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));

        return dateFormat.format(new Date());
    }

    /*public static String getStringFromActualDate() {
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(DATE_FORMAT);

        return dateFormatLocal.format(new Date());
    }*/

    //Used when saving a data
    public static String getStringFromDateUTC(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        return dateFormat.format(date);
    }

    /*public static Date getDateUTC(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }*/


    //----------------------------- NEEDS WORK ------------------------------
    //TODO improve that
    public static int[] dateFromString(String date)
    {
        String[] aux = {"","",""};
        int[] output = new int[3];
        int i = 0;

        while(date.length()>0 && i < 2)
        {
            String letter = date.substring(0,1);
            date = date.substring(1, date.length());
            if(letter.equals("/"))
            {
                output[i] = Integer.parseInt(aux[i]);
                i++;
            }
            else
            {
                aux[i] += letter;
            }
        }
        output[2] = Integer.parseInt(date);
        return output;
    }

    //TODO improve that
    public static String dateToString(int[] date)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(date[0])).append("/");
        sb.append(Integer.toString(date[1])).append("/");
        sb.append(Integer.toString(date[2]));
        return sb.toString();
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