package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.proyecto.huila.todosalhuila.R;

public class Calificar extends AppCompatActivity {

    private String sitio_turistico;
    private String nombre_sitio_turistico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        final Intent intent = getIntent();
        this.nombre_sitio_turistico = intent.getStringExtra("nombre_sitio_turistico");
        this.sitio_turistico = intent.getStringExtra("sitio_turistico");
        //Bitmap bmp = intent.getParcelableExtra("imagen");

        TextView sitio_turistico = (TextView) findViewById(R.id.label_lugar_calificar);
        sitio_turistico.setText(nombre_sitio_turistico);

        if(getIntent().hasExtra("byteArray")) {
            ImageView imagen = (ImageView) findViewById(R.id.calificar_imagen1);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            imagen.setImageBitmap(b);
        }

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);

        LayerDrawable stars2 = (LayerDrawable) ratingBar.getProgressDrawable();
        stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars2.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        LayerDrawable stars3 = (LayerDrawable) ratingBar2.getProgressDrawable();
        stars3.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars3.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        stars3.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    int stars = (int) starsf + 1;
                    ratingBar.setRating(stars);

                    RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                    LayerDrawable stars2 = (LayerDrawable) ratingBar.getProgressDrawable();
                    stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                    stars2.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }

                return true;
            }
        });

        
    }

}
