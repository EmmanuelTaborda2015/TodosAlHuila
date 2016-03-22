package com.proyecto.huila.todosalhuila.categorias;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.geolocalizacion.Informacion;
import com.proyecto.huila.todosalhuila.webservice.WS_MiPyme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Subcategorias extends AppCompatActivity {

    private ArrayList<TitularItemsSubcategorias> Items;

    private ArrayList<TitularItemsSubcategorias> ItemsSearch;

    private AdaptadorSubcategorias Adaptador;

    private ListView listaItems;

    private ProgressDialog circuloProgreso;

    private String categoria;

    FrameLayout fm_search;

    ArrayList<String> lista;

    AutoCompleteTextView textView;

    int parmSearch = -1;

    ArrayList<Integer> posicionInicial;
    ArrayList<Integer> posicionActual;
    ArrayList<Integer> posicionFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        this.categoria = intent.getStringExtra("subcategoria");

        this.setTitle(categoria);

        textView = (AutoCompleteTextView) findViewById(R.id.search);

        listaItems = (ListView) findViewById(R.id.listItems);

        Items = new ArrayList<TitularItemsSubcategorias>();

        ItemsSearch = new ArrayList<TitularItemsSubcategorias>();

        lista = new ArrayList<String>();

        posicionInicial = new ArrayList<Integer>();
        posicionActual = new ArrayList<Integer>();
        posicionFinal = new ArrayList<Integer>();

        fm_search = (FrameLayout) findViewById(R.id.fram_search);
        fm_search.setVisibility(View.GONE);

        Button buscar = (Button) findViewById(R.id.searchBtn);
        Button cerrar = (Button) findViewById(R.id.delete);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaItems.setAdapter(null);
                ItemsSearch = new ArrayList<TitularItemsSubcategorias>();

                if (parmSearch > -1) {
                    for (int i = 0; i < Items.size(); i++) {
                        if (lista.get(parmSearch).equals(Items.get(i).getCiudad()) | lista.get(parmSearch).equals(Items.get(i).getTitle())) {
                            ItemsSearch.add(new TitularItemsSubcategorias(Items.get(i).getSitio(), Items.get(i).getTitle().toString(), Items.get(i).getDescription(), Items.get(i).getImg(), Items.get(i).getCiudad(), Items.get(i).getItem()));
                            Adaptador = new AdaptadorSubcategorias(Subcategorias.this, ItemsSearch);
                            posicionActual.add(Items.get(i).getItem());
                        }
                    }

                    posicionFinal = new ArrayList<Integer>();
                    posicionFinal = posicionActual;

                    listaItems.setAdapter(Adaptador);
                    textView.setText("");
                } else {
                    Adaptador = new AdaptadorSubcategorias(Subcategorias.this, Items);
                    listaItems.setAdapter(Adaptador);

                    posicionFinal = new ArrayList<Integer>();
                    posicionFinal = posicionInicial;

                    sinSeleccion();
                }

                parmSearch = -1;
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                fm_search.setVisibility(View.GONE);
            }
        });

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parmSearch = position;
            }
        });

        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        final WS_MiPyme asyncTask = new WS_MiPyme(new WS_MiPyme.AsyncResponse() {

            @Override
            public void processFinish(String output) {

                Items = new ArrayList<TitularItemsSubcategorias>();

                try {
                    JSONObject json = new JSONObject(output);
                    final JSONArray items = json.getJSONArray("getInformacion");

                    for (int i = 0; i < items.length(); i++) {
                        final JSONObject datos = new JSONObject(items.get(i).toString());
                        JSONObject ubicacion = new JSONObject(datos.get("ubicacion").toString());
                        JSONObject categorizacion = new JSONObject(datos.get("categorizacion").toString());
                        if (categoria.equals(categorizacion.get("subcategoria").toString())) {
                            String ciudad = updateCityAndPincode(Double.parseDouble(ubicacion.get("latitud").toString()), Double.parseDouble(ubicacion.get("longitud").toString()));
                            lista.add(ciudad.toString());
                            lista.add(datos.getString("nombre").toString());
                            posicionInicial.add(i);
                            Items.add(new TitularItemsSubcategorias(datos.getString("id").toString(), datos.getString("nombre").toString(), categorizacion.get("subcategoria").toString(), datos.getString("imagen").toString(), ciudad, i));
                        }
                    }

                    posicionFinal = new ArrayList<Integer>();
                    posicionFinal = posicionInicial;

                    if (Items.size() > 0) {
                        Adaptador = new AdaptadorSubcategorias(Subcategorias.this, Items);
                        listaItems.setAdapter(Adaptador);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Subcategorias.this,
                                android.R.layout.simple_dropdown_item_1line, lista);
                        textView.setAdapter(adapter);

                        listaItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                Intent i = new Intent(Subcategorias.this, Informacion.class);
                                try {
                                    i.putExtra("datos", items.get(posicionFinal.get(position)).toString());
                                    startActivity(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        sinElementos();
                    }

                    circuloProgreso.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        asyncTask.execute();
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

    private String updateCityAndPincode(double latitude, double longitude) {
        try {
            Geocoder gcd = new Geocoder(Subcategorias.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //lugar.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAddressLine(0));
                Log.v("address", addresses.get(0).getLocality());
                return addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            Log.e("TAG Subcategorias", "exception:" + e.toString());
        }

        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_atras) {
            Intent i = new Intent(Subcategorias.this, Categorias.class);
            startActivity(i);
            finish();
        } else if (id == R.id.action_buscar) {
            fm_search.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subcategorias, menu);
        return true;
    }

}
