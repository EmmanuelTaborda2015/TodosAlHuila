package com.proyecto.huila.todosalhuila;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.proyecto.huila.todosalhuila.menu.Inicio;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button boton = (Button) findViewById(R.id.botonBienvenida);
        boton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Login.this, Inicio.class );
                startActivity(i);
            }
        });
    }
}
