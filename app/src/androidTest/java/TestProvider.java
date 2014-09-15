package com.ruchirakulkarni.redditreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ruchirakulkarni.redditreader.data.RedditContract;
import com.ruchirakulkarni.redditreader.data.RedditDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by ruchirakulkarni on 9/15/14.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(RedditDbHelper.DATABASE_NAME);
    }

    static ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        int testScore = 200;
        String testAuthor = "Ruchira";
        String testSubRedditType = "funny";
        String testTitle = "Cool person right here";
        String testPermalink = "r/funny/cool";
        int testComments = 1000;
        String testUrl = "http://reddit.com";
        values.put(RedditContract.RedditPostEntry.COL_AUTHOR, testAuthor);
        values.put(RedditContract.RedditPostEntry.COL_SCORE, testScore);
        values.put(RedditContract.RedditPostEntry.COL_SUBREDDIT_TYPE, testSubRedditType);
        values.put(RedditContract.RedditPostEntry.COL_TITLE, testTitle);
        values.put(RedditContract.RedditPostEntry.COL_PERMALINK, testPermalink);
        values.put(RedditContract.RedditPostEntry.COL_COMMENTS, testComments);
        values.put(RedditContract.RedditPostEntry.COL_URL, testUrl);

        return values;
    }

    public void testInsertReadDb(){
        RedditDbHelper dbHelper = new RedditDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long redditRowId = db.insert(RedditContract.RedditPostEntry.TABLE_NAME, null, getContentValues());

        // Verify we got a row back.
        assertTrue(redditRowId != -1);
        Log.d(LOG_TAG, "New row id: " + redditRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                RedditContract.RedditPostEntry._ID,
                RedditContract.RedditPostEntry.COL_AUTHOR,
                RedditContract.RedditPostEntry.COL_TITLE,
                RedditContract.RedditPostEntry.COL_COMMENTS,
                RedditContract.RedditPostEntry.COL_SCORE,
                RedditContract.RedditPostEntry.COL_COMMENTS,
                RedditContract.RedditPostEntry.COL_URL,
                RedditContract.RedditPostEntry.COL_PERMALINK,
                RedditContract.RedditPostEntry.COL_SUBREDDIT_TYPE
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                RedditContract.RedditPostEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, getContentValues());
        db.close();

    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public void testGetType() {
        // content://com.ruchirakulkarni.redditreader/reddit/
        String type = mContext.getContentResolver().getType(RedditContract.RedditPostEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.ruchirakulkarni.redditreader/reddit/
        assertEquals(RedditContract.RedditPostEntry.CONTENT_TYPE, type);

        

    }
}
