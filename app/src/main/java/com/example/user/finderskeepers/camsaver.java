package com.example.user.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

import com.example.user.finderskeepers.define.Receiver;

public class camsaver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camsaver);

        int timeout = 1000; // make the activity visible for 4 seconds

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
                Intent mIntentCamera = new Intent(camsaver.this, CustomCamera.class);
                // IF USER WANT GET FILE PATH OF ONE SELECTED FILE, SHOULD PUT ACTION_CHOSE_SINGLE_FILE
                mIntentCamera.putExtra(
                        Receiver.EXTRAS_ACTION, Receiver.ACTION_CHOSE_SINGLE_FILE);

                startActivity(mIntentCamera);
            }
        }, timeout);


    }

}
