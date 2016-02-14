package com.proyecto.huila.todosalhuila.webservice;

/**
 * Created by emmanuel on 9/12/15.
 */

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WS_ValidarConexion {

    private int TimeOut=3000;
    private final String NAMESPACE = "urn:app";
    private String URL;
    private final String SOAP_ACTION = "urn:app/validarConexion";
    private final String METHOD_NAME = "validarConexion";
    private String webResponse = "";
    public String startWebAccess() {

        Datos url = new Datos();
        URL = url.getSERVICE();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(URL, TimeOut);
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();
            webResponse = response.toString();
            Log.v("mensaje", webResponse);

        } catch (Exception exception) {
            webResponse = "false";
        }
        return webResponse;
    }
}