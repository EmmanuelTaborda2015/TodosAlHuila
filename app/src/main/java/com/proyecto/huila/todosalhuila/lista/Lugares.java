package com.proyecto.huila.todosalhuila.lista;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.geolocalizacion.Informacion;
import com.proyecto.huila.todosalhuila.lista.Adaptador;
import com.proyecto.huila.todosalhuila.lista.TitularItems;
import com.proyecto.huila.todosalhuila.webservice.WS_ListaCategoria;
import com.proyecto.huila.todosalhuila.webservice.WS_SitioTuristico;

import java.util.ArrayList;

public class Lugares extends AppCompatActivity {

    private ArrayList<TitularItems> Items;
    private com.proyecto.huila.todosalhuila.lista.Adaptador Adaptador;
    private ListView listaItems;

    private Handler handler_listaCategoria = new Handler();
    private Thread thread_listaCategoria;
    private WS_ListaCategoria webResponseListaCategoria;

    private ProgressDialog circuloProgreso;

    private String nombre_categoria;
    private String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        this.nombre_categoria = intent.getStringExtra("nombre_categoria");
        this.categoria = intent.getStringExtra("categoria");

        this.setTitle(nombre_categoria);

        listaItems = (ListView)findViewById(R.id.listItems);

        //Se genera el llamado al web service que enviara los marcadores presentes en la base de datos.
        circuloProgreso = ProgressDialog.show(this, "", "Espere por favor ...", true);

        thread_listaCategoria = new Thread() {
            public void run() {
                Looper.prepare();
                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                webResponseListaCategoria = new WS_ListaCategoria();
                webResponseListaCategoria.startWebAccess("usuario", id_dispositivo, categoria);
                handler_listaCategoria.post(listaCategoria);
            }
        };

        thread_listaCategoria.start();
        ////////////////////////////////////////////////////////////////////////////////////////////////

    }

    //Se establece las acciones a tomar en el instante que llegue la respuesta del web service.
    final Runnable listaCategoria = new Runnable() {
        public void run() {

            Items = new ArrayList<TitularItems>();

            for(int i=0; i<webResponseListaCategoria.getSitioTuristico().size(); i++){
                Items.add(new TitularItems(webResponseListaCategoria.getSitioTuristico().get(i), webResponseListaCategoria.getNombreSitioTuristico().get(i), "Pendiente", webResponseListaCategoria.getImagenSitioTuristico().get(i)));
            }

            Adaptador = new Adaptador(Lugares.this, Items);


            listaItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Intent i = new Intent(Lugares.this, Informacion.class);
                    i.putExtra("sitio_turistico", Items.get(position).getSitio());
                    i.putExtra("nombre_sitio_turistico", Items.get(position).getTitle());
                    startActivity(i);
                }
            });

            listaItems.setAdapter(Adaptador);

            circuloProgreso.dismiss();
        }
    };

}
