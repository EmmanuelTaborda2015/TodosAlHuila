package com.proyecto.huila.todosalhuila.lista;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;

public class Adaptador extends BaseAdapter {
    private ArrayList<TitularItems> listData;
    private LayoutInflater layoutInflater;

    public Adaptador(Context context, ArrayList<TitularItems> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.image_list_item, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.reporterNameView = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgItem);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TitularItems newsItem = listData.get(position);
        holder.headlineView.setText(newsItem.getTitle());
        holder.reporterNameView.setText(newsItem.getDescription());

        if (holder.imageView != null) {
            LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                @Override
                public void processFinish(final Bitmap[] output) {
                    holder.imageView.setImageBitmap(output[0]);
                }
            });

            String[] myTaskParams ={newsItem.getImg()};
            asyncTask.execute(myTaskParams);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;
        TextView reporterNameView;
        ImageView imageView;
    }
}

/*
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {

    private Activity activity; //Activity desde el cual se hace referencia al llenado de la lista

    private ArrayList<TitularItems> arrayItems; // Lista de items

    // Constructor con parámetros que recibe la Acvity y los datos de los items.

    public Adaptador(Activity activity, ArrayList<TitularItems> listaItems){

        super();

        this.activity = activity;

        this.arrayItems = new ArrayList<TitularItems>(listaItems);

    }

    // Retorna el número de items de la lista

    @Override

    public int getCount() {

        return arrayItems.size();

    }

    // Retorna el objeto TitularItems de la lista

    @Override

    public Object getItem(int position) {

        return arrayItems.get(position);

    }

    // Retorna la posición del item en la lista

    @Override

    public long getItemId(int position) {

        return position;

    }

   */
/*

   Clase estática que contiene los elementos de la lista

     *//*


    public static class Fila

    {

        TextView txtTitle;

        TextView txtDescription;

        ImageView img;

    }

    // Método que retorna la vista formateada

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        Fila view = new Fila();

        LayoutInflater inflator = activity.getLayoutInflater();

        TitularItems itm = arrayItems.get(position);

       */
/*

       Condicional para recrear la vista y no distorcionar el número de elementos

         *//*


        if(convertView==null)

        {

            convertView = inflator.inflate(R.layout.image_list_item, parent, false);

            view.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

            view.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);


            LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                @Override
                public void processFinish(final Bitmap[] output) {
                    
                }
            });

            String[] myTaskParams ={};
            asyncTask.execute(myTaskParams);
            
            view.img = (ImageView)convertView.findViewById(R.id.imgItem);

            convertView.setTag(view);

        }

        else

        {

            view = (Fila)convertView.getTag();

        }

        // Se asigna el dato proveniente del objeto TitularItems

        view.txtTitle.setText(itm.getTitle());

        view.txtDescription.setText(itm.getDescription());

        view.img.setImageResource(itm.getImg());

        // Retornamos la vista

        return convertView;

    }

}*/
