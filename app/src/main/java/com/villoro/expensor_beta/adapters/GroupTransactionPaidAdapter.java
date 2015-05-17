package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 17/05/2015.
 */
public class GroupTransactionPaidAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private final Context context;
    CommPaid commPaid;

    public GroupTransactionPaidAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);

        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_who_paid, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));

        final int internalPosition = cursor.getPosition();
        final EditText et_amount = (EditText) view.findViewById(R.id.et_amount);
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double amount = 0;
                String textAmount = et_amount.getText().toString();
                if (textAmount != null && textAmount.length() > 0){
                    amount = Double.parseDouble(textAmount);
                }
                commPaid.changePaid(internalPosition, amount);
            }
        });
    }

    public void setCommPaid(CommPaid commPaid){
        this.commPaid = commPaid;
    }

    public interface CommPaid{
        public void changePaid(int position, double amount);
    }
}
