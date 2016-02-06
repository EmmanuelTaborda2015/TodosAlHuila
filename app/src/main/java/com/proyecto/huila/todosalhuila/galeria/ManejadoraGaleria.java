package com.proyecto.huila.todosalhuila.galeria;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManejadoraGaleria extends FragmentPagerAdapter {

    List<Fragment> fragmentos;
    public ManejadoraGaleria(FragmentManager fm) {
        super(fm);
        fragmentos = new ArrayList<Fragment>();
    }

    public void agregarFragmentos(Fragment xfragmento){
        fragmentos.add(xfragmento);
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentos.get(position);
    }

    @Override
    public int getCount() {
        return fragmentos.size();
    }
}
