package com.villoro.expensor_beta.sections.add_or_update;

/**
 * Created by Arnau on 01/03/2015.
 */
public interface AddOrUpdateInterface {

    public boolean add();
    public boolean valuesAreCorrect();
    public void initialize(long whichID);
    public void setValues();
    public void delete();
}
