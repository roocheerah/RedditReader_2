package com.ruchirakulkarni.redditreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ruchirakulkarni on 9/13/14.
 */
public class TopicFragment extends Fragment{

    private ArrayAdapter<String> postTypeAdapter;
    private String[] posts;
    public static String data = "all";
    public static String STRING_URL = "";
    private final String LOGG_TAG = TopicFragment.class.getSimpleName();

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
        posts = new String[10];
        FetchTopicTask topicTask = new FetchTopicTask(getActivity(), postTypeAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Log.d(LOGG_TAG, "The default topic is " + getString(R.string.pref_topic_default));
        String topic = prefs.getString(getString(R.string.pref_topic_key), getString(R.string.pref_topic_default));
//        Log.d(LOGG_TAG, "The topic key is:" + getString(R.string.pref_topic_key));
//        Log.d(LOGG_TAG, "The String topic is : " + topic);
        topicTask.execute(topic);
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

//             Log.d(LOGG_TAG, "The postAdapter is: " + postTypeAdapter.toString());
             listView.setAdapter(postTypeAdapter);

             listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                     String subReddit = postTypeAdapter.getItem(position);
                     Log.v(LOGG_TAG, "The URL being passed to the web Browser is " + STRING_URL);
                     String url = STRING_URL;
                     Intent i = new Intent(Intent.ACTION_VIEW);
                     i.setData(Uri.parse(url));
                     startActivity(i);
                 }
             });

             return rootView;
         }
         return null;
    }
}


