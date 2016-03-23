package com.proyecto.huila.todosalhuila.inicio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proyecto.huila.indicador.AutoPlayManager;
import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.geolocalizacion.Geolocalizacion;


public class Inicio extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        this.setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        if (new NetworkUtil().isOnline() == false) {
            connetion.setVisibility(View.VISIBLE);
        }

        connetion.setVisibility(View.GONE);

        this.autoImageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);

        initView();

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void initView() {
        final Integer[] resArray = new Integer[]{R.drawable.image2, R.drawable.image1, R.drawable.image3, R.drawable.image4};

        this.autoImageIndicatorView.setupLayoutByDrawable(resArray);
        this.autoImageIndicatorView.show();

        AutoPlayManager autoBrocastManager = new AutoPlayManager(this.autoImageIndicatorView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(5);
        autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);
        autoBrocastManager.loop();
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
                .show();
    }

    @Override
    public void onBackPressed() {
        salir();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_salir) {
            salir();
        }else if(seleccion == 0) {
            seleccion++;
            if (new NetworkUtil().isOnline() == false) {
                if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                    sinConexion();
                } else {
                    conexionNoValida();
                }
            } else {
                if (id == R.id.action_user) {
                    Intent i = new Intent(Inicio.this, Login.class);
                    startActivity(i);
                } else if (id == R.id.action_geolocalizacion) {
                    Intent i = new Intent(Inicio.this, Geolocalizacion.class);
                    startActivity(i);
                    finish();
                }
                seleccion=0;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
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
                        seleccion=0;
                    }
                })
                .setCancelable(false)
                .show();
    }

}