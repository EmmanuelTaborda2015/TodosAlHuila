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
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.Login;
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
            {"Avistamiento de aves", "Avistamiento de ballenas", "Senderos", "Flora y fauna", "servicios ambientales"},
            {"Ciclomontañismo", "Ciclismo", "Cicloturismo", "Downhill", "Parapente", "Espeleologia", "Rapel", "Torrentismo", "Escalada", "Senderismo - Trekking", "Caminata", "Carrera", "Pesca deportiva", "Cabalgata", "Natacion", "Lancha", "Moto de agua", "Kayak", "Canotaje - Rafting", "Naútica", "Carro"},
            {"Haciendas", "Agroecoturismo", "Vivero agrofotrestal", "Agroindustria de Chocolate"},
            {"Arquitectura y monumentos", "Parques y plazas", "Iglesias"},
            {"Cafés", "Restaurantes", "Comida internacional", "Comida típica", "Comida rápida", "Banquetes", "Panadería, bizcochería y/o repostería", "Heladería"},
            {"Fiestas y celebraciones", "Religioso", "Artesanias", "Museos", "Exposiciones itinerantes", "Teatro", "Danza", "Música", "Arqueologia", "Bibliotecas", "Gestor cultural", "Fundacion", "Educación turismo"},
            {"Clínicas de cirugía estética", "Termalismo", "Talasoterapia", "Spas/Centros de bienestar", "Salones de belleza", "Productos de linea corporal", "Prestador de servicios de seg7"},
            {"Seminarios", "Mesas de trabajo", "Diplomados", "Foros", "Simposios", "Congresos", "Encuentros", "Ferias de negocio", "Exposiciones", "Actividades de Networking", "Ruedas de negocio", "Recintos feriales", "Asesorias y consultorias", "Eventos sociales", "Centros de negocios"},
            {"Desierto"},
            {"Piscinas", "Ríos", "Balnearios", "Playas de arena oscura", "Playas de arena blanca", "Acuario natural"},
            {"Bares", "Discotecas", "Karaokes", "Cervecerías"},
            {"Comercio productos", "Centros comerciales", "Sitios de souvenirs/recuerdos", "Hipermercados", "Supermercados", "Miscelania", "Internet y papeleria", "Mercados campesinos", "Plazas de mercado", "Floristería", "Boutiques", "Zapaterías", "Desarrollo de software", "Diseño web", "Tecnología", "Publicidad", "Alquiler de equipos/sonido videobeam", "Operador turístico", "Guía Turístico", "Agencias de viajes", "administrador de empresas turistica y hotelera, asesorías"},
            {"Hoteles", "Hostales", "Posadas", "Servicio de alojamiento familiar", "Fincas"},
            {"Salas de cine", "Teatros", "Parques recreativos", "Parque de atracciones", "Parques temáticos", "Parques temáticos", "Escenarios deportivos", "Parador Turistico", "Estadero"},
            {"Parqueaderos", "Empresas transportadoras"},
            {"Acevedo", "Aipe", "Algeciras", "Altamira", "Baraya", "Campoalegre", "Colombia", "Elias", "El Agrado", "Garzón", "Gigante", "Guadalupe", "Hobo", "Íquira", "Isnos", "La Argentina", "La Plata", "Nátaga", "Neiva", "Oporapa", "Paicol", "Palermo", "Palestina", "Pital", "Pitalito", "Rivera", "Salado Blanco", "Santa María", "San Agustín", "Suaza", "Tarqui", "Tello", "Teruel", "Tesalia", "Timaná", "Villavieja", "Yaguará "}};

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
