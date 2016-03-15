package com.proyecto.huila.todosalhuila.categorias;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.huila.todosalhuila.R;

public class ViewHolder {

    public TextView text;
    public ImageView imageview;
    public ImageView imageviewIndicator;
    public ViewHolder(View v) {
        this.text = (TextView)v.findViewById(R.id.text1);
        this.imageview = (ImageView)v.findViewById(R.id.image1);
        this.imageviewIndicator = (ImageView)v.findViewById(R.id.image2);
    }
}
