package com.proyecto.huila.todosalhuila.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proyecto.huila.todosalhuila.R;

public class Localizacion extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            Intent i = new Intent(Localizacion.this, Inicio.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu2) {
            Intent i = new Intent(Localizacion.this, Categorias.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu3) {
            Intent i = new Intent(Localizacion.this, Localizacion.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu4) {
            Intent i = new Intent(Localizacion.this, Agenda.class);
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

    //Desde este punto se realiza la implementacion de los marcadores de Google Map.


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng huila = new LatLng(2.92504, -75.2897);
        mMap.addMarker(new MarkerOptions().position(huila).title("Huila"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.logohuila3)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(huila));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huila, 13));
        mMap.isTrafficEnabled();

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(2.9370613, -75.2952122))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubica_museo))
                .title("Museo de Arte Contemporneo Del Huila"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(2.921914,-75.293802))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubica_museo))
                .title("Museo Prehistórico"));

    }

}
