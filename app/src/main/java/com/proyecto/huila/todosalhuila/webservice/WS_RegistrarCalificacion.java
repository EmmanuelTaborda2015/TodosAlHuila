package com.proyecto.huila.todosalhuila.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WS_RegistrarCalificacion extends AsyncTask<String, Void, String> {

    String url = "http://52.36.201.248/ws_todosalhuila/turista/aplicativo/setcalificacion/";

    public AsyncResponse delegate = null;

    public WS_RegistrarCalificacion(AsyncResponse asyncResponse) {
        delegate = (AsyncResponse) asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    protected String doInBackground(String[] params) {
        ArrayList<NameValuePair> nameValuePairs = new
                ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("user", params[0]));
        nameValuePairs.add(new BasicNameValuePair("dispositivo", params[1]));
        nameValuePairs.add(new BasicNameValuePair("mipyme", params[2]));
        nameValuePairs.add(new BasicNameValuePair("calificacion", params[3]));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new
                    HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String result = convertStreamToString(is);
            Log.v("resultado", result);

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        return "true";
    }
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return sb.toString();
    }


    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}

