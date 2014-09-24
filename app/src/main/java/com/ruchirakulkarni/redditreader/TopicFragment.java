package com.ruchirakulkarni.redditreader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ruchirakulkarni.redditreader.data.RedditContract;

/**
 * Created by ruchirakulkarni on 9/13/14.
 */
public class TopicFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static String STRING_URL = "";
    String data;
    private final String LOGG_TAG = TopicFragment.class.getSimpleName();
    private SimpleCursorAdapter postTypeAdapter;
    private static final int POST_LOADER = 0;
    private String mTopic;


    private static final String[] POST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the reddit table

            RedditContract.RedditPostEntry.TABLE_NAME + "." + RedditContract.RedditPostEntry._ID,
            RedditContract.RedditPostEntry.COL_TITLE,
            RedditContract.RedditPostEntry.COL_AUTHOR,
            RedditContract.RedditPostEntry.COL_SUBREDDIT_TYPE,
            RedditContract.RedditPostEntry.COL_COMMENTS,
            RedditContract.RedditPostEntry.COL_PERMALINK,
            RedditContract.RedditPostEntry.COL_URL,
            RedditContract.RedditPostEntry.COL_SCORE
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_AUTHOR = 2;
    public static final int COLUMN_SUBREDTYPE = 3;
    public static final int COLUMN_COMMENTS = 4;
    public static final int COLUMN_PERMALINK = 5;
    public static final int COLUMN_URL = 6;
    public static final int COLUMN_SCORE = 7;


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
        String topic = Utility.getPreferredTopic(getActivity());
        new FetchTopicTask(getActivity(), this).execute(topic);
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //NOW WE NEED TO RECEIVE THE INTENT FROM THE MAINACTIVITY

        Intent intent = getActivity().getIntent();
//        Log.d("The data right now is: ", data);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            data = intent.getStringExtra(Intent.EXTRA_TEXT);
            data = data.toLowerCase();

            postTypeAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.list_item_post_textview,
                    null,
                    // the column names to use to fill the textviews
                    new String[]{RedditContract.RedditPostEntry.COL_TITLE,
                            RedditContract.RedditPostEntry.COL_AUTHOR,
                            RedditContract.RedditPostEntry.COL_SCORE,
                    },
                    // the textviews to fill with the data pulled from the columns above
                    new int[]{R.id.list_item_post_textview,
                            R.id.list_item_post_textview,
                            R.id.list_item_post_textview,
                            R.id.list_item_post_textview
                    },
                    0
            );

            postTypeAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    switch (columnIndex) {
                        case COLUMN_TITLE:
                        case COLUMN_AUTHOR:
                        case COLUMN_SCORE:
                        case COLUMN_COMMENTS: {
                            String textString = cursor.getString(columnIndex);
                            TextView tView = (TextView) view;
                            tView.setText(Utility.formatPost(textString));
                            return true;
                        }
                    }
                    return false;
                }
            });

            View rootView = inflater.inflate(R.layout.fragment_detail_activity1, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_detailactivity1);

//             Log.d(LOGG_TAG, "The postAdapter is: " + postTypeAdapter.toString());
            listView.setAdapter(postTypeAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //String subReddit = postTypeAdapter.getItem(position);
                    Cursor cursor = postTypeAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {
                        Log.v(LOGG_TAG, "The URL being passed to the web Browser is " + STRING_URL);
                        String url = STRING_URL;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
            });

            return rootView;
        }
        return null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // Sort order:  Ascending, by score.
        String sortOrder = RedditContract.RedditPostEntry.COL_SCORE + " ASC";

        mTopic = Utility.getPreferredTopic(getActivity());
        Uri postForTopicUri = RedditContract.RedditPostEntry.buildRedditUri(
                mTopic, id);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                postForTopicUri,
                POST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        postTypeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        postTypeAdapter.swapCursor(null);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
