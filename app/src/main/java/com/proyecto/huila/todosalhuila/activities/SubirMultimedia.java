package com.proyecto.huila.todosalhuila.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.webservice.WS_SubirArchivo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubirMultimedia extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

    @Bind(R.id.sf_edittex_titulo)
    EditText titulo;

    @Bind(R.id.sf_editText_descripcion)
    EditText descripcion;

    @Bind(R.id.sf_editText_lugar)
    EditText lugar;

    @Bind(R.id.sf_textview_lugar)
    TextView txtlugar;

    @Bind(R.id.sf_button_cargar)
    Button cargar;

    @Bind(R.id.sf_button_cancelar)
    Button cancelar;

    @Bind(R.id.sf_imageview_ubicacion)
    ImageView ubicacion;

    public static final String INTENT_PATH = "INTENT_PATH";
    public static final String INTENT_TYPE_FILE = "INTENT_TYPE_FILE";

    private ProgressDialog circuloProgreso;

    protected static final String TAG = "MainActivity";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    protected Location mCurrentLocation;

    protected Boolean mRequestingLocationUpdates;

    protected String mLastUpdateTime;


    int RQS_GooglePlayServices = 0;

    int contador = 0;

    private String path;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_subir_multimedia);

        ButterKnife.bind(this);

        final WS_SubirArchivo asyncTask =new WS_SubirArchivo(new WS_SubirArchivo.AsyncResponse() {

            @Override
            public void processFinish(String output) {
                circuloProgreso.dismiss();
                Intent i = new Intent(SubirMultimedia.this, CameraActivity.class);
                startActivity(i);
                finish();
            }
        });

        lugar.setVisibility(View.GONE);
        txtlugar.setVisibility(View.GONE);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        path = getIntent().getStringExtra(INTENT_PATH);
        type = getIntent().getIntExtra(INTENT_TYPE_FILE, 0);

        buildGoogleApiClient();

        createLocationRequest();

        buildLocationSettingsRequest();

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador=0;
                checkLocationSettings();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                circuloProgreso = ProgressDialog.show(SubirMultimedia.this, "", "Subiendo Archivo ...", true);

                String ImageBase64 = convertFileToString(path);

                String[] myTaskParams = new String[0];

                try {

                    String title = java.net.URLEncoder.encode(String.valueOf(titulo.getText()), "utf-8");
                    String caption = java.net.URLEncoder.encode(String.valueOf(descripcion.getText()), "utf-8");

                    if (contador == 0) {
                        myTaskParams = new String[]{ImageBase64, title, caption, "", "", ""};
                    } else {
                        String address = java.net.URLEncoder.encode(String.valueOf(lugar.getText()), "utf-8");
                        String latitude = java.net.URLEncoder.encode(String.valueOf(mCurrentLocation.getLatitude()), "utf-8");
                        String longitude = java.net.URLEncoder.encode(String.valueOf(mCurrentLocation.getLongitude()), "utf-8");
                        myTaskParams = new String[]{ImageBase64, title, caption, address, latitude, longitude};
                    }
                    asyncTask.execute(myTaskParams);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String convertFileToString(String pathOnSdCard){

        String strFile=null;
        File file=new File(pathOnSdCard);
        try {
            byte[] data = FileUtils.readFileToByteArray(file);//Convert any file, image or video into byte array
            strFile = Base64.encodeToString(data, Base64.DEFAULT);//Convert byte array into string
        } catch (IOException e) {

            e.printStackTrace();
        }
        return strFile;
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
                //   setButtonsEnabledState();
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
    public void onLocationChanged(Location location) {Log.v("Coorde", "aqui");
        if (contador == 0) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
            lugar.setVisibility(View.VISIBLE);
            txtlugar.setVisibility(View.VISIBLE);
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
                    status.startResolutionForResult(SubirMultimedia.this, REQUEST_CHECK_SETTINGS);
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
            Geocoder gcd = new Geocoder(SubirMultimedia.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                lugar.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
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