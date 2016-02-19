package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.webservice.WS_ConsultarCalificacion;
import com.proyecto.huila.todosalhuila.webservice.WS_RegistrarCalificacion;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class Calificar extends AppCompatActivity {

    private String sitio_turistico;
    private String nombre_sitio_turistico;

    private Handler handler_consultarCalificacion = new Handler();
    private Thread thread_consultarCalificacion;
    private WS_ConsultarCalificacion webResponseConsultarCalificacion;

    private Handler handler_registrarCalificacion = new Handler();
    private Thread thread_registrarCalificacion;
    private WS_RegistrarCalificacion webResponseRegistrarCalificacion;

    private ProgressDialog circuloProgreso;

    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        final Intent intent = getIntent();
        this.nombre_sitio_turistico = intent.getStringExtra("nombre_sitio_turistico");
        this.sitio_turistico = intent.getStringExtra("sitio_turistico");
        //Bitmap bmp = intent.getParcelableExtra("imagen");

        //Se genera el llamado al web service que enviara el promedio de calificaciones para un sitio turistico presentes en la base de datos.
        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        thread_consultarCalificacion = new Thread() {
            public void run() {
                Looper.prepare();
                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                webResponseConsultarCalificacion = new WS_ConsultarCalificacion();
                webResponseConsultarCalificacion.startWebAccess("usuario", id_dispositivo, sitio_turistico);
                handler_consultarCalificacion.post(calificacion);
            }
        };

        thread_consultarCalificacion.start();
        ////////////////////////////////////////////////////////////////////////////////////////////////

        Button calificar = (Button) findViewById(R.id.calificar_boton1);
        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se genera el llamado al web service que enviara el promedio de calificaciones para un sitio turistico presentes en la base de datos.
                circuloProgreso = ProgressDialog.show(Calificar.this, "", "Espere por favor ...", true);

                thread_registrarCalificacion = new Thread() {
                    public void run() {
                        Looper.prepare();
                        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                        webResponseRegistrarCalificacion = new WS_RegistrarCalificacion();
                        webResponseRegistrarCalificacion.startWebAccess("usuario", id_dispositivo, sitio_turistico, String.valueOf((int)ratingBar.getRating()));
                        handler_registrarCalificacion.post(registrarCalificacion);
                    }
                };

                thread_registrarCalificacion.start();
                ////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        TextView sitio_turistico = (TextView) findViewById(R.id.label_lugar_calificar);
        sitio_turistico.setText(nombre_sitio_turistico);

        if (getIntent().hasExtra("byteArray")) {
            ImageView imagen = (ImageView) findViewById(R.id.calificar_imagen1);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            imagen.setImageBitmap(b);
        }

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        LayerDrawable stars2 = (LayerDrawable) ratingBar.getProgressDrawable();
        stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars2.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
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
        });
    }

    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable calificacion = new Runnable() {
        public void run() {

            final RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
            final TextView calificacion = (TextView) findViewById(R.id.calificar_label2);

            float stars = Float.parseFloat(webResponseConsultarCalificacion.getWebResponse());

            BigDecimal a = new BigDecimal(stars);
            BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            calificacion.setText(roundOff.toString());

            ratingBar2.setRating(stars);

            LayerDrawable stars3 = (LayerDrawable) ratingBar2.getProgressDrawable();
            stars3.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            stars3.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            stars3.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            circuloProgreso.dismiss();
        }
    };

    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable registrarCalificacion = new Runnable() {
        public void run() {
            String resultado = webResponseRegistrarCalificacion.getWebResponse();
            if("true".equals(resultado)){
                Toast.makeText(Calificar.this, R.string.registroCalificacionTrue, Toast.LENGTH_LONG).show();
                finish();
            }else if("false".equals(resultado)){
                Toast.makeText(Calificar.this, R.string.registroCalificacionFalse, Toast.LENGTH_LONG).show();
            }
            circuloProgreso.dismiss();

        }
    };
}