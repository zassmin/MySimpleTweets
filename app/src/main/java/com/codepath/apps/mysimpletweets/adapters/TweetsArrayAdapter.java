package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zassmin on 9/30/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    // adapter displays stuff!

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        // resource is where we override the template
        super(context, 0, tweets); // zero since we are using a custom adapter
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: implement into the view holder pattern
        final Tweet tweet = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);

        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        // this needs to dynamically render
        if (tweet.getRelativeCreatedAtTime() == null) {
            tweet.setShortRelativeTime(tweet.getCreatedAt());
        }
        tvCreatedAt.setText(tweet.getRelativeCreatedAtTime());
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out old image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        // onClickProfilePhoto
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FIXME: abstract out so I'm not calling an activity in the Adapter, reach down, not up
                v.setTag(tweet.getUser());
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user", tweet.getUser());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
