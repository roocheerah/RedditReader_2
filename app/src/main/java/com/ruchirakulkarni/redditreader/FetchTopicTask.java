package com.ruchirakulkarni.redditreader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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

    private final String LOG_TAG = FetchTopicTask.class.getSimpleName();
    private ArrayAdapter<String> postTypeAdapter;
    private final Context mContext;
    private String data;
    private String STRING_URL = TopicFragment.STRING_URL;

    public FetchTopicTask(Context context, ArrayAdapter <String> adapter) {
       mContext = context;
       postTypeAdapter = adapter;
       data = TopicFragment.data;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //This will contain the raw JSON String response as a String
        String redditJsonStr = null;
        String topic = params[0];
        data = "r/" + topic + "/" + data;
        Log.d(LOG_TAG, "The data is " + data);

        try {
            //construct the url for the entry of the reddit API
            String tempURL = "http://www.reddit.com/" + data + "/.json";
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
            STRING_URL = data.getString(R_URL);
            String subreddit = data.getString(R_SUBREDDIT);

            resultStr[i] = "Title: " + title + " by " + author;

        }

//            for (String s : resultStr){
//                Log.v(LOG_TAG, "Topic Entry " + s);
//            }

        return resultStr;
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
