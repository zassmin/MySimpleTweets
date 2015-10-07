package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Session;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.NetworkConnectivityReceiver;
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
    private SwipeRefreshLayout swipeContainer;

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
        // TODO: delete too many tweets from db
    }

    private void setUpView() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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

    private void fetchTimelineAsync(int page) {
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                aTweets.clear();
                aTweets.addAll(Tweet.fromJSONArray(json));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });

    }

    private void populateTimeline(Long offset) {
        if (NetworkConnectivityReceiver.isNetworkAvailable(this) != true) {
            Toast.makeText(this, "you are offline, there are no new tweets", Toast.LENGTH_LONG).show();
            return;
        }

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

        if (NetworkConnectivityReceiver.isNetworkAvailable(this) != true) {
            aTweets.addAll(Tweet.getAll());
            return;
        }

        populateTimeline(max_id);
    }

    private void populateCurrentUser() {
        if (NetworkConnectivityReceiver.isNetworkAvailable(this) != true) {
            // grab current user from Shared Prefs, if there is one
            // FIXME: should this be on a different thread?
            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String userId = pref.getString("user_id", "");
            if (!userId.isEmpty()) {
                long uId = Long.parseLong(userId);
                currentUser = User.findById(uId);
                if (currentUser != null) {
                    session.setCurrentUser(currentUser);
                } else {
                    logOut();
                }
                return;
            }
            return; // exit if null, we should not call this endpoint if user isn't online
        }

        client.getUserVerificationSettings(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // create a user
                currentUser = User.findOrCreateFromJson(response);
                // set a current user
                session.setCurrentUser(currentUser);
                // only make the second request if that use isn't stored locally
                if (session.getCurrentUser() != null) {
                    // set shared preferences
                    // this should be happening each time!!
                    SharedPreferences pref =
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("user_id", session.getCurrentUser().getId().toString());
                    edit.commit();
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

    private void logOut() {
        Toast.makeText(this,
                "we should be logging you out since there is no user session",
                Toast.LENGTH_LONG).show();
    }
}
