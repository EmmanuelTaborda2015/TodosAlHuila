package com.proyecto.huila.indicador;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap[]> {

    public AsyncResponse delegate = null;

    public LoadImageFromURL(AsyncResponse asyncResponse) {
        delegate = (AsyncResponse) asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(Bitmap[] output);
    }


    @Override
    protected Bitmap[] doInBackground(String... params) {
        // TODO Auto-generated method stub
        Bitmap[] bitmaps = new Bitmap[params.length];
        for (int i=0; i<params.length; i++) {
            try {
                URL url = new URL(params[i]);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                bitmaps[i]=bitMap;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

    @Override
    protected void onPostExecute(Bitmap[] result) {
        delegate.processFinish(result);
    }

}