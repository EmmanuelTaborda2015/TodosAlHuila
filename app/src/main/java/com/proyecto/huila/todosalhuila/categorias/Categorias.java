package com.proyecto.huila.todosalhuila.categorias;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.proyecto.huila.todosalhuila.R;

public class Categorias extends Activity {
	final Context context = this;
    private static final String[][] data = {
            {"Avistamiento de aves","Avistamiento de ballenas","Senderos","Flora y fauna","servicios ambientales"},
            {"Ciclomontañismo","Ciclismo","Cicloturismo","Downhill","Parapente","Espeleologia","Rapel","Torrentismo","Escalada","Senderismo - Trekking","Caminata","Carrera","Pesca deportiva","Cabalgata","Natacion","Lancha","Moto de agua","Kayak","Canotaje - Rafting","Naútica","Carro"},{"Haciendas","Agroecoturismo","Vivero agrofotrestal","Agroindustria de Chocolate"},{"Arquitectura y monumentos","Parques y plazas","Iglesias"},{"Cafés","Restaurantes","Comida internacional","Comida típica","Comida rápida","Banquetes","Panadería, bizcochería y/o repostería","Heladería"},{"Fiestas y celebraciones","Religioso","Artesanias","Museos","Exposiciones itinerantes","Teatro","Danza","Música","Arqueologia","Bibliotecas","Gestor cultural","Fundacion","Educación turismo"},{"Clínicas de cirugía estética","Termalismo","Talasoterapia","Spas/Centros de bienestar","Salones de belleza","Productos de linea corporal","Prestador de servicios de salud"},{"Seminarios","Mesas de trabajo","Diplomados","Foros","Simposios","Congresos","Encuentros","Ferias de negocio","Exposiciones","Actividades de Networking","Ruedas de negocio","Recintos feriales","Asesorias y consultorias","Eventos sociales","Centros de negocios"},{"Desierto"},{"Piscinas","Ríos","Balnearios","Playas de arena oscura","Playas de arena blanca","Acuario natural"},{"Bares","Discotecas","Karaokes","Cervecerías"},{"Comercio productos","Centros comerciales","Sitios de souvenirs/recuerdos","Hipermercados","Supermercados","Miscelania","Internet y papeleria","Mercados campesinos","Plazas de mercado","Floristería","Boutiques","Zapaterías","Desarrollo de software","Diseño web","Tecnología","Publicidad","Alquiler de equipos/sonido videobeam","Operador turístico","Guía Turístico","Agencias de viajes","administrador de empresas turistica y hotelera, asesorías"},{"Hoteles","Hostales","Posadas","Servicio de alojamiento familiar","Fincas"},{"Salas de cine","Teatros","Parques recreativos","Parque de atracciones","Parques temáticos","Parques temáticos","Escenarios deportivos","Parador Turistico","Estadero"},{"Parqueaderos","Empresas transportadoras"},{"Acevedo","Aipe","Algeciras","Altamira","Baraya","Campoalegre","Colombia","Elias","El Agrado","Garzón","Gigante","Guadalupe","Hobo","Íquira","Isnos","La Argentina","La Plata","Nátaga","Neiva","Oporapa","Paicol","Palermo","Palestina","Pital","Pitalito","Rivera","Salado Blanco","Santa María","San Agustín","Suaza","Tarqui","Tello","Teruel","Tesalia","Timaná","Villavieja","Yaguará "}};
    private ExpandableListView expandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_expandablelist);
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView1);
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
    }
}
