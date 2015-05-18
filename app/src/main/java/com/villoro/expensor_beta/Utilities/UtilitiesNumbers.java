package com.villoro.expensor_beta.Utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseUser;
import com.villoro.expensor_beta.data.ExpensorContract;
import com.villoro.expensor_beta.data.Tables;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Arnau on 12/05/2015.
 */
public class UtilitiesNumbers {

    //------------------------------------------- VALUES UTILITIES ---------------------------------

    public static String getFancyDouble(double amount){
        String output = "";

        output = "" + round(amount, 2);

        if(output.substring(output.length()-2, output.length()-1).equals(".")){
            output += "0";
        }

        return output;
    }

    public static double round(double amount, int decimals)
    {
        if (decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int getAdapterPositionById(Adapter adapter, long id)
    {
        final int count = adapter.getCount();
        for( int pos = 0; pos < count ; pos++)
        {
            if (id == adapter.getItemId(pos))
            {
                return pos;
            }
        }
        return 0;
    }

    public static long getMyId(Context context){
        Cursor cursor = context.getContentResolver().query(ExpensorContract.PeopleEntry.PEOPLE_URI, null,
                Tables.EMAIL + " = '" + ParseUser.getCurrentUser().getEmail() + "'", null, null);
        if(cursor.moveToFirst()){
            return cursor.getLong(cursor.getColumnIndex(Tables.ID));
        } else {
            return -1;
        }
    }

    public static long getIdFromUri(Uri uri) {
        return ContentUris.parseId(uri);
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
}
