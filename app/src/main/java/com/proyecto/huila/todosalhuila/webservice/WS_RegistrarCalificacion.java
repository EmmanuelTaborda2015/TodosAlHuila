package com.proyecto.huila.todosalhuila.webservice;

/**
 * Created by emmanuel on 9/12/15.
 */

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_RegistrarCalificacion {

    private final String NAMESPACE = "urn:app";
    private String URL;
    private final String SOAP_ACTION = "urn:app/registrarCalificacion";
    private final String METHOD_NAME = "registrarCalificacion";

    public String getWebResponse() {
        return webResponse;
    }

    private String webResponse = "";

    public void startWebAccess(String usuario, String dispositivo, String sitio_turistico, String calificacion) {

        Datos url = new Datos();
        URL = url.getSERVICE();

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("usuario", usuario);
        request.addProperty("dispositivo", dispositivo);
        request.addProperty("sitioTuristico", sitio_turistico);
        request.addProperty("calificacion", calificacion);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();
            webResponse = response.toString();
        } catch (Exception exception) {
            webResponse = "false";
        }
    }
}