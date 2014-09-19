package com.ruchirakulkarni.redditreader.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ruchirakulkarni on 9/18/14.
 */
public class RedditReaderAuthenticationService extends Service{

    private RedditReaderAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new RedditReaderAuthenticator(this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
