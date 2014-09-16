package com.ruchirakulkarni.redditreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abhay on 9/14/14.
 */
public class RedditDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reddit.db";
    private static final int DATABASE_VERSION = 1;

    public RedditDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_REDDIT_TABLE = "CREATE TABLE " + RedditContract.RedditPostEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                RedditContract.RedditPostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                RedditContract.RedditPostEntry.COL_SCORE + " INTEGER NOT NULL, " +
                RedditContract.RedditPostEntry.COL_AUTHOR + " TEXT NOT NULL, " +
                RedditContract.RedditPostEntry.COL_SUBREDDIT_TYPE + " TEXT NOT NULL, " +
                RedditContract.RedditPostEntry.COL_TITLE + " TEXT NOT NULL," +
                RedditContract.RedditPostEntry.COL_PERMALINK + " TEXT NOT NULL, " +
                RedditContract.RedditPostEntry.COL_COMMENTS + " INTEGER NOT NULL, " +
                RedditContract.RedditPostEntry.COL_URL + " TEXT NOT NULL, " +

                " UNIQUE (" + RedditContract.RedditPostEntry.COL_TITLE + ", " +
                RedditContract.RedditPostEntry.COL_AUTHOR + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REDDIT_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RedditContract.RedditPostEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


