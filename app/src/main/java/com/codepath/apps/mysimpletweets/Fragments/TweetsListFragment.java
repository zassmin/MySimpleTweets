package com.codepath.apps.mysimpletweets.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.NetworkConnectivityReceiver;

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
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        lvTweets.setAdapter(aTweets);
        setUpView();
        initialOrRefreshPopulateTimeline((long) 0);
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

    public void insert(Tweet tweet, int position) {
        aTweets.insert(tweet, position);
    }

    public void clear() {
        aTweets.clear();
    }

    private void setUpView() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnectivityReceiver.isNetworkAvailable(v.getContext()) != true) {
                    Toast.makeText(v.getContext(), "to get new tweets please go online", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                    return;
                }
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

    }

    protected void initialOrRefreshPopulateTimeline(long page) {
        aTweets.clear();

        if (NetworkConnectivityReceiver.isNetworkAvailable(v.getContext()) != true) {
            aTweets.addAll(Tweet.getAll());
            return;
        }

        populateTimeline(page);
    }
}
