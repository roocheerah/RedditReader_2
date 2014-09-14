package com.ruchirakulkarni.redditreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.widget.AdapterView;
/**
 * Created by ruchirakulkarni on 9/13/14.
 */
public class TopicFragment extends Fragment{

    private ArrayAdapter<String> postTypeAdapter;
    String data;

    public TopicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_activity1, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updatePosts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePosts() {
        FetchTopicTask topicTask = new FetchTopicTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String topic = prefs.getString(getString(R.string.pref_topic_key),
                getString(R.string.pref_topic_default));
        topicTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View rootView = inflater.inflate(R.layout.fragment_detail_activity1, container, false);

         //NOW WE NEED TO RECEIVE THE INTENT FROM THE MAINACTIVITY

         Intent intent = getActivity().getIntent();
         if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            data = intent.getStringExtra(Intent.EXTRA_TEXT);
             data = data.toLowerCase();

             postTypeAdapter = new ArrayAdapter<String>(
                     getActivity(), R.layout.list_item_post_textview, R.id.list_item_post_textview, new ArrayList<String>());
             ListView listView = (ListView) rootView.findViewById(R.id.listview_detailactivity1);
             listView.setAdapter(postTypeAdapter);

             listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                     String subReddit = postTypeAdapter.getItem(position);
                     Intent intent = new Intent(getActivity(), DetailActivity2.class)
                             .putExtra(Intent.EXTRA_TEXT, subReddit);
                     startActivity(intent);
                 }
             });

             return rootView;
         }
         return null;
    }

    public class FetchTopicTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchTopicTask.class.getSimpleName();

        public FetchTopicTask(Void... params){

        }
        @Override
        protected String[] doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //This will contain the raw JSON String response as a String
            String redditJsonStr = null;
            try {
            //construct the url for the entry of the reddit API
               String tempURL = "http://www.reddit.com/r/subreddit/" + data + "/.json";
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
                String url = data.getString(R_URL);
                String subreddit = data.getString(R_SUBREDDIT);

                resultStr[i] = "Title: " + title + " by " + author;

            }

            for (String s : resultStr){
                Log.v(LOG_TAG, "Topic Entry " + s);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                postTypeAdapter.clear();
                for(String post : result){
                    postTypeAdapter.add(post);
                }
            }
        }
    }
}

