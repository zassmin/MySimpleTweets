package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends AppCompatActivity {
    User currentUser;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        setUpView();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        client = TwitterApplication.getRestClient(); // singleton client
    }

    private void setUpView() {
        // set current user
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        // render image
        ImageView ivUserProfileImage = (ImageView) findViewById(R.id.ivUserProfileImage);
        ivUserProfileImage.setImageResource(android.R.color.transparent); // FIXME: do I need to do this if it's not an adapter?
        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivUserProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClose(View view) {
        // this should take you back to the timeline without posting the data
        Toast.makeText(this, "closed", Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onTweet(View view) {
        // grab tweet data
        EditText etTweet = (EditText) findViewById(R.id.etTweet);
        String status = etTweet.getText().toString();
        if (status == "") {
            Toast.makeText(this, "tweet needs text", Toast.LENGTH_LONG).show();
        } else {
            // api call
            client.postTweet(status, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    populateTweetFromResponse(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // handle 403 with rate limit error or tweet dup
                }
            });
        }
    }

    private void populateTweetFromResponse(JSONObject response) {
        // create tweet
        Tweet tweet = Tweet.fromJSON(response);

        // intent
        Intent data = new Intent();
        data.putExtra("tweet", tweet);
        setResult(RESULT_OK, data);
        finish();
    }
}
