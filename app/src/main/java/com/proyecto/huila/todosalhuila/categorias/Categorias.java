package com.proyecto.huila.todosalhuila.categorias;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

public class Categorias extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    private RelativeLayout connetion;

    int seleccion = 0;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
    }

    final Context context = this;
    private static final String[][] data = {

            ////Naturaleza
            //Ecoturismo
            {"Avistamiento de aves", "Flora y fauna", "Parques Naturales", "Senderos", "Servicios Ambientales", "Otra Ecoturismo"},
            //Turismo de aventura
            {"Cabalgata", "Caminata", "Canotaje - Rafting", "Carrera", "Ciclismo", "Ciclomontañismo", "Cicloturismo", "Downhill", "Escalada", "Espeleologia", "Kayak", "Lancha", "Moto de Agua", "Natación", "Parapente", "Pesca deportiva", "Rapel", "Senderismo-Trekking", "Torrentismo", "Otra Aventura"},
            //Turismo rural
            {"Agroecoturismo", "Agroindustria del café", "Agroindustria del chocolate", "Agroindustria de la panela", "Haciendas", "Viveros", "Otra Rural"},

            ////Cultural
            //Atractivos turísticos
            {"Arquitectura y monumentos", "Iglesias", "Parques y plazas", "Otra Turística"},
            //Gastronomía
            {"Cafés", "Comida internacional", "Comida rápida", "Comida típica", "Heladería", "Panadería, bizcochería, repostería", "Restaurantes", "Otra Gastronomía"},
            //Cultura
            {"Arqueología", "Artesanías", "Blibliotecas", "Danza", "Educación Turismo", "Exposiciones itinerantes", "Fiestas y celebraciones", "Fundación", "Gestor cultural", "Museos", "Música", "Religioso", "Teatro", "Otra Cultura"},

            ////Salud y Bienestar
            //Salud y bienestar
            {"Clínicas de cirugía estética", "Gimnasio", "Prestador de Servicios de Salud", "Salones de belleza", "Spas/Centros de bienestar", "Termalismo/Talasoterapia", "Otra Salud"},

            ////Negocios
            //Turismo corporativo
            {"Actividades de Networking", "Asesorias y consultorías", "Centros de negocios", "Congresos", "Diplomados", "Encuentros", "Eventos sociales", "Exposiciones", "Ferias de negocio", "Foros", "Mesas de trabajo", "Recintos feriales", "Ruedas de negocio", "Seminarios", "Simposios", "Otra Negocios"},

            ////Sol y Veraneo
            //Sol y Veraneo
            {"Balnearios", "Piscinas", "Ríos", "Otra Sol"},

            ////Nocturno
            //Astronomía
            {"Desierto", "Otra Astronomía"},
            //Bares y Pubs
            {"Bares", "Cervecerías", "Discotecas", "Karaokes", "Otra Nocturno"},

            ////Entretenimiento
            //Comercio
            {"Agencias de viajes", "Boutiques", "Centros comerciales", "Empresas de Publicidad", "Empresas de Tecnología", "Floristerías", "Hipermercados", "Internet y papelerías", "Mercados campesinos", "Misceláneas", "Operador turístico", "Plazas de mercado", "Sitios de souvenirs/recuerdos", "Supermercados", "Venta de artesanías", "Zapaterías", "Otra Comercio"},
            //Hospedaje
            {"Fincas", "Hostales", "Hoteles", "Posadas", "Servicio de alojamiento familiar", "Otra Hospedaje"},
            //Recreación
            {"Casinos", "Escenarios deportivos", "Parque de atracciones", "Parques recreativos", "Parques temáticos", "Salas de cine", "Teatros", "Otra Recreación"},
            //Transporte
            {"Empresas Transportadoras", "Parqueaderos", "Otra Transporte"}}; //,

            //{"Acevedo", "Aipe", "Algeciras", "Altamira", "Baraya", "Campoalegre", "Colombia", "Elias", "El Agrado", "Garzón", "Gigante", "Guadalupe", "Hobo", "Íquira", "Isnos", "La Argentina", "La Plata", "Nátaga", "Neiva", "Oporapa", "Paicol", "Palermo", "Palestina", "Pital", "Pitalito", "Rivera", "Salado Blanco", "Santa María", "San Agustín", "Suaza", "Tarqui", "Tello", "Teruel", "Tesalia", "Timaná", "Villavieja", "Yaguará "}};

    private ExpandableListView expandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_expandablelist);

        this.setTitle("");

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

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        expandableListView.setAdapter(new ExpandableListAdapterCategorias(context, this, data));


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                v.setBackgroundColor(Categorias.this.getResources().getColor(R.color.black));
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(final int groupPosition) {
                if (groupPosition != previousItem) {
                    Log.v("posicion", groupPosition + "");
                    expandableListView.smoothScrollToPosition(groupPosition);
                    expandableListView.collapseGroup(previousItem);
                    previousItem = groupPosition;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            expandableListView.smoothScrollToPosition(groupPosition);
                        }
                    }, 1000);
                }

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {

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
                                seleccion = 0;
                                String itemclicked = data[groupPosition][childPosition];
                                Intent i = new Intent(Categorias.this, Subcategorias.class);
                                i.putExtra("subcategoria", itemclicked);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                    asyncTaskConection.execute();

                }
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
        int id = item.getItemId();

        if (id == R.id.action_salir) {
            salir();
        } else if (id == R.id.action_home) {
            Intent i = new Intent(Categorias.this, InicioLogin.class);
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
}
