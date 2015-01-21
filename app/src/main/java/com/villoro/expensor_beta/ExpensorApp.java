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


    }
}
