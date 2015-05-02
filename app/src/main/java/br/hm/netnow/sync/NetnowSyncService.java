package br.hm.netnow.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by helmutmigge on 01/05/2015.
 */

public class NetnowSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NetnowSyncAdapter sNetnowSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sNetnowSyncAdapter == null) {
                sNetnowSyncAdapter = new NetnowSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sNetnowSyncAdapter.getSyncAdapterBinder();
    }
}