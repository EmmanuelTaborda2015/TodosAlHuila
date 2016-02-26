package com.proyecto.huila.todosalhuila.menu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.proyecto.huila.todosalhuila.geolocalizacion.Geolocalizacion;
import com.proyecto.huila.todosalhuila.herramientas.AppStatus;
import com.proyecto.huila.todosalhuila.lista.Lugares;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexion;

public class Categorias extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog circuloProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppStatus app = new AppStatus(Categorias.this);
        Thread conexion = app.isOnline();

        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        while(conexion.isAlive()){
            Log.v("Corriendo", "si");
        }

        circuloProgreso.dismiss();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Aquí se generan los eventos de los botones correspondeintes a cada categoría.

        LinearLayout categoria1 = (LinearLayout) findViewById(R.id.hoteles);
        categoria1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria1));
                i.putExtra("categoria", "1");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout categoria2 = (LinearLayout) findViewById(R.id.restaurantes);
        categoria2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria2));
                i.putExtra("categoria", "2");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout categoria3 = (LinearLayout) findViewById(R.id.atractivos_turisticos);
        categoria3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria3));
                i.putExtra("categoria", "3");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout categoria4 = (LinearLayout) findViewById(R.id.centros_comerciales);
        categoria4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria4));
                i.putExtra("categoria", "4");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout categoria5 = (LinearLayout) findViewById(R.id.discotecas);
        categoria5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria5));
                i.putExtra("categoria", "5");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        LinearLayout categoria6 = (LinearLayout) findViewById(R.id.iglesias);
        categoria6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Categorias.this, Lugares.class);
                i.putExtra("nombre_categoria", getResources().getString(R.string.categoria6));
                i.putExtra("categoria", "6");
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.cerrar_aplicacion)
                    .setMessage(R.string.salir_aplicacion)
                    .setPositiveButton(R.string.opcionSi, new DialogInterface.OnClickListener()
                    {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu1) {
            Intent i = new Intent(Categorias.this, Inicio.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu2) {
            Intent i = new Intent(Categorias.this, Categorias.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu3) {
            Intent i = new Intent(Categorias.this, Geolocalizacion.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu4) {
            Intent i = new Intent(Categorias.this, Agenda.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu5) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
