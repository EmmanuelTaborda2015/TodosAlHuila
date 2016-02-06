package com.proyecto.huila.todosalhuila.galeria;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.proyecto.huila.todosalhuila.R;

public class FragmentosImagenes extends Fragment {

    private static final String ARG_IMAGE = "imagen";
    private int imagen;

    public static FragmentosImagenes newInstance(int imagen) {
        FragmentosImagenes fragment = new FragmentosImagenes();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE, imagen);
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            imagen = getArguments().getInt(ARG_IMAGE);
        }
    }

    public FragmentosImagenes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.visor, container, false);

        ImageView imagenView = (ImageView) rootView.findViewById(R.id.imageView1);
        imagenView.setImageResource(imagen);
        return rootView;
    }
}