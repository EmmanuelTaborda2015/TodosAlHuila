package com.proyecto.huila.todosalhuila.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.proyecto.huila.todosalhuila.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaybackActivity extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_TYPE_FILE = "INTENT_TYPE_FILE";

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
    private int type;


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
                Intent i = new Intent(PlaybackActivity.this, Multimedia.class);
                startActivity(i);
                finish();
            }
        });

        cargarPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaybackActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        Intent i = new Intent(PlaybackActivity.this, SubirMultimedia.class);
                        i.putExtra("INTENT_TYPE_FILE", type);
                        i.putExtra("INTENT_PATH", path);
                        startActivity(i);
                    }
                });
            }
        });
    }

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
        Intent i = new Intent(PlaybackActivity.this, Multimedia.class);
        startActivity(i);
        finish();
    }
}
