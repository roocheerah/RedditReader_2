package com.ruchirakulkarni.redditreader.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ruchirakulkarni on 9/18/14.
 */
public class RedditReaderSyncService extends Service {
    private static final Object rSyncAdapterLock = new Object();
    private static RedditReaderSyncAdapter rSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("RedditReaderService", "onCreate - RedditReaderSyncService");
        synchronized (rSyncAdapterLock) {
            if (rSyncAdapter == null) {
                rSyncAdapter = new RedditReaderSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return rSyncAdapter.getSyncAdapterBinder();
    }
}
