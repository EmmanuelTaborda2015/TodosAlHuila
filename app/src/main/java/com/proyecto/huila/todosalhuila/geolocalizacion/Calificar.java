package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.webservice.WS_ConsultarCalificacion;
import com.proyecto.huila.todosalhuila.webservice.WS_RegistrarCalificacion;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class Calificar extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }

    private String sitio_turistico;
    private String mipyme;

    private Handler handler_consultarCalificacion = new Handler();
    private Thread thread_consultarCalificacion;
    private WS_ConsultarCalificacion webResponseConsultarCalificacion;

    private Handler handler_registrarCalificacion = new Handler();
    private Thread thread_registrarCalificacion;
    private WS_RegistrarCalificacion webResponseRegistrarCalificacion;

    private ProgressDialog circuloProgreso;

    private RatingBar ratingBar;

    private int seleccion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String con) {

                if (con=="false") {
                    connetion.setVisibility(View.VISIBLE);
                }
            }
        });
        asyncTaskConection.execute();

        connetion.setVisibility(View.GONE);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        final Intent intent = getIntent();
        this.mipyme = intent.getStringExtra("mipyme");

        //Se genera el llamado al web service que enviara el promedio de calificaciones para un sitio turistico presentes en la base de datos.
        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        final WS_ConsultarCalificacion asyncTask = new WS_ConsultarCalificacion(new WS_ConsultarCalificacion.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                JSONObject json = null;
                try {
                    json = new JSONObject(output);
                    JSONObject cal = new JSONObject(json.get("calificacion").toString());

                    if ("correcto".equals(cal.get("estado").toString())) {

                        final RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
                        final TextView calificacion = (TextView) findViewById(R.id.calificar_label2);

                        float stars = Float.parseFloat(cal.get("calgeneral").toString());

                        BigDecimal a = new BigDecimal(stars);
                        BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                        calificacion.setText(roundOff.toString() + ", Número de Votos: " + cal.get("numcal").toString());

                        ratingBar2.setRating(stars);

                        LayerDrawable stars3 = (LayerDrawable) ratingBar2.getProgressDrawable();
                        stars3.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                        stars3.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                        stars3.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                        if (!cal.get("mical").toString().equals(null)) {
                            float stars2 = Float.parseFloat(cal.get("mical").toString());

                            BigDecimal a2 = new BigDecimal(stars2);
                            BigDecimal roundOff2 = a2.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                            ratingBar.setRating(stars2);

                            LayerDrawable stars4 = (LayerDrawable) ratingBar.getProgressDrawable();
                            stars4.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                            stars4.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                            stars4.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        }

                    }

                    circuloProgreso.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                circuloProgreso.dismiss();
            }
        });
        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        String params[] = {new Login().usuario, id_dispositivo, mipyme};
        asyncTask.execute(params);
        ////////////////////////////////////////////////////////////////////////////////////////////////

        Button calificar = (Button) findViewById(R.id.calificar_boton1);
        calificar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             //Se genera el llamado al web service que enviara el promedio de calificaciones para un sitio turistico presentes en la base de datos.
                                             circuloProgreso = ProgressDialog.show(Calificar.this, "", "Espere por favor ...", true);

                                             final WS_RegistrarCalificacion asyncTask = new WS_RegistrarCalificacion(new WS_RegistrarCalificacion.AsyncResponse() {

                                                 @Override
                                                 public void processFinish(String output) {
                                                        Log.v("output reg", output);
                                                     try {
                                                         JSONObject json = new JSONObject(output);
                                                         if ("true".equals(json.getString("resultado").toString())) {
                                                             Toast.makeText(Calificar.this, "Su calificación ha sido registrada.", Toast.LENGTH_LONG).show();
                                                             finish();
                                                         } else {
                                                             Toast.makeText(Calificar.this, "No se ha podido registrar su calificación, por favor intente nuevamente.", Toast.LENGTH_LONG).show();
                                                         }
                                                     } catch (JSONException e) {
                                                         e.printStackTrace();
                                                     }

                                                     circuloProgreso.dismiss();
                                                 }
                                             });
                                             String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                                             String params[] = {new Login().usuario, id_dispositivo, mipyme, String.valueOf((int) ratingBar.getRating())};
                                             asyncTask.execute(params);
                                         }
                                         ////////////////////////////////////////////////////////////////////////////////////////////////
                                     }

        );

        TextView sitio_turistico = (TextView) findViewById(R.id.label_lugar_calificar);
        //sitio_turistico.setText(nombre_sitio_turistico);

        if (

                getIntent()

                        .

                                hasExtra("byteArray")

                )

        {
            ImageView imagen = (ImageView) findViewById(R.id.calificar_imagen1);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            imagen.setImageBitmap(b);
        }

        ratingBar = (RatingBar)

                findViewById(R.id.ratingBar);

        LayerDrawable stars2 = (LayerDrawable) ratingBar.getProgressDrawable();
        stars2.getDrawable(2).

                setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        stars2.getDrawable(0).

                setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnTouchListener(new View.OnTouchListener()

                                     {
                                         @Override
                                         public boolean onTouch(View v, MotionEvent event) {


                                             if (event.getAction() == MotionEvent.ACTION_UP) {
                                                 float touchPositionX = event.getX();
                                                 float width = ratingBar.getWidth();
                                                 float starsf = (touchPositionX / width) * 5.0f;
                                                 int stars = (int) starsf + 1;
                                                 ratingBar.setRating(stars);

                                                 RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                                                 LayerDrawable stars2 = (LayerDrawable) ratingBar.getProgressDrawable();
                                                 stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                                                 stars2.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                                             }
                                             if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                 v.setPressed(true);
                                             }

                                             if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                                 v.setPressed(false);
                                             }

                                             return true;
                                         }
                                     }

        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_atras) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atras, menu);
        return true;
    }

    public void conexionNoValida() {
        new AlertDialog.Builder(this)
                .setTitle("Conexión no válida!!!")
                .setMessage("La conexión a internet mediante la cual esta tratando de acceder no es válida, por favor verifiquela e intente de nuevo.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
                .setTitle("Sin conexión a internet!!!")
                .setMessage("Por favor conéctese a una red WIFI o Móvil.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = 0;
                    }
                })
                .setCancelable(false)
                .show();
    }
}
