package com.codepath.apps.mysimpletweets.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.NetworkConnectivityReceiver;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zassmin on 10/7/15.
 */
public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private long max_id = 1;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        lvTweets.setAdapter(aTweets);
        setUpView();
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>(); // initialize
        aTweets = new TweetsArrayAdapter(getActivity(), tweets); // initialize adapter
    }

    // call add all on to the adapter and have access to put whatever data we'd like into the adapter
    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    private void setUpView() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialOrRefreshPopulateTimeline((long) 0);
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


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

    protected void populateTimeline(long offset) {
        // FIXME: move to set up view or to on create or to each timeline
//        if (NetworkConnectivityReceiver.isNetworkAvailable(this) != true) {
//            Toast.makeText(this, "you are offline, there are no new tweets", Toast.LENGTH_LONG).show();
//            return;
//        }
    }

    protected void initialOrRefreshPopulateTimeline(long page) {
        aTweets.clear();

//        if (NetworkConnectivityReceiver.isNetworkAvailable(v.getContext()) != true) {
//            aTweets.addAll(Tweet.getAll());
//            return;
//        }

        populateTimeline(page);
    }
}
