package com.codepath.apps.mysimpletweets.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zassmin on 10/7/15.
 */
public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        lvTweets.setAdapter(aTweets);
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
}
