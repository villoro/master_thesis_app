package com.villoro.expensor_beta;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

/**
 * Created by Arnau on 21/01/2015.
 */
public class ExpensorApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        //Link a parse.com/expensor_alfa
        Parse.initialize(this, "CVyaV0ibsxsi0tUm2VBZALUttuGJeixhqCPYPyXM", "oI5C3b9Le5vzZHNrt8XceFvlGIY2BmDXkUkog2zk");

        //Link a parse.com/expensor_beta
        //Parse.initialize(this, "Ui3h6McHif4OMiHsDQemy6mEw0LfttcF7Xr4ATpx", "1KVMEGr0MBr8g7rqPttYyFV9zStjpC6C9ljtWxCZ");


    }
}
