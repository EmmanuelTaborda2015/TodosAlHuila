package com.proyecto.huila.todosalhuila.categorias;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;

import java.util.ArrayList;

public class AdaptadorSubcategorias extends BaseAdapter {
    private ArrayList<TitularItemsSubcategorias> listData;
    private LayoutInflater layoutInflater;

    public AdaptadorSubcategorias(Context context, ArrayList<TitularItemsSubcategorias> listData) {
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

        TitularItemsSubcategorias newsItem = listData.get(position);
        holder.headlineView.setText(newsItem.getTitle());
        holder.reporterNameView.setText(newsItem.getDescription());

        if (holder.imageView != null) {
            final View finalConvertView = convertView;
            LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                @Override
                public void processFinish(final Bitmap[] output) {
                    if(output[0]==null){
                        Bitmap icon = BitmapFactory.decodeResource(finalConvertView.getResources(), R.drawable.imagen_no_disponible);
                        holder.imageView.setImageBitmap(icon);
                    }else{
                        holder.imageView.setImageBitmap(output[0]);
                    }
                }
            });

            String[] myTaskParams ={"http://"+newsItem.getImg()};
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
