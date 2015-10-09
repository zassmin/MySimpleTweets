package com.codepath.apps.mysimpletweets.Fragments;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zassmin on 10/8/15.
 */
public class UserTimelineFragment extends TweetsListFragment {
    TwitterClient client;
    private static final String SCREEN_NAME = "screen_name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the client
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline((long) 0);
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(SCREEN_NAME, screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    private void populateTimeline(Long offset) {
//        if (NetworkConnectivityReceiver.isNetworkAvailable(this) != true) {
//            Toast.makeText(this, "you are offline, there are no new tweets", Toast.LENGTH_LONG).show();
//            return;
//        }
        String screenName = getArguments().getString(SCREEN_NAME, "");

        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                addAll(Tweet.fromJSONArray(jsonResponse));
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
}
