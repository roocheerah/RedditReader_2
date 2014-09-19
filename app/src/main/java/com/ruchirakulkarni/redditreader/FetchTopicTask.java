package com.ruchirakulkarni.redditreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
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

/**
* Created by ruchirakulkarni on 9/15/14.
*/
public class FetchTopicTask extends AsyncTask<String, Void, String[]> {

    private ArrayAdapter<String> postTypeAdapter;
    private TopicFragment topicFragment;
    private final Context mContext;

    private final String LOG_TAG = FetchTopicTask.class.getSimpleName();

    public FetchTopicTask(Context context, ArrayAdapter<String> adapter, TopicFragment topicFragment){
        this.topicFragment = topicFragment;
        mContext = context;
        postTypeAdapter = adapter;

    }
    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //This will contain the raw JSON String response as a String
        String redditJsonStr = null;
        String topic = params[0];

        if(!topic.equals("")){
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

    private String[] getSubRedditDataFromJSON(String redditJsonStr) throws JSONException{

        //these are the names of the JSON objects that need to be extracted
        final String R_AUTHOR = "author";
        final String R_TITLE = "title";
        final String R_PERMALINK = "permalink"; //the links to the comments on that article
        final String R_URL = "url";
        final String R_SUBREDDIT = "subreddit";
        final String R_DATA = "data";
        final String R_CHILDREN = "children";

        String[] resultStr = new String[10];

        JSONObject redditJson = new JSONObject(redditJsonStr);
        JSONObject dataObject = redditJson.getJSONObject(R_DATA);
        JSONArray children = dataObject.getJSONArray(R_CHILDREN);

        for (int i = 0; i < 10 ; i++) {
            JSONObject dummy = children.getJSONObject(i);
            JSONObject data = dummy.getJSONObject(R_DATA);
            String author = data.getString(R_AUTHOR);
            String title = data.getString(R_TITLE);
            String permalink = data.getString(R_PERMALINK);
            topicFragment.STRING_URL = data.getString(R_URL);
            String subreddit = data.getString(R_SUBREDDIT);

            resultStr[i] = "Title: " + title + " by " + author;

        }

            for (String s : resultStr){
                Log.v(LOG_TAG, "Topic Entry " + s);
            }
//            for (String s : resultStr){
//                Log.v(LOG_TAG, "Topic Entry " + s);
//            }


        return resultStr;
    }


    private void addPost(String topicSetting, String title, String author, int comments, int score) {

        Log.v(LOG_TAG, "inserting post " + title + " by " + author + " has comments = "+ comments + " and score of " + score);

        // First, check if the location with this city name exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.RedditPostEntry.CONTENT_URI,
                new String[]{RedditContract.RedditPostEntry._ID},
                RedditContract.RedditPostEntry.COL_TITLE + " = ?",
                new String[]{},
                null);

        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG, "Found it in the database!");
            int locationIdIndex = cursor.getColumnIndex(RedditContract.RedditPostEntry._ID);
//            return cursor.getLong(locationIdIndex);
        } else {
            Log.v(LOG_TAG, "Didn't find it in the database, inserting now!");
            ContentValues locationValues = new ContentValues();
//            locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
//            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
//            locationValues.put(LocationEntry.COLUMN_COORD_LAT, lat);
//            locationValues.put(LocationEntry.COLUMN_COORD_LONG, lon);

//            Uri locationInsertUri = mContext.getContentResolver()
//                    .insert(LocationEntry.CONTENT_URI, locationValues);

//            return ContentUris.parseId(locationInsertUri);
        }
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(result != null){
            postTypeAdapter.clear();
            for(String post : result){
//                    Log.d(LOG_TAG, " this post is now being added: " + post);
                postTypeAdapter.add(post);
            }
        }

    }
}
