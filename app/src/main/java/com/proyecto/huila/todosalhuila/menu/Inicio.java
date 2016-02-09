package com.proyecto.huila.todosalhuila.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.galeria.FragmentosImagenes;
import com.proyecto.huila.todosalhuila.galeria.ManejadoraGaleria;
import com.proyecto.huila.todosalhuila.geolocalizacion.Geocalizacion;

public class Inicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int[] imagenes = {
            R.drawable.image2,
            R.drawable.image1,
            R.drawable.image3,
            R.drawable.image4
    };

    ManejadoraGaleria manejadorGaleria;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Desde este punto se agrega el código de la página como tal

        //galeria de imagenes

        manejadorGaleria = new ManejadoraGaleria(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        manejadorGaleria.agregarFragmentos(FragmentosImagenes.newInstance(imagenes[0]));
        manejadorGaleria.agregarFragmentos(FragmentosImagenes.newInstance(imagenes[1]));
        manejadorGaleria.agregarFragmentos(FragmentosImagenes.newInstance(imagenes[2]));
        manejadorGaleria.agregarFragmentos(FragmentosImagenes.newInstance(imagenes[3]));

        mViewPager.setAdapter(manejadorGaleria);
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
                    .setPositiveButton(R.string.opcionSi, new DialogInterface.OnClickListener()
                    {
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu1) {
            Intent i = new Intent(Inicio.this, Inicio.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu2) {
            Intent i = new Intent(Inicio.this, Categorias.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu3) {
            Intent i = new Intent(Inicio.this, Geocalizacion.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu4) {
            Intent i = new Intent(Inicio.this, Agenda.class );
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu5) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
