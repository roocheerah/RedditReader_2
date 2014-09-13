package com.ruchirakulkarni.redditreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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


public class DetailActivity1 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity1);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_activity1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail_activity1, container, false);

            //NOW WE NEED TO RECEIVE THE INTENT FROM THE MAINACTIVITY

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String data = intent.getStringExtra(Intent.EXTRA_TEXT);
                //This is setting the textview in the detailactivity1 to the item that was clicked
                //we need to return a listview of possible post options instead of a textview
                //for this, you should go back to the detail1_fragment.xml layout file and substitute
                //the textview with a listview item which then contains things like the the list of posts
                //to be displayed on the page. For that, you should change your URL information according to the
                //String data that was passed through. Then you need to launch another detail activity page
                //with an intent sent from this activity afer a particular post has been selected from that place

                ListView listView = (ListView) rootView.findViewById(R.id.listview_detailactivity1);


                //THINK ABOUT WHETHER YOU REALLY NEED TO ESTABLISH A NETWORK CONNECTION HERE BECAUSE ALL
                //OF THE MAIN ACTIVITY LIST ITEMS ARE CONSTANT. YOU MIGHT NEED ONE ONLY IN GETTING THE SUBREDDITS
                //RELATED TO THESE ON ONCLICK.

                //Now I need to make the connection to the reddit API that is online
                //I need to make a HttpUrl Connection to achieve this

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                //This will contain the raw JSON String response as a String
                String redditJsonStr = null;
                try {
                    //construct the url for the entry of the reddit API
                    String tempURL = "http://www.reddit.com/r/subreddit/" + data + ".json";
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
                    return rootView;
                }
            }
            return null;
        }
    }
}
