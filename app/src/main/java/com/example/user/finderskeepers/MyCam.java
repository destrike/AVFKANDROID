package com.example.user.finderskeepers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.view.View.OnClickListener;


import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;





import butterknife.ButterKnife;
import butterknife.InjectView;


public class MyCam extends Activity implements SurfaceHolder.Callback {
    Camera camera;
    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;
    @InjectView(R.id.btn_take_photo)
    FloatingActionButton btn_take_photo;
    SurfaceHolder surfaceHolder;
    PictureCallback jpegCallback, mpegCallback;
    ShutterCallback shutterCallback;
    private MediaRecorder mediaRecorder;
    Context myContext;
    boolean recording = false;

    private static int camIdBack = Camera.CameraInfo.CAMERA_FACING_BACK;


    private String chooseFlash;
    private ButtonClickListener btnClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cam);


        btnClick= new ButtonClickListener();
        ButterKnife.inject(this);
        surfaceHolder = surfaceView.getHolder();
        // Install a surfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);
        //deprecated setting, but required on android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btn_take_photo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        jpegCallback = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outputStream = null;
                File file_image = getDirc();
                if (!file_image.exists() && !file_image.mkdirs()) {
                    Toast.makeText(getApplication(), "Can't create directory to save image", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = simpleDateFormat.format(new Date());
                String photofile = "FindersKeepers" + date + ".jpg";
                String file_name = file_image.getPath() + File.separator + photofile;
                File picfile = new File(file_name);
                try {
                    outputStream = new FileOutputStream(picfile);
                    outputStream.write(data);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                } catch (IOException ex) {
                } finally {

                }
                Toast.makeText(getApplicationContext(), "Picture saved", Toast.LENGTH_SHORT).show();
                refreshCamera();
                refreshGallery(picfile);
            }
        };

        int idList[]={ R.id.btn_flash, R.id.btn_exit, R.id.btn_switch, R.id.btn_vid, };
        for(int id:idList){ View v= findViewById(id);
            v.setOnClickListener(btnClick);

        }


    }


    private class ButtonClickListener implements OnClickListener{
        public void onClick(View v){


            switch(v.getId()){
                case R.id.btn_flash: chooseFlash();
                    break;

                case R.id.btn_exit:
                    finish();
                    Intent onHome = new Intent (MyCam.this, getHome.class);
                    startActivity(onHome);
                    break;

                case R.id.btn_switch:


                    // if the camera preview is the front
                    camera.release();

                    //swap the id of the camera to be used
                    if(camIdBack == Camera.CameraInfo.CAMERA_FACING_BACK){
                        camIdBack = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                    else {
                        camIdBack = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    camera = Camera.open(camIdBack);

                    setCameraDisplayOrientation(MyCam.this, camIdBack, camera);
                    try {

                        camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    camera.startPreview();


                    break;


                    case R.id.btn_vid:

                        if (recording==true) {
                            // stop recording and release camera
                            mediaRecorder.stop(); // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            Toast.makeText(MyCam.this, "Video captured!", Toast.LENGTH_LONG).show();
                            recording = false;
                        } else {

                            prepareMediaRecorder();

                            try {
                                mediaRecorder.start();
                                Toast.makeText(MyCam.this, "Recording!", Toast.LENGTH_LONG).show();
                            } catch (final Exception ex) {
                                // Log.i("---","Exception in thread");
                            }
                            recording = true;
                        }


                    break;
              }

        }

    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void chooseFlash() {
        final Camera.Parameters params = camera.getParameters();
        final List<String> flashModeList = params.getSupportedFlashModes();
        if (flashModeList == null) {
            // no flash!
            return;
        }
        final CharSequence[] flashText = flashModeList.toArray(
                new CharSequence[flashModeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose flash type");
        builder.setSingleChoiceItems(flashText, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        params.setFlashMode(flashModeList.get(which));
                        camera.setParameters(params);
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }




    //refresh gallery
    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }



    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            //preview surface does not exist
            return;
        }
        //stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }
        //set preview size and make any resize, rotate or
        //reformatting changes here
        //start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }

    public File getDirc() {
        File dics = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(dics, "Camera_Demo");
    }

    public void captureImage() {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //open the camera
        try {
            camera = Camera.open();
        } catch (RuntimeException ex) {
        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        //modify parameter
        parameters.setPreviewFrameRate(20);
        parameters.setPreviewSize(352, 288);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            //The surface thas been created, now tell the camera where to draw
            //the preview
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
//        releaseCamera();
        camera.release();
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FindersKeeperVid.mp4");
        mediaRecorder.setMaxDuration(600000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }






}