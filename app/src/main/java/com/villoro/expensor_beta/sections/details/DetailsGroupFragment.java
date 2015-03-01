package com.villoro.expensor_beta.sections.details;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.villoro.expensor_beta.dialogs.DialogLongClickList;
import com.villoro.expensor_beta.dialogs.DialogOkCancel;

/**
 * Created by Arnau on 01/03/2015.
 */
public class DetailsGroupFragment extends Fragment implements DialogLongClickList.CommGetChoice, DialogOkCancel.CommOkCancel{

    ListView listView;
    Context context;
    long listID;

    public DetailsGroupFragment(){};


    @Override
    public void getChoice(int choice) {

    }

    @Override
    public void ifOkDo(boolean ok, int whichCase) {

    }
}
