package com.proyecto.huila.todosalhuila.comentarios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.Login;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.webservice.WS_ConsultarComentario;
import com.proyecto.huila.todosalhuila.webservice.WS_RegistrarComentario;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Comentarios extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }

    private ArrayList<TitularItemsComentarios> Items;

    private ArrayList<TitularItemsComentarios> ItemsSearch;

    private AdaptadorComentarios Adaptador;

    private ListView listaItems;

    private ProgressDialog circuloProgreso;

    int seleccion = 0;

    private String pyme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String con) {

                if (con=="false") {
                    connetion.setVisibility(View.VISIBLE);
                }
            }
        });
        asyncTaskConection.execute();

        connetion.setVisibility(View.GONE);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        final Intent intent = getIntent();
        pyme = intent.getStringExtra("pyme");

        Log.v("pyme", pyme);

        final EditText comentario = (EditText) findViewById(R.id.editText_comentar);
        ImageView enviar = (ImageView) findViewById(R.id.imageView_comentar);

        comentario.clearFocus();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comentario.getWindowToken(), 0);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comentar = comentario.getText().toString();
                if ("".equals(comentar)) {
                    Toast.makeText(Comentarios.this, "No hay comentarios para enviar", Toast.LENGTH_LONG).show();
                } else {

                    circuloProgreso = ProgressDialog.show(Comentarios.this, "", "Espere por favor ...", true);

                    final WS_RegistrarComentario asyncTask = new WS_RegistrarComentario(new WS_RegistrarComentario.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {

                            try {
                                JSONObject json = new JSONObject(output);
                                if("true".equals(json.getString("resultado").toString())){
                                    Toast.makeText(Comentarios.this, "Su comentarios ha sido registrado.", Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    Toast.makeText(Comentarios.this, "No se ha podido registrar su comentario, por favor intente nuevamente.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            circuloProgreso.dismiss();
                        }
                    });


                        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                        String params[] = new String[0];
                        params = new String[]{new Login().usuario, id_dispositivo, pyme, new Login().nombre, comentar};
                        asyncTask.execute(params);

                }
            }
        });

        Items = new ArrayList<TitularItemsComentarios>();


        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        final WS_ConsultarComentario asyncTask = new WS_ConsultarComentario(new WS_ConsultarComentario.AsyncResponse() {

            @Override
            public void processFinish(String output) {


                listaItems = (ListView) findViewById(R.id.listItems);

                Items = new ArrayList<TitularItemsComentarios>();

                try {
                    JSONObject json = new JSONObject(output);

                    final JSONArray items = json.getJSONArray("comentarios");

                    for (int i = 0; i < items.length(); i++) {
                        final JSONObject datos = new JSONObject(items.get(i).toString());

                        Items.add(new TitularItemsComentarios(datos.getString("nombre_turista").toString().trim(), datos.getString("comentario").toString().trim(), datos.getString("fecha_registro").toString().trim(), datos.getString("fecha_registro").toString()));
                    }

                    if (Items.size() > 0) {
                        Adaptador = new AdaptadorComentarios(Comentarios.this, Items);
                        listaItems.setAdapter(Adaptador);
                    } else {
                        sinElementos();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                circuloProgreso.dismiss();
            }
        });

        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        String params[] = new String[0];
            params = new String[]{new Login().usuario, id_dispositivo,pyme};
            asyncTask.execute(params);

    }

    public void sinElementos() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("No se ha encontrado ningún elemento para los criterios seleccionados.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public void sinSeleccion() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("No se ha seleccionado ninguna palabra de busqueda.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_atras) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atras, menu);
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

}
