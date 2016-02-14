package com.proyecto.huila.todosalhuila.webservice;

/**
 * Created by emmanuel on 5/11/15.
 */
public class Datos {
    public String getHOST() {
        return HOST;
    }

    public String getSERVICE() {
        return SERVICE;
    }

    private String HOST = "http://192.168.0.6/";
    private String SERVICE = HOST + "ws_todosalhuila/turista/servicio/servicio.php";

}

