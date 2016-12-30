package com.proyecto.huila.todosalhuila.inicio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.proyecto.huila.indicador.AutoPlayManager;
import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.activities.Multimedia;
import com.proyecto.huila.todosalhuila.categorias.Categorias;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.directorio.Directorio;
import com.proyecto.huila.todosalhuila.geolocalizacion.Geolocalizacion;
import com.proyecto.huila.todosalhuila.menu.Noticias;
import com.proyecto.huila.todosalhuila.productos.Productos;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;


public class InicioLogin extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    int seleccion = 0;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }

    private ImageIndicatorView autoImageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        this.setTitle("");

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

        this.autoImageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);

        initView();
    }

    private void initView() {
        final Integer[] resArray = new Integer[]{R.drawable.imagen1, R.drawable.imagen2, R.drawable.imagen3, R.drawable.imagen4, R.drawable.imagen5, R.drawable.imagen6};

        this.autoImageIndicatorView.setupLayoutByDrawable(resArray);
        this.autoImageIndicatorView.show();

        AutoPlayManager autoBrocastManager = new AutoPlayManager(this.autoImageIndicatorView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(5);
        autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);
        autoBrocastManager.loop();
    }

    @Override
    public void onBackPressed() {
        salir();
    }

    public void salir() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cerrar_aplicacion)
                .setMessage(R.string.salir_aplicacion)
                .setPositiveButton(R.string.opcionSi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton(R.string.opcionNo, null)
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        final int id = item.getItemId();

        if (id == R.id.action_salir) {
            salir();
        }else if  (id == R.id.action_mas) {
        }else if (seleccion == 0) {
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
                        if (id == R.id.action_noticias) {
                            Intent i = new Intent(InicioLogin.this, Noticias.class);
                            startActivity(i);
                            finish();
                        } else if (id == R.id.action_multimedia) {
                            Intent i = new Intent(InicioLogin.this, Multimedia.class);
                            startActivity(i);
                        } else if (id == R.id.action_categoria) {
                            Intent i = new Intent(InicioLogin.this, Categorias.class);
                            startActivity(i);
                            finish();
                        } else if (id == R.id.action_geolocalizacion) {
                            Intent i = new Intent(InicioLogin.this, Geolocalizacion.class);
                            i.putExtra("login", true);
                            startActivity(i);
                            finish();
                        }else if (id == R.id.action_directorio) {
                            Intent i = new Intent(InicioLogin.this, Directorio.class);
                            i.putExtra("login", false);
                            startActivity(i);
                            finish();
                        }else if (id == R.id.action_portal) {

                            //Intent i = new Intent(Inicio.this, Portal.class);
                            //i.putExtra("login", false);
                            //startActivity(i);
                            //finish();

                            AlertDialog.Builder builder = new AlertDialog.Builder(InicioLogin.this);
                            builder.setTitle(R.string.tituloMensaje);
                            builder.setMessage(R.string.mensajeIrPortal).setNeutralButton(R.string.botonAceptar,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("https://huila.travel/"));
                                            startActivity(intent);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            builder.create().show();

                        }else if (id == R.id.action_productos) {

                            Intent i = new Intent(InicioLogin.this, Productos.class);
                            startActivity(i);
                            finish();
                        }

                            seleccion = 0;
                    }
                }
            });
            asyncTaskConection.execute();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio_login, menu);

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
                        seleccion = 0;
                    }
                })
                .setCancelable(false)
                .show();
    }
}