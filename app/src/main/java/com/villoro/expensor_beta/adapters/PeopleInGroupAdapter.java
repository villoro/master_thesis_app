package com.villoro.expensor_beta.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.villoro.expensor_beta.R;

import java.util.ArrayList;

/**
 * Created by Arnau on 15/05/2015.
 */
public class PeopleInGroupAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> names;
    private final ArrayList<Long> ids;

    CommPeopleInGroup comm;

    TextView tv_name;

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

        tv_name = (TextView) rowView.findViewById(R.id.tv_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < ids.size()) {
                    Log.d("PeopleInGroupAdapter", "pos= " + position + ", id.size= " + ids.size());
                    names.remove(position);
                    ids.remove(position);

                    comm.resetArrayLists(names, ids);
                }
            }
        });

        if(!names.get(position).equals("")){
            tv_name.setText(names.get(position));
        }

        return rowView;
    }

    public void setCommunicator(CommPeopleInGroup comm)
    {
        this.comm = comm;

    }

    public interface CommPeopleInGroup{
        public void resetArrayLists(ArrayList<String> names, ArrayList<Long> ids);
    }
}
