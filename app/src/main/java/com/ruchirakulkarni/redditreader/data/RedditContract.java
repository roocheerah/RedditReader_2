package com.ruchirakulkarni.redditreader.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abhay on 9/14/14.
 */
public class RedditContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.ruchirakulkarni.redditreader";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_REDDIT = "reddit";

    public static final class RedditPostEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REDDIT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_REDDIT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_REDDIT;

        public static final String TABLE_NAME = "reddit";

        public static final String COL_SUBREDDIT_TYPE = "type";

        public static final String COL_PERMALINK = "permalink";

        public static final String COL_AUTHOR = "author";

        public static final String COL_TITLE = "title";

        public static final String COL_URL = "url";

        public static final String COL_COMMENTS = "comments";

        public static final String COL_SCORE = "score";

        public static Uri buildRedditUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}

