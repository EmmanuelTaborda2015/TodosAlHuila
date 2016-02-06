package com.proyecto.huila.todosalhuila.lista;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.lista.Adaptador;
import com.proyecto.huila.todosalhuila.lista.TitularItems;

import java.util.ArrayList;

public class Lugares extends AppCompatActivity {

    private ArrayList<TitularItems> Items;
    private com.proyecto.huila.todosalhuila.lista.Adaptador Adaptador;
    private ListView listaItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle("Atractivos Turísticos");

        listaItems = (ListView)findViewById(R.id.listItems);
        loadItems();
    }

    private void loadItems(){

        Items = new ArrayList<TitularItems>();
        Items.add(new TitularItems("Museo Prehistórico", "Museo", this.getResources().getIdentifier("museoprehistorico", "drawable", this.getPackageName())));
        Items.add(new TitularItems("Museo de Arte Contemporneo Del Huila", "Museo", this.getResources().getIdentifier("museoartehuila", "drawable", this.getPackageName())));

        Adaptador = new Adaptador(this, Items);
        listaItems.setAdapter(Adaptador);
    }

}
