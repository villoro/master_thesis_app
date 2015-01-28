package com.villoro.expensor_beta.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Arnau on 28/01/2015.
 */
public class ExpensorAuthenticatorService extends Service{

    private ExpensorAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ExpensorAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
