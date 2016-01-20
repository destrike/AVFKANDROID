package com.example.user.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;


public class MyVid extends Activity {

    public Button cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vid);

        openSesame();


    }

    public void openSesame() {
        Intent intent = new Intent (MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 1);

    }

}

