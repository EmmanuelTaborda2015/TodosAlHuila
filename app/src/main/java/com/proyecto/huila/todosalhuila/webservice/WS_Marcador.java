package com.proyecto.huila.todosalhuila.webservice;

import android.content.Context;
import android.util.Log;

import com.proyecto.huila.todosalhuila.herramientas.ServicioWeb;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WS_Marcador extends ServicioWeb {

    private final String NAMESPACE = "urn:app";

    private String URL;

    private String usuario;
    private String dispositivo;
    private String marcador;

    private final String SOAP_ACTION = "urn:app/marcador";
    private final String METHOD_NAME = "marcador";

    private List<String> sitio_turistico = new ArrayList<String>();
    private List<String> nombre_sitio_turistico = new ArrayList<String>();
    private List<String> tipo_sitio_turistico = new ArrayList<String>();
    private List<String> coord_x = new ArrayList<String>();
    private List<String> coord_y = new ArrayList<String>();

    public WS_Marcador(Context context, String usuario, String dispositivo, String marcador) {
        super(context);

        this.usuario=usuario;
        this.dispositivo=dispositivo;
        this.marcador=marcador;
    }

    public List<String> getSitioTuristico() {
        return sitio_turistico;
    }

    public List<String> getNombreSitioTuristico() {
        return nombre_sitio_turistico;
    }

    public List<String> getTipoSitioTuristico() {
        return tipo_sitio_turistico;
    }

    public List<String> getCoordX() {
        return coord_x;
    }

    public List<String> getCoordY() {
        return coord_y;
    }

    public void startWebAccess() {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);
        request.addProperty("marcador", marcador);

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
                    tipo_sitio_turistico.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    tipo_sitio_turistico.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(5);
                    nombre_sitio_turistico.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    nombre_sitio_turistico.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(7);
                    coord_x.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    coord_x.add("");
                }
                try {
                    obj3 = (SoapObject) obj2.getProperty(9);
                    coord_y.add(obj3.getProperty("value").toString());
                } catch (NullPointerException ex) {
                    coord_y.add("");
                }
            }
        } catch (Exception exception) {
            Log.v("mensaje", exception.toString());
        }
    }
}