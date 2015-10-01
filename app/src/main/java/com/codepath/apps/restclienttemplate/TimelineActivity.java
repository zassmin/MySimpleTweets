package com.codepath.apps.restclienttemplate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.adapters.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.adapters.TweetsArrayAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private long max_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setUpView();

        tweets = new ArrayList<>(); // initialize
        aTweets = new TweetsArrayAdapter(this, tweets); // initialize adapter
        // connect the list view
        lvTweets.setAdapter(aTweets);
        // get the client
        client = TwitterApplication.getRestClient(); // singleton client
        initialPopulateTimeline();
    }

    private void setUpView() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            protected boolean onLoadMore(int page, int totalItemsCount) {
                int position = aTweets.getCount() - 1;
                max_id = aTweets.getItem(position).getUid();
                populateTimeline(max_id);
                return true;
            }
        });
    }

    private void populateTimeline(Long offset) {
        client.getHomeTimeline(offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                aTweets.addAll(Tweet.fromJSONArray(jsonResponse));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void initialPopulateTimeline() {
        aTweets.clear();
        populateTimeline(max_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
