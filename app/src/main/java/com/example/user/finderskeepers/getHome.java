package com.example.user.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

public class getHome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_home);


        int timeout = 1000; // make the activity visible for 4 seconds

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
                Intent tocam = new Intent(getHome.this, MainActivity.class);
                startActivity(tocam);
            }
        }, timeout);
    }

}
