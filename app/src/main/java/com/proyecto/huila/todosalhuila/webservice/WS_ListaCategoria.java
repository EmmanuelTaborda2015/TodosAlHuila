package com.proyecto.huila.todosalhuila.webservice;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_ListaCategoria {

    private final String NAMESPACE = "urn:app";

    private String URL;

    private final String SOAP_ACTION = "urn:app/listaCategoria";
    private final String METHOD_NAME = "listaCategoria";

    private List<String> sitio_turistico = new ArrayList<String>();
    private List<String> nombre_sitio_turistico = new ArrayList<String>();
    private List<String> imagen = new ArrayList<String>();


    public List<String> getSitioTuristico() {
        return sitio_turistico;
    }

    public List<String> getNombreSitioTuristico() {
        return nombre_sitio_turistico;
    }

    public List<String> getImagenSitioTuristico() {
        return imagen;
    }


    public void startWebAccess(String usuario, String dispositivo, String categoria) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);
        request.addProperty("categoria", categoria);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);

        try {

            httpTransport.call(SOAP_ACTION, envelope);
            SoapObject obj1 = (SoapObject) envelope.bodyIn;

            Vector<?> responseVector = (Vector<?>) obj1.getProperty(0);

            for (int i = 0; i < responseVector.size(); i++) {
                SoapObject obj2 = (SoapObject) responseVector.get(i);
                SoapObject obj3;
                try {
                    obj3 = (SoapObject) obj2.getProperty(1);
                    sitio_turistico.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    sitio_turistico.add("");
                }

                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    nombre_sitio_turistico.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    nombre_sitio_turistico.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    imagen.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    imagen.add("");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}