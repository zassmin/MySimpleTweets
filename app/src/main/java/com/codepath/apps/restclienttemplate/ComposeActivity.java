package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
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
