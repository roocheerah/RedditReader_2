package com.ruchirakulkarni.redditreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RedditPostType())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    public static class RedditPostType extends Fragment {

        public RedditPostType() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ArrayList<String> types = new ArrayList<String>();
            types.add("Hot");
            types.add("New");
            types.add("Rising");
            types.add("Controversial");
            types.add("Top");


            final ArrayAdapter<String> subredditTypeAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.list_item_reddit_type, R.id.list_item_reddit_type_textview, types);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_type);
            listView.setAdapter(subredditTypeAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
               @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l){
                   Context context = getActivity();
                   //you should replace this toast with an intent connected to the DetailActivity1.java class
                   String post = subredditTypeAdapter.getItem(index);
                   Intent intentToDetailActivity1 = new Intent(getActivity(), DetailActivity1.class)
                           .putExtra(Intent.EXTRA_TEXT, post);
                   startActivity(intentToDetailActivity1);
               }
            });

                return rootView;
            }
        }
    }


