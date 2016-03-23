package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_MiPyme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Geolocalizacion extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>, NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }


    private static final String TAG = "Inicio";

    int seleccion = 0;

    private ImageIndicatorView autoImageIndicatorView;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleMap mMap;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    protected Location mCurrentLocation;

    protected Boolean mRequestingLocationUpdates;

    protected String mLastUpdateTime;


    int RQS_GooglePlayServices = 0;

    int contador = 0;

    private Marker previoMarker;
    private int showMarker = 0;
    private boolean selectMarker = false;

    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();


    @Bind(R.id.gl_tooglebutton_segmento1)
    ToggleButton segmento1;
    @Bind(R.id.gl_tooglebutton_segmento2)
    ToggleButton segmento2;
    @Bind(R.id.gl_tooglebutton_segmento3)
    ToggleButton segmento3;
    @Bind(R.id.gl_tooglebutton_segmento4)
    ToggleButton segmento4;
    @Bind(R.id.gl_tooglebutton_segmento5)
    ToggleButton segmento5;
    @Bind(R.id.gl_tooglebutton_segmento6)
    ToggleButton segmento6;
    @Bind(R.id.gl_tooglebutton_segmento7)
    ToggleButton segmento7;
    @Bind(R.id.gl_tooglebutton_segmento8)
    ToggleButton segmento8;
    @Bind(R.id.gl_tooglebutton_segmento9)
    ToggleButton segmento9;
    @Bind(R.id.gl_tooglebutton_segmento10)
    ToggleButton segmento10;
    @Bind(R.id.gl_tooglebutton_segmento11)
    ToggleButton segmento11;
    @Bind(R.id.gl_tooglebutton_segmento12)
    ToggleButton segmento12;
    @Bind(R.id.gl_tooglebutton_segmento13)
    ToggleButton segmento13;
    @Bind(R.id.gl_tooglebutton_segmento14)
    ToggleButton segmento14;
    @Bind(R.id.gl_tooglebutton_segmento15)
    ToggleButton segmento15;
    @Bind(R.id.gl_tooglebutton_segmento16)
    ToggleButton segmento16;

    private String output;

    ToggleButton[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_localizacion);

        ButterKnife.bind(this);

        buttons = new ToggleButton[]{segmento1, segmento2, segmento3, segmento4, segmento5, segmento6, segmento7, segmento8, segmento9, segmento10, segmento11, segmento12, segmento13, segmento14, segmento15, segmento16};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        if (new NetworkUtil().isOnline() == false) {
            connetion.setVisibility(View.VISIBLE);
        }

        connetion.setVisibility(View.GONE);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        this.setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Se instancia Google Map
        buildGoogleApiClient();

        createLocationRequest();

        buildLocationSettingsRequest();

        final WS_MiPyme asyncTask = new WS_MiPyme(new WS_MiPyme.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                dibujarMarker(output);
            }
        });

        asyncTask.execute();

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dibujarMarker(output);
                }
            });
        }

    }

    public void dibujarMarker(String output) {

        this.output = output;

        JSONObject json = null;

        mMap.clear();

        try {
            json = new JSONObject(output);
            final JSONArray items = json.getJSONArray("getInformacion");
            for (int i = 0; i < items.length(); i++) {
                final JSONObject datos = new JSONObject(items.get(i).toString());
                JSONObject ubicacion = new JSONObject(datos.get("ubicacion").toString());
                JSONObject categorizacion = new JSONObject(datos.get("categorizacion").toString());

                boolean dibujar = false;
                int icon = 0;

                String segmento = categorizacion.getString("segmento");

                switch (segmento) {
                    case "Ecoturismo":
                        if (segmento1.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg1;
                        }
                        break;
                    case "Turismo de aventura":
                        if (segmento2.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg2;
                        }
                        break;
                    case "Turismo rural":
                        if (segmento3.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg3;
                        }
                        break;
                    case "Atractivos turísticos":
                        if (segmento4.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg4;
                        }
                        break;
                    case "Gastronomia":
                        if (segmento5.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg5;
                        }
                        break;
                    case "Cultura":
                        if (segmento6.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg6;
                        }
                        break;
                    case "Salud y bienestar":
                        if (segmento7.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg7;
                        }
                        break;
                    case "Turismo corporativo":
                        if (segmento8.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg8;
                        }
                        break;
                    case "Sol y playa":
                        if (segmento9.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg9;
                        }
                        break;
                    case "Astronomía":
                        if (segmento10.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg10;
                        }
                        break;
                    case "Bares y Pubs":
                        if (segmento11.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg11;
                        }
                        break;
                    case "Comercio":
                        if (segmento12.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg12;
                        }
                        break;
                    case "Hospedaje":
                        if (segmento13.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg12;
                        }
                        break;
                    case "Recreación":
                        if (segmento14.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg14;
                        }
                        break;
                    case "Transporte":
                        if (segmento15.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg15;
                        }
                        break;
                    case "Municipios":
                        if (segmento16.isChecked() == true) {
                            dibujar = true;
                            icon = R.drawable.seg16;
                        }
                        break;
                }

                if (dibujar == true) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString())))
                            .icon(BitmapDescriptorFactory.fromResource(icon))
                            .snippet(categorizacion.getString("subcategoria"))
                            .title(datos.getString("nombre").toString()));
                    mHashMap.put(marker, i);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker arg0) {

                            if (arg0.equals(previoMarker) && showMarker > 0) {
                                arg0.hideInfoWindow();
                                showMarker = 0;
                            } else {
                                int zoom = (int) mMap.getCameraPosition().zoom;

                                //if (zoom <= 7) {
                                //    zoom = 13;
                                //}

                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getPosition().latitude + (double) 90 / Math.pow(2, zoom), arg0.getPosition().longitude), zoom);
                                mMap.moveCamera(cu);
                                arg0.showInfoWindow();
                                showMarker++;
                            }
                            previoMarker = arg0;
                            selectMarker = true;
                            return true;
                        }
                    });
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            try {
                                if(seleccion==0) {
                                    seleccion++;
                                    if (new NetworkUtil().isOnline() == false) {
                                        if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                            sinConexion();
                                        } else {
                                            conexionNoValida();
                                        }
                                    } else {
                                        seleccion = 0;
                                        Intent i = new Intent(Geolocalizacion.this, Informacion.class);
                                        i.putExtra("datos", items.get(mHashMap.get(marker)).toString());
                                        startActivity(i);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (seleccion == 0) {
            seleccion++;
            if (new NetworkUtil().isOnline() == false) {
                if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                    sinConexion();
                } else {
                    conexionNoValida();
                }
            } else {

                if (id == R.id.action_miubicacion) {
                    contador = 0;
                    checkLocationSettings();
                } else if (id == R.id.action_llegarcaminando) {
                    if (selectMarker) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "&mode=w");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("¿Cómo Llegar Caminando?")
                                .setMessage("Para encontrar las rutas que puede tomar caminando, primero debe seleccionar el destino.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                } else if (id == R.id.action_llegarconduciendo) {
                    if (selectMarker) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "&mode=d");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("¿Cómo Llegar Conduciendo?")
                                .setMessage("Para encontrar las rutas que puede tomar conduciendo, primero debe seleccionar el destino.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                } else if (id == R.id.action_home) {
                    if (new Login().login == true) {
                        Intent i = new Intent(Geolocalizacion.this, InicioLogin.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(Geolocalizacion.this, Inicio.class);
                        startActivity(i);
                        finish();
                    }
                } else if (id == R.id.action_ubicacion) {
                    ubicacionEspecifica();
                }

                seleccion = 0;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geolocalizacion, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ubicacionEspecifica();
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

    public void ubicacionEspecifica() {
        LatLng huila = new LatLng(2.92504, -75.2897);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(huila));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huila, 12));
        //mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                //     setButtonsEnabledState();
            }
        });

    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (contador == 0) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.v("coordenadas", "aquí");
            updateLocationUI();

            LatLng mylocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(mylocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
            final CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mylocation)
                    .zoom(15)
                    .tilt(30)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            contador++;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Invoked when settings dialog is opened and action taken
     *
     * @param locationSettingsResult This below OnResult will be used by settings dialog actions.
     */

    //step 5
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(Geolocalizacion.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            updateCityAndPincode(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }

    private void updateCityAndPincode(double latitude, double longitude) {
        try {
            Geocoder gcd = new Geocoder(Geolocalizacion.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //lugar.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
            }
        } catch (Exception e) {
            Log.e(TAG, "exception:" + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
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