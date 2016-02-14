package com.proyecto.huila.todosalhuila.webservice;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_SitioTuristico {

    private final String NAMESPACE = "urn:app";

    private String URL;

    private final String SOAP_ACTION = "urn:app/informacionSitioTuristico";
    private final String METHOD_NAME = "informacionSitioTuristico";

    private List<String> Descripcion = new ArrayList<String>();
    private List<String> informacion_contacto = new ArrayList<String>();
    private List<String> imagenes = new ArrayList<String>();

    public List<String> getDescripcion() {
        return Descripcion;
    }

    public List<String> getInformacionContacto() {
        return informacion_contacto;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void startWebAccess(String usuario, String dispositivo, String sitio_turistico) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);
        request.addProperty("sitio_turistico", sitio_turistico);

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
                    Descripcion.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    Descripcion.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(3);
                    informacion_contacto.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    informacion_contacto.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    imagenes.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    imagenes.add("");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}