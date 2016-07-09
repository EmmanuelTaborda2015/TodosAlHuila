package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GeolocalizacionPunto extends AppCompatActivity
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

    private String AppMaps = "googlemaps";

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

    boolean login = false;

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
    //@Bind(R.id.gl_tooglebutton_segmento16)
    //ToggleButton segmento16;
    @Bind(R.id.layout_categorias)
    HorizontalScrollView layoutcategorias;
    @Bind(R.id.imageview_punto)
    ImageView imageviewpunto;

    private String output;

    private String pyme;

    ToggleButton[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_localizacion);

        ButterKnife.bind(this);

        layoutcategorias.setVisibility(View.GONE);

        final Intent intent = getIntent();
        this.login = intent.getBooleanExtra("login", false);
        this.pyme = intent.getStringExtra("pyme");


        buttons = new ToggleButton[]{segmento1, segmento2, segmento3, segmento4, segmento5, segmento6, segmento7, segmento8, segmento9, segmento10, segmento11, segmento12, segmento13, segmento14, segmento15/*, segmento16*/};

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

        this.setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Se instancia Google Map
        buildGoogleApiClient();

        createLocationRequest();

        buildLocationSettingsRequest();

    }

    public void dibujarMarker(String output) {

        this.output = output;

        JSONObject json = null;

        try {

            final JSONObject datos = new JSONObject(output);

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
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment1));
                    }
                    break;
                case "Turismo de aventura":
                    if (segmento2.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg2;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment2));
                    }
                    break;
                case "Turismo rural":
                    if (segmento3.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg3;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment3));
                    }
                    break;
                case "Atractivos turísticos":
                    if (segmento4.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg4;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment4));
                    }
                    break;
                case "Gastronomía":
                    if (segmento5.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg5;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment5));
                    }
                    break;
                case "Cultura":
                    if (segmento6.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg6;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment6));
                    }
                    break;
                case "Salud y bienestar":
                    if (segmento7.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg7;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment7));
                    }
                    break;
                case "Turismo corporativo":
                    if (segmento8.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg8;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment8));
                    }
                    break;
                case "Sol y playa":
                    if (segmento9.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg9;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment9));
                    }
                    break;
                case "Astronomía":
                    if (segmento10.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg10;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment10));
                    }
                    break;
                case "Bares y Pubs":
                    if (segmento11.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg11;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment11));
                    }
                    break;
                case "Comercio":
                    if (segmento12.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg12;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment12));
                    }
                    break;
                case "Hospedaje":
                    if (segmento13.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg13;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment13));
                    }
                    break;
                case "Recreación":
                    if (segmento14.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg14;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment14));
                    }
                    break;
                case "Transporte":
                    if (segmento15.isChecked() == true) {
                        dibujar = true;
                        icon = R.drawable.seg15;
                        imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segment15));
                    }
                    break;
               // case "Municipios":
                 //   if (segmento16.isChecked() == true) {
                   //     dibujar = true;
                     //   icon = R.drawable.seg16;
                       // imageviewpunto.setImageDrawable(getResources().getDrawable(R.drawable.segmento16));
                    //}
                    //break;
            }

            if (dibujar == true) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString())))
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .snippet(categorizacion.getString("subcategoria"))
                        .title(datos.getString("nombre").toString()));
                mHashMap.put(marker, 0);

                previoMarker = marker;

                selectMarker = true;

                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString()))));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString())), 12));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString())), 12));
                //mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);


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
        final int id = item.getItemId();

        if (id == R.id.action_regresar) {
            finish();
        }

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

                        if (id == R.id.action_miubicacion) {
                            contador = 0;
                            checkLocationSettings();
                        } else if (id == R.id.action_llegarcaminando) {
                            if (selectMarker) {
                                //Uri gmmIntentUri = Uri.parse("google.navigation:q=" + previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "&mode=w");
                                //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                //mapIntent.setPackage("com.google.android.apps.maps");
                                //startActivity(mapIntent);

                                if(isGoogleMapsInstalled() ){
                                    String uri = "http://maps.google.com/maps?daddr="+ previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "&dirflg=w";
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }else{
                                    sinGoogleMaps();
                                }

                            } else {
                                new AlertDialog.Builder(GeolocalizacionPunto.this)
                                        .setTitle(R.string.tituloLlegarCaminando)
                                        .setMessage(R.string.mensajeLlegarCaminando)
                                        .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        } else if (id == R.id.action_llegarconduciendo) {
                            if (selectMarker) {

                                dialogoAppMaps();

                            } else {
                                new AlertDialog.Builder(GeolocalizacionPunto.this)
                                        .setTitle(R.string.tituloLlegarConduciendo)
                                        .setMessage(R.string.mensajeLlegarConduciendo)
                                        .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        } else if (id == R.id.action_ubicacion) {
                            ubicacionEspecifica();
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
        getMenuInflater().inflate(R.menu.menu_geolocalizacion_punto, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        dibujarMarker(pyme);
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

    public void ubicacionEspecifica() {
        mMap.clear();
        dibujarMarker(pyme);
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
                    status.startResolutionForResult(GeolocalizacionPunto.this, REQUEST_CHECK_SETTINGS);
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
            Geocoder gcd = new Geocoder(GeolocalizacionPunto.this, Locale.getDefault());
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

    public void sinGoogleMaps() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloFuncionDeshabilitada)
                .setMessage(R.string.sinGoogle)
                .setPositiveButton(R.string.botonInstalar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                        startActivity(a);
                    }
                })
                .setNegativeButton(R.string.botonCerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (login) {
                            Intent i = new Intent(GeolocalizacionPunto.this, InicioLogin.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(GeolocalizacionPunto.this, Inicio.class);
                            startActivity(i);
                            finish();
                        }
                    }
                })
                .show();
    }

    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public DialogInterface.OnClickListener getGoogleMapsListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);

                //Finish the activity so they can't circumvent the check
                finish();
            }
        };
    }

    public void sinWaze() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloFuncionDeshabilitada)
                .setMessage(R.string.sinWaze)
                .setPositiveButton(R.string.botonInstalar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent =
                                new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.botonCerrar,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void dialogoAppMaps(){

        final Dialog dialog = new Dialog(GeolocalizacionPunto.this);
        dialog.setContentView(R.layout.seleccionar_app_mapas);
        dialog.setTitle(R.string.appNavegacion);
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.group_radiobutton);

        dialog.show();

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rd_googlemaps:
                        AppMaps = "googlemaps";
                        break;
                    case R.id.rd_waze:
                        AppMaps = "waze";
                        break;
                }

            }
        });

        Button btnsubmit = (Button) dialog.findViewById(R.id.button_mapas);
        btnsubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if("googlemaps".equals(AppMaps)){
                    if(isGoogleMapsInstalled() ){
                        String uri = "http://maps.google.com/maps?daddr="+ previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "&dirflg=d";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }else{
                        sinGoogleMaps();
                    }
                }else if ("waze".equals(AppMaps)){
                    try
                    {
                        String url = "waze://?ll=" + previoMarker.getPosition().latitude + "," + previoMarker.getPosition().longitude + "navigate=yes";
                        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                        startActivity( intent );

                    }
                    catch ( ActivityNotFoundException ex  )
                    {
                        sinWaze();
                    }
                }

                dialog.dismiss();

            }
        });
    }

}