package com.example.user.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import define.Receiver;
import ui.activity.custom.camera.*;


public class MyVid extends Activity {

//    private ButtonClickListener btnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vid);

        int timeout = 1000; // make the activity visible for 4 seconds

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
                Intent mIntentCamera = new Intent(MyVid.this, ui.activity.custom.camera.CustomCamera.class);
                // IF USER WANT GET FILE PATH OF ONE SELECTED FILE, SHOULD PUT ACTION_CHOSE_SINGLE_FILE
                mIntentCamera.putExtra(
                        Receiver.EXTRAS_ACTION, Receiver.ACTION_CHOSE_SINGLE_FILE);

                startActivity(mIntentCamera);
            }
        }, timeout);

    }

}

