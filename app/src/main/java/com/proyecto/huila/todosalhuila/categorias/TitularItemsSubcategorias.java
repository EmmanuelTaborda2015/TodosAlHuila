package com.proyecto.huila.todosalhuila.categorias;

public class TitularItemsSubcategorias {

    String sitio; // Título del item

    String title; // Título del item

    String description; // Descripción del item

    String img; // Imagen del ítem

    String ciudad; // Ciudad del ítem

    int item; // Ciudad del ítem

    // Constructor por defecto de la clase

    public TitularItemsSubcategorias(){}

    // Constructor con parámetros para inicializar el item

    public TitularItemsSubcategorias(String _sitio, String _title, String _description, String _img, String _ciudad, int _item){

        this.sitio = _sitio;

        this.title = _title;

        this.description = _description;

        this.img = _img;

        this.ciudad = _ciudad;

        this.item = _item;

    }

    // Aqui inicia el GET y el SET para cada propiedad de la clase

    public String getSitio() { return sitio; }

    public void setSitioString (String sitio) { this.sitio = sitio; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getImg() { return img; }

    public void setImg(String img) { this.img = img; }

    public String getCiudad() { return ciudad; }

    public void setCiudad(String img) { this.ciudad = img; }

    public int getItem() { return item; }

    public void setItem(int item) { this.item = item; }

}