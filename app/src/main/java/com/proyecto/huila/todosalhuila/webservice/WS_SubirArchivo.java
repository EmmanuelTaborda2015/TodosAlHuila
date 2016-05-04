package com.proyecto.huila.todosalhuila.webservice;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


import com.proyecto.huila.todosalhuila.activities.SubirMultimedia;

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

public class WS_SubirArchivo extends AsyncTask<String, Void, String> {

    String url = "http://52.20.189.85/joomlaH/app.php";

    public AsyncResponse delegate = null;

    public WS_SubirArchivo(AsyncResponse asyncResponse) {
        delegate = (AsyncResponse) asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    protected String doInBackground(String[] params) {
        ArrayList<NameValuePair> nameValuePairs = new
                ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", params[6]));
        nameValuePairs.add(new BasicNameValuePair("username", params[7]));
        nameValuePairs.add(new BasicNameValuePair("password", params[8]));
        nameValuePairs.add(new BasicNameValuePair("file", params[0]));
        nameValuePairs.add(new BasicNameValuePair("title", params[1]));
        nameValuePairs.add(new BasicNameValuePair("caption", params[2]));
        nameValuePairs.add(new BasicNameValuePair("address", params[3]));
        nameValuePairs.add(new BasicNameValuePair("latitude", params[4]));
        nameValuePairs.add(new BasicNameValuePair("longitude", params[5]));

            String result = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            result = convertStreamToString(is);

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        return result;
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

