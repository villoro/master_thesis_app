package com.villoro.expensor_beta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Arnau on 21/01/2015.
 */
public class Utility {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getDateUTC() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));

        return dateFormat.format(new Date());
    }

    public static String getDate() {
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(DATE_FORMAT);

        return dateFormatLocal.format(new Date());
    }
}
