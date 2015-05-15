package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.villoro.expensor_beta.R;
import com.villoro.expensor_beta.data.ExpensorContract;

import java.util.ArrayList;

/**
 * Created by Arnau on 15/05/2015.
 */
public class PeopleInGroupAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> names;
    private final ArrayList<Long> ids;

    CommPeopleInGroup comm;

    AutoCompleteTextView autoComplete;

    public PeopleInGroupAdapter(Context context, ArrayList<String> names, ArrayList<Long> ids){
        super(context, R.layout.row_people_in_group, names);

        this.context = context;
        this.names = names;
        this.ids = ids;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_people_in_group, parent, false);

        autoComplete = (AutoCompleteTextView) rowView.findViewById(R.id.ac_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                names.remove(position);
                ids.remove(position);
                comm.resetArrayLists(names, ids);
            }
        });

        if(!names.get(position).equals("")){
            autoComplete.setText(names.get(position));
        }

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("PeopleInGroupAdapter", "text= " + s.toString());
                setFromCursor(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                names.add(position, autoComplete.getText().toString().trim());
                ids.add(id);
                names.add("");
                comm.resetArrayLists(names, ids);
            }
        });

        return rowView;
    }

    public void setCommunicator(CommPeopleInGroup comm)
    {
        this.comm = comm;

    }

    public interface CommPeopleInGroup{
        public void resetArrayLists(ArrayList<String> names, ArrayList<Long> ids);
    }

    public void setFromCursor(String partName){
        Cursor cursor = context.getContentResolver().query(
                ExpensorContract.PeopleEntry.buildFromPartOfNameUri(partName), null, null, null, null);
        PeopleAdapter peopleAdapter = new PeopleAdapter(context, cursor, 0);

        Log.d("PeopleInGroupAdapter", "cursor count= " + cursor.getCount());

        autoComplete.setAdapter(peopleAdapter);
    }
}
