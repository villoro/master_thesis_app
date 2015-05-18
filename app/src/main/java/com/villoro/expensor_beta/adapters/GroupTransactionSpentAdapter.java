package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.Tables;

/**
 * Created by Arnau on 17/05/2015.
 */
public class GroupTransactionSpentAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private final Context context;

    private final String LOCK_SYMBOL = "\uD83D\uDD12";
    private final String PLUS_SYMBOL = "+";
    private final String MINUS_SYMBOL = "-";

    CommSpent commSpent;

    TextView[] tv_weight;
    Button[] buttonsPlus, buttonsMinus;

    public GroupTransactionSpentAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);

        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tv_weight = new TextView[c.getCount()];
        buttonsPlus = new Button[c.getCount()];
        buttonsMinus = new Button[c.getCount()];
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.row_who_spent, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(Tables.NAME)));

        final int position = cursor.getPosition();

        buttonsPlus[position] = (Button) view.findViewById(R.id.b_plus);
        buttonsMinus[position] = (Button) view.findViewById(R.id.b_minus);

        tv_weight[position] = (TextView) view.findViewById(R.id.tv_number);


        buttonsPlus[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commSpent.isLocked(position)) {
                    int weight = Integer.parseInt(tv_weight[position].getText().toString());
                    weight += 1;
                    tv_weight[position].setText("" + weight);
                    commSpent.divideSpent();
                } else {
                    toastAboutLockedButton();
                }
            }
        });

        buttonsMinus[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commSpent.isLocked(position)) {
                    int weight = Integer.parseInt(tv_weight[position].getText().toString());
                    if (weight >= 1) {
                        weight -= 1;
                        tv_weight[position].setText("" + weight);
                        commSpent.divideSpent();
                    }
                } else {
                    toastAboutLockedButton();
                }
            }
        });

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
                if(et_amount.isFocused()) {
                    String textAmount = et_amount.getText().toString();

                    if (textAmount != null && textAmount.length() > 0) {
                        lockPerson(position, textAmount, et_amount);
                    } else {
                        unlockPerson(position, et_amount);
                    }
                    Log.d("", "textAmount= " + textAmount + " ,locked?= " + commSpent.isLocked(position));
                }
            }
        });
    }

    public void lockPerson(int internalPosition, String textAmount, EditText editText){
        if(commSpent.lockPerson(internalPosition, Double.parseDouble(textAmount))) {
            commSpent.divideSpent();
            editText.setTextColor(Color.parseColor("#808080"));

            buttonsPlus[internalPosition].setText(LOCK_SYMBOL);
            buttonsMinus[internalPosition].setText(LOCK_SYMBOL);
        }
    }

    public void unlockPerson(int internalPosition, EditText editText){
        commSpent.unlockPerson(internalPosition);
        editText.setTextColor(Color.BLACK);

        buttonsPlus[internalPosition].setText(PLUS_SYMBOL);
        buttonsMinus[internalPosition].setText(MINUS_SYMBOL);
    }

    public void setCommSpent(CommSpent commSpent){
        this.commSpent = commSpent;
    }

    public interface CommSpent{
        public boolean lockPerson(int position, double amount);
        public void unlockPerson(int position);
        public void divideSpent();
        public boolean isLocked(int position);
    }

    public void toastAboutLockedButton(){
        Toast toast = Toast.makeText(context, "Està bloquejat, elimina la seva quantitat si vols assignar un pes", Toast.LENGTH_LONG);
        toast.show();
    }
}
