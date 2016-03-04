/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proyecto.huila.todosalhuila.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.CameraConfig;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.custom.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Actividad principal para el control de la camara
 *
 * @author anthorlop
 *
 */
public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";

    private static  final int FOCUS_AREA_SIZE= 500;

    private Camera mCamera;
    private CameraPreview mPreview;

    private MediaRecorder mediaRecorder;

    @Bind(R.id.button_capture)
    ImageView capture;

    @Bind(R.id.button_ChangeCamera)
    ImageView switchCamera;

    @Bind(R.id.buttonFoto)
    ImageView captureImage;

    private Context myContext;

    @Bind(R.id.camera_preview)
    LinearLayout cameraPreview;

    private String url_file;

    private static boolean cameraFront = false;
    private static boolean flash = false;

    @Bind(R.id.buttonQuality)
    Button buttonQuality;

    @Bind(R.id.listOfQualities)
    ListView listOfQualities;

    @Bind(R.id.buttonFlash)
    ImageView buttonFlash;

    @Bind(R.id.chronoRecordingImage)
    ImageView chronoRecordingImage;

    @Bind(R.id.textChrono)
    Chronometer chrono;

    private long countUp;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_TYPE_FILE = "INTENT_TYPE_FILE";

    private static final int MAX_VIDEO_LENGTH = 15 * 1000;
    private static final int MAX_VIDEO_LENGTH_SEG = 15;

    private static String pathFile;

    private int quality = CamcorderProfile.QUALITY_480P;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myContext = this;

        ButterKnife.bind(this);

        initialize();

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(CameraActivity.this, R.raw.guncocking);
                mp.start();
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            pathFile = getOutputMediaFile(MEDIA_TYPE_IMAGE).toString();
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                Intent i = new Intent(CameraActivity.this, PlaybackActivity.class);
                i.putExtra(INTENT_PATH, pathFile);
                i.putExtra(INTENT_TYPE_FILE, MEDIA_TYPE_IMAGE);
                startActivity(i);

            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    };
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {

            releaseCamera();

            final boolean frontal = cameraFront;

            // if the front facing camera does not exist
            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {

                // desactivar el cambio de camara
                switchCameraListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CameraActivity.this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                    }
                };

                // seleccionar la camara trasera
                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                }
            } else if (!frontal) {

                // seleccionar la camara trasera sin desactivar la delantera
                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                }
            }

            mCamera = Camera.open(cameraId);
            mPreview.refreshCamera(mCamera);
            reloadQualities(cameraId);

        }
    }

    public void initialize() {

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture.setOnClickListener(captrureListener);

        switchCamera.setOnClickListener(switchCameraListener);

        buttonQuality.setOnClickListener(qualityListener);

        buttonFlash.setOnClickListener(flashListener);


        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        focusOnTouch(event);
                    } catch(Exception e){
                        Log.i(TAG, "Fail when camera try autofocus");
                    }
                }
                return true;
            }
        });

    }

    private void reloadQualities(int idCamera) {


        SharedPreferences prefs = getSharedPreferences("RECORDING", Context.MODE_PRIVATE);

        quality = prefs.getInt("QUALITY", CamcorderProfile.QUALITY_480P);

        changeVideoQuality(quality);

        final ArrayList<String> list = new ArrayList<String>();

        int maxQualitySupported = CamcorderProfile.QUALITY_480P;

        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_480P)) {
            list.add("480p");
            maxQualitySupported = CamcorderProfile.QUALITY_480P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_720P)) {
            list.add("720p");
            maxQualitySupported = CamcorderProfile.QUALITY_720P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_1080P)) {
            list.add("1080p");
            maxQualitySupported = CamcorderProfile.QUALITY_1080P;
        }
        if (CamcorderProfile.hasProfile(idCamera, CamcorderProfile.QUALITY_2160P)) {
            list.add("2160p");
            maxQualitySupported = CamcorderProfile.QUALITY_2160P;
        }

        if (!CamcorderProfile.hasProfile(idCamera, quality)) {
            quality = maxQualitySupported;
            updateButtonText(maxQualitySupported);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listOfQualities.setAdapter(adapter);

        listOfQualities.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                buttonQuality.setText(item);

                if (item.equals("480p")) {
                    changeVideoQuality(CamcorderProfile.QUALITY_480P);
                } else if (item.equals("720p")) {
                    changeVideoQuality(CamcorderProfile.QUALITY_720P);
                } else if (item.equals("1080p")) {
                    changeVideoQuality(CamcorderProfile.QUALITY_1080P);
                } else if (item.equals("2160p")) {
                    changeVideoQuality(CamcorderProfile.QUALITY_2160P);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    listOfQualities.animate().setDuration(200).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    listOfQualities.setVisibility(View.GONE);
                                }
                            });
                } else {
                    listOfQualities.setVisibility(View.GONE);
                }
            }

        });

    }

    View.OnClickListener qualityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && listOfQualities.getVisibility() == View.GONE) {
                    listOfQualities.setVisibility(View.VISIBLE);
                    listOfQualities.animate().setDuration(200).alpha(95)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {

                                }

                            });
                } else {
                    listOfQualities.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    View.OnClickListener flashListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording && !cameraFront) {
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.drawable.ic_flash_off_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    flash = true;
                    buttonFlash.setImageResource(R.drawable.ic_flash_on_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        }
    };

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);

                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

                reloadQualities(cameraId);

            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);

                // al poner la camara frontal se desactiva el flash
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.drawable.ic_flash_off_white);
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }

                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

                reloadQualities(cameraId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    boolean recording = false;
    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording

                stopChronometer();

                capture.setImageResource(R.drawable.player_record);

                releaseMediaRecorder(); // release the MediaRecorder object

                recording = false;


                Intent i = new Intent(CameraActivity.this, PlaybackActivity.class);
                i.putExtra(INTENT_PATH, pathFile);
                i.putExtra(INTENT_TYPE_FILE, MEDIA_TYPE_VIDEO);
                startActivity(i);


            } else {
                if (prepareMediaRecorder()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // If there are stories, add them to the table

                            mediaRecorder.start();
                            recording = true;

                            try {

                                startChronometer();

                                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                } else {
                                    changeRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                }

                                capture.setImageResource(R.drawable.player_stop);

                            } catch (final Exception ex) {
                                // Log.i("---","Exception in thread");
                            }
                        }
                    });

                }else{
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                }

            }
        }
    };

    private void changeRequestedOrientation(int orientation) {
        setRequestedOrientation(orientation);
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();


        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (cameraFront) {
                mediaRecorder.setOrientationHint(270);
            } else {
                mediaRecorder.setOrientationHint(90);
            }
        }

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(quality));


        pathFile = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mediaRecorder.setMaxDuration(MAX_VIDEO_LENGTH); // Set max duration 15 sec.
        mediaRecorder.setMaxFileSize(CameraConfig.MAX_FILE_SIZE_RECORD); // Set max file size 50M

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
        if (mCamera != null) {
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    private void changeVideoQuality(int quality) {
        SharedPreferences prefs = getSharedPreferences("RECORDING", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("QUALITY", quality);

        editor.commit();

        this.quality = quality;

        updateButtonText(quality);
    }

    private void updateButtonText(int quality) {
        if (quality == CamcorderProfile.QUALITY_480P)
            buttonQuality.setText("480p");
        if (quality == CamcorderProfile.QUALITY_720P)
            buttonQuality.setText("720p");
        if (quality == CamcorderProfile.QUALITY_1080P)
            buttonQuality.setText("1080p");
        if (quality == CamcorderProfile.QUALITY_2160P)
            buttonQuality.setText("2160p");
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public void setFlashMode(String mode) {

        try {
            if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)
                    && mCamera != null
                    && !cameraFront) {

                mPreview.setFlashMode(mode);
                mPreview.refreshCamera(mCamera);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception changing flashLight mode",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void startChronometer() {

        chrono.setVisibility(View.VISIBLE);

        final long startTime = SystemClock.elapsedRealtime();

        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUp = (SystemClock.elapsedRealtime() - startTime) / 1000;

                if (countUp % 2 == 0) {
                    chronoRecordingImage.setVisibility(View.VISIBLE);
                } else {
                    chronoRecordingImage.setVisibility(View.INVISIBLE);
                }

                String asText = String.format("%02d", countUp / 60) + ":" + String.format("%02d", countUp % 60);

                chrono.setText(asText);

                if(countUp>MAX_VIDEO_LENGTH_SEG-5){
                    chrono.setTextColor(getResources().getColor(R.color.red));
                }
                if(countUp==MAX_VIDEO_LENGTH_SEG+1){
                    // stop recording and release camera
                    mediaRecorder.stop(); // stop the recording

                    stopChronometer();

                    capture.setImageResource(R.drawable.player_record);

                    releaseMediaRecorder(); // release the MediaRecorder object

                    recording = false;

                    Intent i = new Intent(CameraActivity.this, PlaybackActivity.class);
                    i.putExtra(INTENT_PATH, pathFile);
                    i.putExtra(INTENT_TYPE_FILE, MEDIA_TYPE_VIDEO);
                    startActivity(i);
                }
            }
        });
        chrono.start();
    }

    private void stopChronometer() {
        chrono.stop();
        chronoRecordingImage.setVisibility(View.INVISIBLE);
        chrono.setVisibility(View.INVISIBLE);
    }

    public static void reset() {
        flash = false;
        cameraFront = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (recording) {
            // stop recording and release camera
            mediaRecorder.stop(); // stop the recording

            if (chrono != null && chrono.isActivated())
                chrono.stop();

            releaseMediaRecorder(); // release the MediaRecorder object
            Toast.makeText(CameraActivity.this, "La grabación se ha detenido", Toast.LENGTH_LONG).show();
            recording = false;

            File mp4 = new File(url_file);
            if (mp4.exists() && mp4.isFile()) {
                mp4.delete();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i(TAG, "fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus", "success!");
            } else {
                // do something...
                Log.i("tap_to_focus", "fail!");
            }
        }
    };

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


}

