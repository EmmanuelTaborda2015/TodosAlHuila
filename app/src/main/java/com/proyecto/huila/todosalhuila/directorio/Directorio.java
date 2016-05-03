package com.proyecto.huila.todosalhuila.directorio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.geolocalizacion.Geolocalizacion;
import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.login.Login;
import com.proyecto.huila.todosalhuila.webservice.WS_Directorio;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONException;
import org.json.JSONObject;

public class Directorio extends AppCompatActivity {

    private NetworkStateReceiver networkStateReceiver;

    private ImageIndicatorView autoImageIndicatorView;

    private RelativeLayout connetion;

    int seleccion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.setTitle("Directorio de Sitios");

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        final WS_ValidarConexionGoogle asyncTask = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output == "false") {
                    connetion.setVisibility(View.VISIBLE);
                }
            }
        });
        asyncTask.execute();

        connetion.setVisibility(View.GONE);

        Button buscar = (Button) findViewById(R.id.buttonBuscarDirectorio);
        final EditText palabra = (EditText) findViewById(R.id.buscarPalabra);
        Spinner categoria = (Spinner) findViewById(R.id.spinnerCategoriaDirectorio);
        Spinner tipo = (Spinner) findViewById(R.id.spinnerTipoBusquedaDirectorio);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(palabra.getWindowToken(), 0);

                final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        if (output == "false") {
                            if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                sinConexion();
                            } else {
                                conexionNoValida();
                            }
                        } else {
                            if ("".equals(palabra.getText().toString())) {
                                ingresePalabra();
                            } else {


                                final WS_Directorio asyncTask = new WS_Directorio(new WS_Directorio.AsyncResponse() {

                                    @Override
                                    public void processFinish(String output) {


                                        try {
                                            final JSONObject datos;
                                            datos = new JSONObject(output);
                                            final JSONObject respuesta;
                                            respuesta = new JSONObject(datos.get("datos").toString());
                                            Log.v("respue",respuesta.toString());
                                            if ("correcto".equals(respuesta.get("estado").toString())) {

                                                //Intent i = new Intent(Login.this, InicioLogin.class);
                                                //startActivity(i);
                                                //finish();
                                            } else {
                                                //datosInvalidos();
                                            }

                                            //circuloProgreso.dismiss();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                                String[] myTaskParams = {"neiva", "", ""};
                                asyncTask.execute(myTaskParams);
                                // circuloProgreso = ProgressDialog.show(Login.this, "", "Espere por favor ...", true);
                            }

                        }
                    }
                }

                );
                asyncTaskConection.execute();

            }
        });

        categoria.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(palabra.getWindowToken(), 0);
                return false;
            }

        });

        tipo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(palabra.getWindowToken(), 0);
                return false;
            }
        });

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
        final int id = item.getItemId();

        if (id == R.id.action_salir) {
            salir();
        } else if (id == R.id.action_home) {
            Intent i = new Intent(Directorio.this, Inicio.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categorias, menu);
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
                        seleccion = 0;
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void ingresePalabra() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("Debe ingresar una palabra para iniciar la búsqueda.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }
}
