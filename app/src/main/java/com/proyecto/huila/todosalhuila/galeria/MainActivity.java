package com.proyecto.huila.todosalhuila.galeria;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.proyecto.huila.indicador.ImageIndicatorViewUrl;
import com.proyecto.huila.indicador.LoadImageFromURL;
import com.proyecto.huila.todosalhuila.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

            private ImageIndicatorViewUrl imageIndicatorView;

            @Override
            public void processFinish(Bitmap[] output) {
                this.imageIndicatorView = (ImageIndicatorViewUrl) findViewById(R.id.indicate_view);
                final Bitmap[] resArray = output;
                this.imageIndicatorView.setupLayoutByDrawable(resArray);
                this.imageIndicatorView.show();
            }
        });

        String[] myTaskParams = { "http://192.168.0.6/imagen/chicala1.JPEG", "http://192.168.0.6/imagen/chicala2.JPEG", "http://192.168.0.6/imagen/chicala3.JPEG", "http://192.168.0.6/imagen/chicala4.JPEG"};
        asyncTask.execute(myTaskParams);

    }
}