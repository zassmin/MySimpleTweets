package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.ComposeActivity;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Session;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
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
    private User currentUser;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setUpView();
        session = new Session();
        tweets = new ArrayList<>(); // initialize
        aTweets = new TweetsArrayAdapter(this, tweets); // initialize adapter
        // connect the list view
        lvTweets.setAdapter(aTweets);
        // get the client
        client = TwitterApplication.getRestClient(); // singleton client
        populateCurrentUser();
        initialPopulateTimeline();
    }

    private void setUpView() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            protected boolean onLoadMore(int page, int totalItemsCount) {
                int position = aTweets.getCount() - 1;
                max_id = aTweets.getItem(position).getRemoteId();
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

    private void populateCurrentUser() {
        client.getAccountSettings(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // FIXME: this flow breaks if a user updates their screen_name
                // create a user
                currentUser = User.findOrSetScreenNameFromJSON(response);
                // set a current user
                session.setCurrentUser(currentUser);
                // only make the second request if that use isn't stored locally
                if (session.getCurrentUser() != null && session.getCurrentUser().getRemoteId() == 0) {
                    populateNewCurrentUser();
                }
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

    private void populateNewCurrentUser() {
        client.getUserShow(session.getCurrentUser().getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    session.getCurrentUser().setName(response.getString("name"));
                    session.getCurrentUser().setProfileImageUrl(response.getString("profile_image_url"));
                    session.getCurrentUser().setRemoteId(response.getLong("id"));
                    session.getCurrentUser().save();
                    Log.d("current user", currentUser.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        intent.putExtra("current_user", session.getCurrentUser());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweetFromServer = (Tweet) data.getSerializableExtra("tweet");
            Toast.makeText(this, "api data" + tweetFromServer.getBody().toString(), Toast.LENGTH_LONG).show();
            aTweets.insert(tweetFromServer, 0);
        }
    }
}
