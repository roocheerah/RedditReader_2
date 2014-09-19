package com.ruchirakulkarni.redditreader;

/**
 * Created by ruchirakulkarni on 9/19/14.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
    public static String getPreferredTopic(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_topic_key),
                context.getString(R.string.pref_topic_default));
    }
}
