package com.proyecto.huila.todosalhuila.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaybackActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private ImageIndicatorView autoImageIndicatorView;

    private RelativeLayout connetion;

    int seleccion =0;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_TYPE_FILE = "INTENT_TYPE_FILE";

    @Bind(R.id.vv_playback)
    VideoView videoPlayback;
    @Bind(R.id.im_playback)
    ImageView imagePlayback;

   private int mVideoCurPos;

    private String path;
    private int type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        ButterKnife.bind(this);

        setTitle(R.string.tituloCaptura);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        final WS_ValidarConexionGoogle asyncTask = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output=="false") {
                    connetion.setVisibility(View.VISIBLE);
                }
            }
        });
        asyncTask.execute();

        connetion.setVisibility(View.GONE);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        if (id == R.id.action_atras) {
            Intent i = new Intent(PlaybackActivity.this, Multimedia.class);
            startActivity(i);
            finish();
        }else if(seleccion == 0) {
            seleccion++;
            final WS_ValidarConexionGoogle asyncTask = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                @Override
                public void processFinish(String output) {

                    if (output=="false") {
                        if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                            sinConexion();
                        } else {
                            conexionNoValida();
                        }
                    } else {
                        if (id == R.id.action_subir) {
                            PlaybackActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                        Intent i = new Intent(PlaybackActivity.this, SubirMultimedia.class);
                                        i.putExtra("INTENT_TYPE_FILE", type);
                                        i.putExtra("INTENT_PATH", path);
                                        startActivity(i);
                                        finish();

                                }
                            });
                        }
                        seleccion=0;
                    }
                }
            });
            asyncTask.execute();


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        return true;
    }

    public void conexionNoValida() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloConexion)
                .setMessage(R.string.mensaConexion)
                .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = 0;
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void sinConexion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloSinConexion)
                .setMessage(R.string.mensaSinConexion)
                .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion=0;
                    }
                })
                .setCancelable(false)
                .show();
    }

}
