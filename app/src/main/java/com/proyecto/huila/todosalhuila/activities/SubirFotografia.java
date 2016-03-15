package com.proyecto.huila.todosalhuila.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.galeria.MainActivity;

import java.io.IOException;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SubirFotografia extends Activity implements LocationListener {

    @Bind(R.id.sf_edittex_titulo)
    EditText titulo;

    @Bind(R.id.sf_editText_descripcion)
    EditText descripcion;

    @Bind(R.id.sf_button_cargar)
    Button cargar;

    @Bind(R.id.sf_button_cancelar)
    Button cancelar;

    private LocationListener locListener = null;
    AlertDialog alert = null;
    Context context;

    private String longitude;
    private String latitude;
    private String address;

    private int contador = 0;

    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_subir_fotografia);

        ButterKnife.bind(this);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                } else {
                    mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, SubirFotografia.this);

                    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        });
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address list = (Address) geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                address = list.getAddressLine(0);
                Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        @Override
        public void onLocationChanged(Location loc) {
            Log.v("prueba", "si");
            if(contador==0){
                latitude = String.valueOf(loc.getLatitude());
                longitude = String.valueOf(loc.getLongitude());
                setLocation(loc);
                titulo.setText(latitude);
                descripcion.setText(longitude);
                contador++;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(context, "GPS Desactivado", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(context, "GPS Activado", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


