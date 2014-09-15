package com.ruchirakulkarni.redditreader.data;

import android.provider.BaseColumns;

/**
 * Created by abhay on 9/14/14.
 */
public class RedditContract {
    public static final class RedditPostEntry implements BaseColumns{

        public static final String TABLE_NAME = "reddit";

        public static final String COL_SUBREDDIT_TYPE = "type";

        public static final String COL_PERMALINK = "permalink";

        public static final String COL_AUTHOR = "author";

        public static final String COL_TITLE = "title";

        public static final String COL_URL = "url";

        public static final String COL_COMMENTS = "comments";

        public static final String COL_SCORE = "score";

    }
}
