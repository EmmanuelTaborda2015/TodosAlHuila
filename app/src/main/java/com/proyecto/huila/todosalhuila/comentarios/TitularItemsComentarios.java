package com.proyecto.huila.todosalhuila.comentarios;

public class TitularItemsComentarios {

    String title; // Título del item

    String description; // Descripción del item

    String img; // Imagen del ítem

    String fecha; // Fecha del registro del comentario

    // Constructor por defecto de la clase

    public TitularItemsComentarios(){}

    // Constructor con parámetros para inicializar el item

    public TitularItemsComentarios(String _title, String _description, String _fecha, String _img){

        this.title = _title;

        this.description = _description;

        this.fecha = _fecha;

        this.img = _img;
    }

    // Aqui inicia el GET y el SET para cada propiedad de la clase
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getImg() { return img; }

    public void setImg(String img) { this.img = img; }

}