package com.proyecto.huila.todosalhuila.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.herramientas.ServicioWeb;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaybackActivity extends Activity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_TYPE_FILE = "INTENT_TYPE_FILE";

    private Handler handler_cargar = new Handler();
    private Thread thread_cargar;
    private ServicioWeb webResponseCargar;

    private ProgressDialog circuloProgreso;

    private int type;

    private int mCameraId;

    @Bind(R.id.vv_playback)
    VideoView videoPlayback;
    @Bind(R.id.im_playback)
    ImageView imagePlayback;
    @Bind(R.id.cargar_plaback)
    ImageView cargarPlayback;
    @Bind(R.id.atras_plaback)
    ImageView regresar;

    private int mVideoCurPos;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        ButterKnife.bind(this);

        videoPlayback.setVisibility(View.GONE);
        imagePlayback.setVisibility(View.GONE);

        path = getIntent().getStringExtra(INTENT_PATH);
        type = getIntent().getIntExtra(INTENT_TYPE_FILE, 0);

        if (path == null && type == 0) {
            finish();
        }

        PlaybackActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (type == MEDIA_TYPE_VIDEO) {

                    videoPlayback.setVisibility(View.VISIBLE);
                    videoPlayback.setVideoPath(path);
                    videoPlayback.setKeepScreenOn(true);
                    videoPlayback.setMediaController(new MediaController(PlaybackActivity.this));
                    videoPlayback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                        }
                    });
                    videoPlayback.start();

                } else if (type == MEDIA_TYPE_IMAGE) {

                    imagePlayback.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imagePlayback.setImageBitmap(bitmap);

                }
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlaybackActivity.this, CameraActivity.class);
                startActivity(i);
                finish();
            }
        });

        cargarPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread procesar = new Thread(){
                    public void run(){
                        convertFileToString(path);

                        if(type==1){
                            circuloProgreso = ProgressDialog.show(PlaybackActivity.this, "", "Subiendo Imagen...", true);
                        }else if(type==2){
                            circuloProgreso = ProgressDialog.show(PlaybackActivity.this, "", "Subiendo Video...", true);
                        }
                        handler_cargar.post(cargar);
                    }
                };

                procesar.start();




            }
        });
    }

    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable cargar = new Runnable() {
        public void run() {
            Toast.makeText(PlaybackActivity.this, "Se ha cargado correctamente la imagen", Toast.LENGTH_LONG).show();
            String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
            Intent i = new Intent(PlaybackActivity.this, CameraActivity.class);
            startActivity(i);
            finish();
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public String convertFileToString(String pathOnSdCard){



        String strFile=null;

        File file=new File(pathOnSdCard);

        try {

            byte[] data = FileUtils.readFileToByteArray(file);//Convert any file, image or video into byte array

            strFile = Base64.encodeToString(data, Base64.DEFAULT);//Convert byte array into string


            Log.v("prueba", strFile);
            Looper.prepare();


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

            byte[] decoded = Base64.decode(strFile, Base64.DEFAULT);
            FileOutputStream fos = null;
            fos = new FileOutputStream(mediaStorageDir.getPath() + File.separator +
                    "Emmanuel" + ".mp4");
            fos.write(decoded);
            fos.close();


        } catch (IOException e) {

            e.printStackTrace();

        }

        return strFile;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayback.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayback.pause();
        mVideoCurPos = videoPlayback.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayback.seekTo(mVideoCurPos);
        videoPlayback.start();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PlaybackActivity.this, CameraActivity.class);
        startActivity(i);
        finish();
    }
}
