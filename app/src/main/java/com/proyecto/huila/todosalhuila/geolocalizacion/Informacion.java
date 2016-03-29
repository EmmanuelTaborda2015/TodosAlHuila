package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.lista.Comentario;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.webservice.WS_SitioTuristico;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class Informacion extends AppCompatActivity {

    private Handler handler_sitio = new Handler();
    private Thread thread_sitio;
    private WS_SitioTuristico webResponseSitio;

    private ProgressDialog circuloProgreso;

    private String sitio_turistico;

    private String title;

    private String mipyme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout tool = (RelativeLayout) findViewById(R.id.toolbar2);
        //tool.setVisibility(View.GONE);

        final Intent intent = getIntent();
        this.sitio_turistico = intent.getStringExtra("datos");

        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);


        final JSONObject datos;
        try {
            datos = new JSONObject(sitio_turistico);

            title = datos.get("nombre").toString();

            mipyme = datos.get("id").toString();

            LoadImageFromURL asyncTask = new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                private ImageIndicatorViewUrl imageIndicatorView;

                @Override
                public void processFinish(final Bitmap[] output) {

                    this.imageIndicatorView = (ImageIndicatorViewUrl) findViewById(R.id.indicate_view);
                    Bitmap[] resArray = output;
                    if (resArray.length == 0) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                        resArray = new Bitmap[1];
                        resArray[0] = icon;
                    }
                    this.imageIndicatorView.setupLayoutByDrawable(resArray);
                    this.imageIndicatorView.show();

                    ImageView comentar = (ImageView) findViewById(R.id.botonComentar);
                    comentar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Comentario.class);
                            i.putExtra("sitio_turistico", sitio_turistico);
                            startActivity(i);
                        }
                    });

                    ImageView calificar = (ImageView) findViewById(R.id.botonCalificar);
                    calificar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Calificar.class);
                            if (output.length > 0) {
                                try {
                                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                    output[0].compress(Bitmap.CompressFormat.PNG, 50, bs);
                                    i.putExtra("byteArray", bs.toByteArray());
                                }catch (Exception e){
                                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                    icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                    i.putExtra("byteArray", bs.toByteArray());
                                }
                            } else {
                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                i.putExtra("byteArray", bs.toByteArray());
                            }
                            i.putExtra("mipyme",mipyme);
                            i.putExtra("sitio_turistico", title);
                            startActivity(i);
                        }
                    });

                    ImageView compartir = (ImageView) findViewById(R.id.botonCompartir);
                    compartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Inicio.class);
                            i.putExtra("sitio_turistico", title);
                            startActivity(i);
                        }
                    });

                    ImageView ubicar = (ImageView) findViewById(R.id.botonUbicar);
                    ubicar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, GeolocalizacionPunto.class);
                            i.putExtra("sitio_turistico", title);
                            startActivity(i);
                        }
                    });

                    circuloProgreso.dismiss();

                }
            });

            if (!"".equals(datos.get("imagen").toString()) && !datos.get("imagen").toString().equals(null)) {
                Log.v("image", "http://" + datos.get("imagen").toString());
                String[] myTaskParams = {"http://" + datos.get("imagen").toString()};
                asyncTask.execute(myTaskParams);
            } else {
                String[] myTaskParams = {};
                asyncTask.execute(myTaskParams);
            }

            this.setTitle(title);

            TextView descripcion = (TextView) findViewById(R.id.textViewtab1);
            descripcion.setText(datos.get("descripcion").toString());

            TextView datosContacto = (TextView) findViewById(R.id.textViewtab2);

            if (!"null".equals(datos.get("direccion").toString())) {
                datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato1) + " " + datos.get("direccion").toString() + "\n");
            }
            if (!"null".equals(datos.get("telefono").toString())) {
                datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato2) + " " + datos.get("telefono").toString() + "\n");
            }
            if (!"null".equals(datos.get("email").toString())) {
                datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato3) + " " + datos.get("email").toString() + "\n");
            }
            if (!"null".equals(datos.get("site").toString())) {
                datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato4) + " " + datos.get("site").toString() + "\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.text_tab1));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.text_tab2));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        TabWidget widget = tabs.getTabWidget();
        for (int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);

            // Look for the title view to ensure this is an indicator and not a divider.
            TextView tv = (TextView) v.findViewById(android.R.id.title);
            if (tv == null) {
                continue;
            }
            v.setBackgroundResource(R.drawable.tab_selector);
        }

        TextView x = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        x.setTextSize(10);
        x = (TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        x.setTextSize(10);
    }

}

