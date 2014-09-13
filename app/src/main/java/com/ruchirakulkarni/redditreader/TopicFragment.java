package com.ruchirakulkarni.redditreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ruchirakulkarni on 9/13/14.
 */
public class TopicFragment extends Fragment{

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
            FetchTopicTask topicTask = new FetchTopicTask();
            topicTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

             ListView listView = (ListView) rootView.findViewById(R.id.listview_detailactivity1);

             return rootView;
         }
         return null;
    }

    public class FetchTopicTask extends AsyncTask<Void, Void, Void> {

        public FetchTopicTask(Void... params){

        }
        @Override
        protected Void doInBackground(Void... voids) {
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
                System.out.println(redditJsonStr);
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
                return null;
            }
        }
    }
}

