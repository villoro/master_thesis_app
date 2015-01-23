package com.villoro.expensor_beta;

import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String getStringFromActualDate() {
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(DATE_FORMAT);

        return dateFormatLocal.format(new Date());
    }

    //Used to show a read date
    public static String getStringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    //Used when saving a data
    public static String getStringFromDateUTC(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        return dateFormat.format(date);
    }

    public static Date getDateUTCFromString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
