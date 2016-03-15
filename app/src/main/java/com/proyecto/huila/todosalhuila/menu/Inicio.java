package com.proyecto.huila.todosalhuila.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.proyecto.huila.indicador.AutoPlayManager;
import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.activities.CameraActivity;
import com.proyecto.huila.todosalhuila.categorias.Categorias;
import com.proyecto.huila.todosalhuila.geolocalizacion.Geolocalizacion;
import com.proyecto.huila.todosalhuila.geolocalizacion.Informacion;
import com.proyecto.huila.todosalhuila.webservice.WS_MiPyme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Inicio extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String TAG = "Inicio";

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

    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        this.setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Se instancia Google Map
        buildGoogleApiClient();

        createLocationRequest();

        buildLocationSettingsRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //contador = 0;

        //checkLocationSettings();prue

        final WS_MiPyme asyncTask = new WS_MiPyme(new WS_MiPyme.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                JSONObject json = null;
                try {
                    json = new JSONObject(output);
                    final JSONArray items = json.getJSONArray("getInformacion");
                    for(int i=0; i<items.length(); i++){
                        final JSONObject datos = new JSONObject(items.get(i).toString());
                        JSONObject ubicacion = new JSONObject(datos.get("ubicacion").toString());
                        JSONObject categorizacion = new JSONObject(datos.get("categorizacion").toString());

                        Marker marker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString())))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.municipios))
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
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getPosition().latitude + (double) 90 / Math.pow(2, zoom), arg0.getPosition().longitude), zoom);
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
                                try {
                                    Intent i = new Intent(Inicio.this, Informacion.class);
                                    i.putExtra("datos", items.get(mHashMap.get(marker)).toString());
                                    startActivity(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        asyncTask.execute();


        this.autoImageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);

        initView();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_user){
            Intent i = new Intent(Inicio.this, Login.class);
            startActivity(i);
        }else if(id==R.id.action_webpage){
            Intent i = new Intent(Inicio.this, Inicio.class);
            startActivity(i);
        }else if(id==R.id.action_home){
            Intent i = new Intent(Inicio.this, Inicio.class);
            startActivity(i);
        }else if(id==R.id.action_geolocalizacion){
            Intent i = new Intent(Inicio.this, Geolocalizacion.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng huila = new LatLng(2.92504, -75.2897);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(huila));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huila, 13));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                contador = 0;

                checkLocationSettings();

                return false;
            }
        });
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
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());Log.v("coordenadas", "aquí");
            updateLocationUI();
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
                    status.startResolutionForResult(Inicio.this, REQUEST_CHECK_SETTINGS);
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
            Geocoder gcd = new Geocoder(Inicio.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //lugar.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
            }
        } catch (Exception e) {
            Log.e(TAG, "exception:" + e.toString());
        }
    }

    @Override
    protected void onStart () {
        super.onStart();


        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}