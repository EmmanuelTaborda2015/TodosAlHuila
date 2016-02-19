package com.proyecto.huila.todosalhuila.lista;

public class TitularItems {

    String title; // Título del item

    String description; // Descripción del item

    String img; // Imagen del ítem

    // Constructor por defecto de la clase

    public TitularItems(){}

    // Constructor con parámetros para inicializar el item

    public TitularItems(String _title, String _description, String _img){

        this.title = _title;

        this.description = _description;

        this.img = _img;

    }

    // Aqui inicia el GET y el SET para cada propiedad de la clase

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getImg() { return img; }

    public void setImg(String img) { this.img = img; }

}