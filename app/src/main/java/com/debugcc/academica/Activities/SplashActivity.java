package com.debugcc.academica.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.FirebaseTasks;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SPLASH ACTIVITY";
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FirebaseTasks.getAllEvents(new FirebaseTasks.OnEventsUpdatedListener() {
            @Override
            public void onEventsUpdated(List<Event> events) {
                /// All events
            }
        });

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }

}
