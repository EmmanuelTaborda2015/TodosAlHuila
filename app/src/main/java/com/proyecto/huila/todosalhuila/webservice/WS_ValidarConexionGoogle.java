package com.proyecto.huila.todosalhuila.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WS_ValidarConexionGoogle extends AsyncTask<String, Void, String> {


    public AsyncResponse delegate = null;

    public WS_ValidarConexionGoogle(AsyncResponse asyncResponse) {
        delegate = (AsyncResponse) asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    protected String doInBackground(String[] params) {

        try {
            URL url = null;
            url = new URL("http://www.google.com/");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000); // mTimeout is in seconds
            urlc.connect();
            if (urlc.getResponseCode() == 200) {
                return "true";
            } else {
                return "false";
            }
        } catch (IOException e) {
            Log.i("warning", "Error checking internet connection", e);
            return "false";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
