package com.villoro.expensor_beta.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Arnau on 28/01/2015.
 */
public class ExpensorSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static ExpensorSyncAdapter sExpensorSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("ExpensorSyncService", "onCreate - ExpensorSyncService");
        synchronized (sSyncAdapterLock) {
            if (sExpensorSyncAdapter == null) {
                sExpensorSyncAdapter = new ExpensorSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sExpensorSyncAdapter.getSyncAdapterBinder();
    }
}
