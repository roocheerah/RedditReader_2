package com.ruchirakulkarni.redditreader;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ruchirakulkarni.redditreader.data.RedditContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
* Created by ruchirakulkarni on 9/15/14.
*/
public class FetchTopicTask extends AsyncTask<String, Void, String[]> {

    private static final boolean DEBUG = true ;
    private TopicFragment topicFragment;
    private final Context mContext;

    private final String LOG_TAG = FetchTopicTask.class.getSimpleName();

    public FetchTopicTask(Context context, TopicFragment topicFragment){
        this.topicFragment = topicFragment;
        mContext = context;

    }


    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //This will contain the raw JSON String response as a String
        String redditJsonStr = null;
        String topic = params[0];

        if(!topic.equals("")){
            //NEED TO DEBUG HERE BECAUSE THE URL RETURNED IS NOT CORRECT...
            topicFragment.data = "r/" + topic + "/" + topicFragment.data;
        }

        Log.d(LOG_TAG, "The data is " + topicFragment.data);

        try {
            //construct the url for the entry of the reddit API
            String tempURL = "http://www.reddit.com/" + topicFragment.data + "/.json";
            System.out.println(tempURL);
            URL url = new URL(tempURL);

            // Create the request to Reddit API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                redditJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                redditJsonStr = null;
            }

            redditJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Topic JSON String " + redditJsonStr);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            redditJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }

            try {
                return getSubRedditDataFromJSON(redditJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private String[] getSubRedditDataFromJSON(String redditJsonStr) throws JSONException{

        //these are the names of the JSON objects that need to be extracted
        final String R_AUTHOR = "author";
        final String R_TITLE = "title";
        final String R_PERMALINK = "permalink"; //the links to the comments on that article
        final String R_URL = "url";
        final String R_SUBREDDIT = "subreddit";
        final String R_DATA = "data";
        final String R_CHILDREN = "children";
        final String R_COMMENT = "num_comments";
        final String R_SCORE = "score";

        String[] resultStr = new String[10];
        Vector<ContentValues> cVVector = new Vector<ContentValues>(10);

        JSONObject redditJson = new JSONObject(redditJsonStr);
        JSONObject dataObject = redditJson.getJSONObject(R_DATA);
        JSONArray children = dataObject.getJSONArray(R_CHILDREN);

        for (int i = 0; i < 10 ; i++) {

            ContentValues postValues = new ContentValues();

            JSONObject dummy = children.getJSONObject(i);
            JSONObject data = dummy.getJSONObject(R_DATA);
            String author = data.getString(R_AUTHOR);
            String title = data.getString(R_TITLE);
            String permalink = data.getString(R_PERMALINK);
            topicFragment.STRING_URL = data.getString(R_URL);
            String subreddit = data.getString(R_SUBREDDIT);
            int comments = data.getInt(R_COMMENT);
            int score = data.getInt(R_SCORE);


            postValues.put(RedditContract.RedditPostEntry.COL_TITLE, title);
            postValues.put(RedditContract.RedditPostEntry.COL_AUTHOR, author);
            postValues.put(RedditContract.RedditPostEntry.COL_COMMENTS, comments );
            postValues.put(RedditContract.RedditPostEntry.COL_PERMALINK, permalink);
            postValues.put(RedditContract.RedditPostEntry.COL_SCORE, score);
            postValues.put(RedditContract.RedditPostEntry.COL_SUBREDDIT_TYPE, subreddit);
            postValues.put(RedditContract.RedditPostEntry.COL_URL, topicFragment.STRING_URL);

            cVVector.add(postValues);

            resultStr[i] = "Title: " + title + " by " + author;
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = mContext.getContentResolver()
                    .bulkInsert(RedditContract.RedditPostEntry.CONTENT_URI, cvArray);
            for (ContentValues s : cvArray) {
                Log.v(LOG_TAG, "The rows inserted are : " + s.toString());
            }
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");
            // Use a DEBUG variable to gate whether or not you do this, so you can easily
            // turn it on and off, and so that it's easy to see what you can rip out if
            // you ever want to remove it.
//            if (DEBUG) {
//                Cursor redditCursor = mContext.getContentResolver().query(
//                        RedditContract.RedditPostEntry.CONTENT_URI,
//                        null,
//                        null,
//                        null,
//                        null
//                );
//
//                Log.d("redditCursor has the value of", redditCursor.toString());
//
//                if (redditCursor.moveToFirst()) {
//                    ContentValues resultValues = new ContentValues();
//                    DatabaseUtils.cursorRowToContentValues(redditCursor, resultValues);
//                    Log.v(LOG_TAG, "Query succeeded! **********");
//                    for (String key : resultValues.keySet()) {
//                        Log.v(LOG_TAG, key + ": " + resultValues.getAsString(key));
//                    }
//                } else {
//                    Log.v(LOG_TAG, "Query failed! :( **********");
//                }
//            }
        }
        return resultStr;
    }
}
