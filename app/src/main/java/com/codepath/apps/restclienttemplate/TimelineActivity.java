package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapters.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.adapters.TweetsArrayAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private long max_id = 1;

    /*
    TODOs:
      * [ ] User can then enter a new tweet and post this to twitter (post to twitter api)
      * [ ] User is taken back to home timeline with new tweet visible in timeline (make sure request is updating on twitter)
     */
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
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                } else {
                    Log.d("DEBUG", "null error");
                }
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

        return super.onOptionsItemSelected(item);
    }

    public void onCompose(MenuItem item) {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ComposeActivity.class);
        // needs current user info stored locally
        // intent.putExtra("profile_image", current_user.profileImage);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // find out what the result code for close is
        if (requestCode == REQUEST_CODE && resultCode == 200) {
            Toast.makeText(this, "happy data", Toast.LENGTH_SHORT).show();
        }
    }
}
