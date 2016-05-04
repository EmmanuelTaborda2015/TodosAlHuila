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

    public String getHostMiPyme() {
        return HostMiPyme;
    }

    private String HOST = "http://52.20.189.85/";

    private String SERVICE = HOST + "ws_todosalhuila/turista/servicio/servicio.php";

        private String HostMiPyme = "http://52.39.71.226:8080/api/empresa/getInformacion/";

}

