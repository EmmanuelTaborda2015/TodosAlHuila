package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.categorias.Categorias;
import com.proyecto.huila.todosalhuila.herramientas.ServicioWeb;
import com.proyecto.huila.todosalhuila.menu.Agenda;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.webservice.WS_Marcador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeolocalizacionLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private List<String> marker = new ArrayList<String>();
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    private Marker previoMarker;
    private int showMarker = 0;

    static ServicioWeb prueba;

    private final String type = "ALL";

    private Handler handler_marcador = new Handler();
    private Thread thread_marcador;
    private ServicioWeb webResponseMarcador;

    private ProgressDialog circuloProgreso;

    private Context context;

    private List<String> sitio_turistico = new ArrayList<String>();
    private List<String> nombre_sitio_turistico = new ArrayList<String>();
    private List<String> tipo_sitio_turistico = new ArrayList<String>();
    private List<String> coord_x = new ArrayList<String>();
    private List<String> coord_y = new ArrayList<String>();

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

        //Se genera el llamado al web service que enviara los marcadores presentes en la base de datos.
        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);


        GeolocalizacionLogin.this.runOnUiThread(new Runnable() {
            public void run() {
                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                webResponseMarcador = new WS_Marcador(GeolocalizacionLogin.this, "usuario", id_dispositivo, type);
                webResponseMarcador.startWebAccess();
                handler_marcador.post(marcador);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////
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
            Intent i = new Intent(GeolocalizacionLogin.this, Inicio.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu2) {
            Intent i = new Intent(GeolocalizacionLogin.this, Categorias.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu3) {
            Intent i = new Intent(GeolocalizacionLogin.this, GeolocalizacionLogin.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu4) {
            Intent i = new Intent(GeolocalizacionLogin.this, Agenda.class);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(huila));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huila, 13));
        mMap.isTrafficEnabled();
        mMap.setMyLocationEnabled(true);
    }



    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable marcador = new Runnable() {
        public void run() {

            sitio_turistico = webResponseMarcador.getSitioTuristico();
            tipo_sitio_turistico = webResponseMarcador.getTipoSitioTuristico();
            nombre_sitio_turistico = webResponseMarcador.getNombreSitioTuristico();
            coord_x = webResponseMarcador.getCoordX();
            coord_y = webResponseMarcador.getCoordY();

            int imagen = 0;

            for(int i=0; i < sitio_turistico.size(); i++){
                if("1".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_hotel;
                }else if ("2".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_restaurante;
                }else if ("3".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_museo;
                }else if ("4".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_cencom;
                }else if ("5".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_disco;
                }else if ("6".equals(tipo_sitio_turistico.get(i))){
                    imagen = R.drawable.ubica_iglesia;
                }

                Marker marker =mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(coord_x.get(i)), Double.parseDouble(coord_y.get(i))))
                        .icon(BitmapDescriptorFactory.fromResource(imagen))
                        .title(nombre_sitio_turistico.get(i)));

                mHashMap.put(marker, Integer.parseInt(sitio_turistico.get(i)));
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {

                    if (arg0.equals(previoMarker) && showMarker > 0) {
                        arg0.hideInfoWindow();
                        showMarker = 0;
                    } else {

                        int zoom = (int)mMap.getCameraPosition().zoom;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getPosition().latitude + (double)90/Math.pow(2, zoom), arg0.getPosition().longitude), zoom);
                        mMap.moveCamera(cu);
                        arg0.showInfoWindow();
                        showMarker++;
                    }

                    previoMarker = arg0;

                    return true;
                }

            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent i = new Intent(GeolocalizacionLogin.this, Informacion.class);
                    i.putExtra("sitio_turistico", mHashMap.get(marker).toString());
                    i.putExtra("nombre_sitio_turistico", marker.getTitle());
                    startActivity(i);
                }
            });

            circuloProgreso.dismiss();

        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

}
