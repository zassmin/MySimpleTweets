package com.codepath.apps.restclienttemplate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.User;
import com.squareup.picasso.Picasso;

public class ComposeActivity extends AppCompatActivity {
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        setUpView();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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
        setResult(RESULT_OK, null);
        finish();
    }

    public void onTweet(View view) {
        // grab tweet data
        // post to current user
        Toast.makeText(this, "tweeting", Toast.LENGTH_SHORT).show();
    }
}
