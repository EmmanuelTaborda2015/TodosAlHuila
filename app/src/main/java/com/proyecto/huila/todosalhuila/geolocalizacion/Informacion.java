package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.comentarios.Comentarios;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.lista.Comentario;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.webservice.WS_SitioTuristico;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Informacion extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }


    private Handler handler_sitio = new Handler();
    private Thread thread_sitio;
    private WS_SitioTuristico webResponseSitio;

    private ProgressDialog circuloProgreso;

    private String pyme;

    private String title;

    private String mipyme;

    private int seleccion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String con) {

                if (con == "false") {
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
        this.pyme = intent.getStringExtra("datos");
        boolean login = intent.getBooleanExtra("login", false);

        RelativeLayout toolbar2 = (RelativeLayout) findViewById(R.id.toolbar2);

        if(!login){
            toolbar2.setVisibility(View.GONE);
        }

        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);


        final JSONObject datos;
        try {
            datos = new JSONObject(pyme);

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
                            if (seleccion == 0) {
                                seleccion++;
                                final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                                    @Override
                                    public void processFinish(String con) {

                                        if (con == "false") {
                                            if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                                sinConexion();
                                            } else {
                                                conexionNoValida();
                                            }
                                        } else {
                                            seleccion = 0;
                                            Intent i = new Intent(Informacion.this, Comentarios.class);
                                            i.putExtra("pyme", pyme);
                                            i.putExtra("pyme", mipyme);
                                            startActivity(i);
                                        }
                                    }
                                });
                                asyncTaskConection.execute();
                            }
                        }
                    });

                    ImageView calificar = (ImageView) findViewById(R.id.botonCalificar);
                    calificar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (seleccion == 0) {
                                seleccion++;
                                final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                                    @Override
                                    public void processFinish(String con) {

                                        if (con == "false") {
                                            if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                                sinConexion();
                                            } else {
                                                conexionNoValida();
                                            }
                                        } else {
                                            seleccion = 0;
                                            Intent i = new Intent(Informacion.this, Calificar.class);
                                            if (output.length > 0) {
                                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                                output[0].compress(Bitmap.CompressFormat.WEBP, 50, bs);
                                                i.putExtra("byteArray", bs.toByteArray());
                                            } else {
                                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.imagen_no_disponible);
                                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                                icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                                i.putExtra("byteArray", bs.toByteArray());
                                            }
                                            i.putExtra("mipyme", mipyme);
                                            i.putExtra("pyme", title);
                                            startActivity(i);
                                        }
                                    }
                                });
                                asyncTaskConection.execute();

                            }
                        }
                    });

                    ImageView ubicar = (ImageView) findViewById(R.id.botonUbicar);
                    ubicar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (seleccion == 0) {
                                seleccion++;
                                final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                                    @Override
                                    public void processFinish(String con) {

                                        if (con == "false") {
                                            if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                                sinConexion();
                                            } else {
                                                conexionNoValida();
                                            }
                                        } else {
                                            seleccion = 0;
                                            Intent i = new Intent(Informacion.this, GeolocalizacionPunto.class);
                                            i.putExtra("pyme", pyme);
                                            i.putExtra("login", true);
                                            startActivity(i);
                                        }
                                    }
                                });
                                asyncTaskConection.execute();

                            }
                        }
                    });

                    circuloProgreso.dismiss();

                }
            });

            ImageView atras = (ImageView) findViewById(R.id.botonAtras);
            atras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            int resultado = datos.get("imagen").toString().indexOf("private");

            if (!"".equals(datos.get("imagen").toString()) && !datos.get("imagen").toString().equals(null) && resultado == -1) {
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

            if("".equals(datosContacto.getText())){
                datosContacto.setText("Sin Información Disponible \n\nDatos de Contacto de la Gobernación \n\nDirección: Neiva-Huila-Colombia - Carrera 4 Calle 8 esquina\n PBX (57 + 8) 8671300  -  Línea gratuita 01 8000 968 716 \n Horario de Atención: Lunes a Jueves de 7:00 a.m. a 11:30 a.m. y de 2:00 p.m. a 5:30 p.m.\n" +
                        "Viernes de 7:00 a.m. a 11:30 a.m. y de 2:00 p.m. a 4:30 p.m.");
            }

            if("".equals(descripcion.getText())){
                descripcion.setText("Sin Información Disponible \n\nNuestra Visión \n\nEn el año 2020 el Huila será el corazón verde de Colombia, pacífico, solidario y emprendedor; líder de una región dinámica donde florecen los sueños de todos");

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

