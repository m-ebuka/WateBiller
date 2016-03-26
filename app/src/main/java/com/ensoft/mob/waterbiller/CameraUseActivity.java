package com.ensoft.mob.waterbiller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ensoft.mob.waterbiller.helpers.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraUseActivity extends AppCompatActivity {
    Button button_cancel;
    Button button_capture;
    Button button_success;
    MyApp app;
    byte[] picData;

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private static final String TAG = "VideoCamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_use);
        app = ((MyApp)getApplication());


        //actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setTitle("Aqua Biller");
        actionBar.setIcon(R.drawable.logo);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5D617A"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff' fontSize='10'>AquaBiller : Camera</font>"));

        //get the buttons
        button_cancel = (Button)findViewById(R.id.button_cancel);
        button_capture = (Button) findViewById(R.id.button_capture);
        button_success = (Button)findViewById(R.id.button_success);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cameraStartPreview();
                app.setCameraByte(null);
                app.setCameraByteForMeterReading(null);
                app.setProfileImage(null);
                app.setSituationImage(null);
                Intent intent = new Intent(getApplicationContext(),app.getActivity().getClass());
                CameraUseActivity.this.finish();
                startActivity(intent);

            }
        });
        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
                if (mPicture == null) {
                    //Log.d("MyCameraApp", "Picture taken is null");
                    Toast.makeText(getApplicationContext(),"No PicData", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Yes PicData", Toast.LENGTH_LONG).show();
                    System.out.println("Picture callback : " + mPicture);
                }

            }
        });
        button_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),app.getActivity().getClass());
                //mCamera.release();
                CameraUseActivity.this.finish();
                startActivity(intent);
            }
        });

        cameraStartPreview(); //start previewing camera
    }

    private void cameraStartPreview()
    {
        mCamera = getCameraInstance();
        setCameraDisplayOrientation(mCamera); //set camera display orientation
        mCameraPreview = new CameraPreview(this,mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
    }

    public void onResume(){
        super.onResume();

        if(mCamera ==null){

            setContentView(R.layout.activity_camera_use);
            mCamera = getCameraInstance();
            setCameraDisplayOrientation(mCamera); //set camera display orientation
            mCameraPreview = new CameraPreview(this,mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mCameraPreview);

        }

    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();

            /******/
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size mSize = null;
            for (Camera.Size size : sizes) {
                //Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
                mSize = size;
            }
            //Log.i(TAG, "Chosen resolution: "+ mSize.width+" "+ mSize.height);
            parameters.setPictureSize(320, 240); //320 240

            //Check if device support autoflash
            List<String> flashModes = parameters.getSupportedFlashModes();
            if(flashModes.contains(android.hardware.Camera.Parameters.FLASH_MODE_AUTO))
            {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
            camera.setParameters(parameters);

            /**********/
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    // Called when shutter is opened
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    // Handles data for jpeg picture
    /*Camera.PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("image", data);
            db.insert("storedImages", "tag", values);
            preview.camera.startPreview();
        }
    };*/

    // Handles data for raw picture
    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //picData = data;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(bitmap==null){
                Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "taken", Toast.LENGTH_SHORT).show();
                if(app.getActivity().getClass() == BuildingEnumerationActivity.class) {
                    app.setCameraByte(data);
                }
                if(app.getActivity().getClass() == BillingActivity.class)
                {
                    app.setCameraByteForMeterReading(data);
                }
                if(app.getActivity().getClass() == individual_profile.class){
                    app.setProfileImage(data);
                }
                if(app.getActivity().getClass() == iReportActivity.class){
                    app.setSituationImage(data);
                }
            }



            //removed
            /*File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                //Log.d("MyCameraApp", "Picture taken is null");
                Toast.makeText(getApplicationContext(),"PicData is null", Toast.LENGTH_LONG).show();
                return;
            }*/
            //SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
            //int shutterSound = soundPool.load(this, R.raw.camera_click, 0);
            //soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);

            //removed
            /*System.out.println("Text [Byte Format] : " + data);
            Log.d(TAG, "Picture data:" + data.toString());
            Toast.makeText(getApplicationContext(),"Picture taken successfully", Toast.LENGTH_LONG).show();*/

            /*try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                Log.d("MyCameraApp", "Picture taken:" + data.toString());
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }*/
        }


    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        Log.d("MyCameraApp", "success in create directory");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    public void setCameraDisplayOrientation(android.hardware.Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);


        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_use, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
