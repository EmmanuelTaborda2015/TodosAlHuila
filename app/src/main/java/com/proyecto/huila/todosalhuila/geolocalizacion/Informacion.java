package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.lista.Comentario;
import com.proyecto.huila.todosalhuila.menu.Inicio;
import com.proyecto.huila.todosalhuila.webservice.WS_SitioTuristico;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class Informacion extends AppCompatActivity {

    private Handler handler_sitio = new Handler();
    private Thread thread_sitio;
    private WS_SitioTuristico webResponseSitio;

    private ProgressDialog circuloProgreso;

    private String sitio_turistico;
    private String nombre_sitio_turistico;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_informacion);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            final Intent intent = getIntent();
            this.nombre_sitio_turistico = intent.getStringExtra("nombre_sitio_turistico");
            this.sitio_turistico = intent.getStringExtra("sitio_turistico");

            this.setTitle(this.nombre_sitio_turistico);

            //Se genera el llamado al web service que enviara los marcadores presentes en la base de datos.
            circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

            thread_sitio = new Thread() {
                public void run() {
                    Looper.prepare();
                    String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                    webResponseSitio = new WS_SitioTuristico();
                    webResponseSitio.startWebAccess("usuario", id_dispositivo, intent.getStringExtra("sitio_turistico") );
                    handler_sitio.post(sitio);
                }
            };

            thread_sitio.start();
            ////////////////////////////////////////////////////////////////////////////////////////////////

            TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
            tabs.setup();

            TabHost.TabSpec spec=tabs.newTabSpec("tab1");
            spec.setContent(R.id.tab1);
            spec.setIndicator(getString(R.string.text_tab1));
            tabs.addTab(spec);

            spec=tabs.newTabSpec("tab2");
            spec.setContent(R.id.tab2);
            spec.setIndicator(getString(R.string.text_tab2));
            tabs.addTab(spec);

            //spec=tabs.newTabSpec("tab3");
            //spec.setContent(R.id.tab3);
            //spec.setIndicator(getString(R.string.text_tab3));
            //tabs.addTab(spec);

            tabs.setCurrentTab(0);

            TabWidget widget = tabs.getTabWidget();
            for(int i = 0; i < widget.getChildCount(); i++) {
                View v = widget.getChildAt(i);

                // Look for the title view to ensure this is an indicator and not a divider.
                TextView tv = (TextView)v.findViewById(android.R.id.title);
                if(tv == null) {
                    continue;
                }
                v.setBackgroundResource(R.drawable.tab_selector);
            }

            TextView x = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
            x.setTextSize(10);
            x = (TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
            x.setTextSize(10);
            //x = (TextView) tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
            //x.setTextSize(10);

        }


    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable sitio = new Runnable() {
        public void run() {

            LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                private ImageIndicatorViewUrl imageIndicatorView;

                @Override
                public void processFinish(final Bitmap[] output) {

                    this.imageIndicatorView = (ImageIndicatorViewUrl) findViewById(R.id.indicate_view);
                    Bitmap[] resArray = output;
                    if(resArray.length==0){
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                        resArray = new Bitmap[1];
                        resArray[0] =icon;
                    }
                    this.imageIndicatorView.setupLayoutByDrawable(resArray);
                    this.imageIndicatorView.show();

                    TextView descripcion = (TextView) findViewById(R.id.textViewtab1);
                    descripcion.setText(webResponseSitio.getDescripcion().get(0));

                    TextView datosContacto = (TextView) findViewById(R.id.textViewtab2);

                    String linea = webResponseSitio.getInformacionContacto().get(0);
                    String [] campos = linea.split(";");

                    int j = 0;
                    while(j<campos.length){
                        String [] dato = campos[j].split(",");
                        if("1".equals(dato[0])){
                            datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato1) +" "+ dato[1] + "\n");
                        }else if("2".equals(dato[0])){
                            datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato2) +" "+ dato[1] + "\n");
                        }else if("3".equals(dato[0])){
                            datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato3) +" "+ dato[1] + "\n");
                        }else if("4".equals(dato[0])){
                            datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato4) +" "+ dato[1] + "\n");
                        }else if("5".equals(dato[0])){
                            datosContacto.setText(datosContacto.getText() + getResources().getString(R.string.tipodato5) +" "+ dato[1] + "\n");
                        }
                        j++;
                    }

                    ImageView comentar = (ImageView) findViewById(R.id.botonComentar);
                    comentar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Comentario.class);
                            i.putExtra("sitio_turistico", sitio_turistico);
                            i.putExtra("nombre_sitio_turistico", nombre_sitio_turistico);
                            startActivity(i);
                        }
                    });

                    ImageView calificar = (ImageView) findViewById(R.id.botonCalificar);
                    calificar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Calificar.class);
                            if(output.length>0){
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                output[0].compress(Bitmap.CompressFormat.PNG, 50, bs);
                                i.putExtra("byteArray", bs.toByteArray());
                            }else{
                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                i.putExtra("byteArray", bs.toByteArray());
                            }
                            i.putExtra("sitio_turistico", sitio_turistico);
                            i.putExtra("nombre_sitio_turistico", nombre_sitio_turistico);
                            startActivity(i);
                        }
                    });

                    ImageView compartir = (ImageView) findViewById(R.id.botonCompartir);
                    compartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, Inicio.class);
                            i.putExtra("sitio_turistico", sitio_turistico);
                            i.putExtra("nombre_sitio_turistico", nombre_sitio_turistico);
                            startActivity(i);
                        }
                    });

                    ImageView ubicar = (ImageView) findViewById(R.id.botonUbicar);
                    ubicar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Informacion.this, GeolocalizacionPunto.class);
                            i.putExtra("sitio_turistico", sitio_turistico);
                            i.putExtra("nombre_sitio_turistico", nombre_sitio_turistico);
                            startActivity(i);
                        }
                    });

                    circuloProgreso.dismiss();
                }
            });

            if(!"".equals(webResponseSitio.getImagenes().get(0))){
                String linea = webResponseSitio.getImagenes().get(0);
                String [] campos = linea.split(";");

                int j = 0;
                String[] myTaskParams = new String[campos.length];
                while(j<campos.length) {
                    String[] dato = campos[j].split(",");
                    myTaskParams[j] = dato[1];
                    j++;
                }
                asyncTask.execute(myTaskParams);
            }else{
                String[] myTaskParams ={};
                asyncTask.execute(myTaskParams);
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

 }


