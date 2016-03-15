package com.proyecto.huila.todosalhuila.categorias;
 
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;

import com.proyecto.huila.todosalhuila.R;

public class ExpandableListAdapterCategorias extends BaseExpandableListAdapter implements ExpandableListAdapter  {
	public Context context;
    private LayoutInflater vi;
    private String[][] data;
    int _objInt;

    private static final int GROUP_ITEM_RESOURCE = R.layout.categorias_group_item;
    private static final int CHILD_ITEM_RESOURCE = R.layout.categorias_child_item;

    public ExpandableListAdapterCategorias(Context context, Activity activity, String[][] data) {
        this.data = data;
        this.context = context;
        vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        _objInt = data.length; 
    }

    public String getChild(int groupPosition, int childPosition) {
        return data[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return data[groupPosition].length;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        String child = getChild(groupPosition, childPosition);
        int id_res = 0;

        if (child != null) {
            v = vi.inflate(CHILD_ITEM_RESOURCE, null);
            ViewHolder holder = new ViewHolder(v);
            holder.text.setText(Html.fromHtml(child));
        }
        return v;
    }

    public String getGroup(int groupPosition) {
        return "group-" + groupPosition;
    }

    public int getGroupCount() {
        return data.length;
    }


    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        String group = null;
        int id_res = 0;
        long group_id = getGroupId(groupPosition);

        if(group_id == 0){
        	group = "Ecoturismo";
        	id_res = R.drawable.categoria9;
        }
        else if(group_id == 1){
        	group = "Turismo de aventura";
        	id_res = R.drawable.categoria10;
        }
        else if(group_id == 2){
        	group = "Turismo rural";
        	id_res = R.drawable.categoria11;
        }
        else if(group_id == 3){
            group = "Atractivos turísticos";
            id_res = R.drawable.categoria12;
        }
        else if(group_id == 4){
            group = " Gastronomia";
            id_res = R.drawable.categoria13;
        }
        else if(group_id == 5){
            group = "Cultura";
            id_res = R.drawable.categoria14;
        }
        else if(group_id == 6){
            group = "Salud y bienestar";
            id_res = R.drawable.categoria15;
        }
        else if(group_id == 7){
            group = "Turismo corporativo";
            id_res = R.drawable.categoria8;
        }
        else if(group_id == 8){
            group = "Sol y playa";
            id_res = R.drawable.categoria7;
        }
        else if(group_id == 9){
            group = "Astronomía";
            id_res = R.drawable.categoria6;
        }
        else if(group_id == 10){
            group = "Bares y Pubs";
            id_res = R.drawable.categoria5;
        }
        else if(group_id == 11){
            group = "Comercio";
            id_res = R.drawable.categoria4;
        }
        else if(group_id == 12){
            group = "Hospedaje";
            id_res = R.drawable.categoria3;
        }
        else if(group_id == 13){
            group = "Recreación";
            id_res = R.drawable.categoria3;
        }
        else if(group_id == 14){
            group = "Transporte";
            id_res = R.drawable.categoria2;
        }
        else if(group_id == 15){
            group = "Municipios";
            id_res = R.drawable.categoria1;
        }
        
        if (group != null) {
            v = vi.inflate(GROUP_ITEM_RESOURCE, null);
            ViewHolder holder = new ViewHolder(v);

            holder.text.setText(Html.fromHtml(group));
            holder.imageview.setImageResource(id_res);

            if (isExpanded) {
                holder.imageviewIndicator.setImageResource(R.drawable.des);
                v.setBackgroundColor(v.getResources().getColor(R.color.azul3));
            } else {
                holder.imageviewIndicator.setImageResource(R.drawable.con);
            }
        }
        return v;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }


}