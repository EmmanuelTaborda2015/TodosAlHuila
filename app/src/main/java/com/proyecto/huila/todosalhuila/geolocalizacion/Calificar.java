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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.webservice.WS_ConsultarCalificacion;
import com.proyecto.huila.todosalhuila.webservice.WS_RegistrarCalificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class Calificar extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        final Intent intent = getIntent();
        this.mipyme = intent.getStringExtra("mipyme");

        Log.v("mipyme", mipyme);
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

                        calificacion.setText(roundOff.toString());

                        ratingBar2.setRating(stars);

                        LayerDrawable stars3 = (LayerDrawable) ratingBar2.getProgressDrawable();
                        stars3.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                        stars3.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                        stars3.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                        if(!cal.get("mical").toString().equals(null)){
                            float stars2 = Float.parseFloat(cal.get("mical").toString());

                            BigDecimal a2 = new BigDecimal(stars2);
                            BigDecimal roundOff2 = a2.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                            calificacion.setText(roundOff2.toString());

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

    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable calificacion = new Runnable() {
        public void run() {

            final RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
            final TextView calificacion = (TextView) findViewById(R.id.calificar_label2);

            // float stars = Float.parseFloat(webResponseConsultarCalificacion.getWebResponse());

            //BigDecimal a = new BigDecimal(stars);
            ///BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            //calificacion.setText(roundOff.toString());

            //ratingBar2.setRating(stars);

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
            //String resultado = webResponseRegistrarCalificacion.getWebResponse();
            // if("true".equals(resultado)){
            //   Toast.makeText(Calificar.this, R.string.registroCalificacionTrue, Toast.LENGTH_LONG).show();
            // finish();
            //}else if("false".equals(resultado)){
            //  Toast.makeText(Calificar.this, R.string.registroCalificacionFalse, Toast.LENGTH_LONG).show();
            //}
            //circuloProgreso.dismiss();

        }
    };
}
