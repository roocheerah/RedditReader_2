package com.ruchirakulkarni.redditreader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.ruchirakulkarni.redditreader.data.RedditContract;
import com.ruchirakulkarni.redditreader.data.RedditDbHelper;

/**
 * Created by ruchirakulkarni on 9/15/14.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(RedditDbHelper.DATABASE_NAME);
    }

    public void testInsertReadProvider(){

        ContentValues testValues = TestDb.getContentValues();
        Uri redditUri = mContext.getContentResolver().insert(RedditContract.RedditPostEntry.CONTENT_URI, testValues);


        long redditRowId = ContentUris.parseId(redditUri);
       // long redditRowId = db.insert(RedditContract.RedditPostEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(redditRowId != -1);
        //Log.d(LOG_TAG, "New row id: " + redditRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.RedditPostEntry.CONTENT_URI,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null // sort order
        );

        TestDb.validateCursor(cursor, testValues);

    }

    public void testGetType() {
        // content://com.ruchirakulkarni.redditreader/reddit/
        String type = mContext.getContentResolver().getType(RedditContract.RedditPostEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.ruchirakulkarni.redditreader/reddit/
        assertEquals(RedditContract.RedditPostEntry.CONTENT_TYPE, type);
    }
}
