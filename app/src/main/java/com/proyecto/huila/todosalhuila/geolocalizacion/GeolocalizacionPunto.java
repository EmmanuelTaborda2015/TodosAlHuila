package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
import com.proyecto.huila.todosalhuila.webservice.WS_Marcador;

import java.util.ArrayList;
import java.util.List;

public class GeolocalizacionPunto extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<String> marker = new ArrayList<String>();

    private final int zoomMarker = 17;
    private String type = "ALL";

    private Handler handler_marcador = new Handler();
    private Thread thread_marcador;
    private WS_Marcador webResponseMarcador;

    private ProgressDialog circuloProgreso;

    private String sitio_turistico = "";
    private String nombre_sitio_turistico = "";
    private String tipo_sitio_turistico = "";
    private String coord_x = "";
    private String coord_y = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocalizacion_punto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        type= intent.getStringExtra("sitio_turistico");

        this.setTitle(intent.getStringExtra("nombre_sitio_turistico"));

        //Se genera el llamado al web service que enviara los marcadores presentes en la base de datos.
        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        thread_marcador = new Thread() {
            public void run() {
                Looper.prepare();
                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                //webResponseMarcador = new WS_Marcador();
                //webResponseMarcador.startWebAccess("usuario", id_dispositivo, type);
                handler_marcador.post(marcador);
            }
        };

        thread_marcador.start();
        ////////////////////////////////////////////////////////////////////////////////////////////////
    }

        //Desde este punto se realiza la implementacion de los marcadores de Google Map.


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng huila = new LatLng(2.92504, -75.2897);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(huila));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huila, zoomMarker));
        mMap.isTrafficEnabled();
    }


    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable marcador = new Runnable() {
        public void run() {

            sitio_turistico = webResponseMarcador.getSitioTuristico().get(0);
            tipo_sitio_turistico = webResponseMarcador.getTipoSitioTuristico().get(0);
            nombre_sitio_turistico = webResponseMarcador.getNombreSitioTuristico().get(0);
            coord_x = webResponseMarcador.getCoordX().get(0);
            coord_y = webResponseMarcador.getCoordY().get(0);

            int imagen = 0;

            if ("1".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_hotel;
            } else if ("2".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_restaurante;
            } else if ("3".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_museo;
            } else if ("4".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_cencom;
            } else if ("5".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_disco;
            } else if ("6".equals(tipo_sitio_turistico)) {
                imagen = R.drawable.ubica_iglesia;
            }

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(coord_x), Double.parseDouble(coord_y)))
                    .icon(BitmapDescriptorFactory.fromResource(imagen))
                    .title(nombre_sitio_turistico));

            marker.showInfoWindow();

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(coord_x), Double.parseDouble(coord_y)), zoomMarker);
            mMap.moveCamera(cu);

            circuloProgreso.dismiss();

        }

        ////////////////////////////////////////////////////////////////////////////////////////////
    };
}
