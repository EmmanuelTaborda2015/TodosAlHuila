package com.proyecto.huila.todosalhuila;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.huila.todosalhuila.inicio.Inicio;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_Login;
import com.proyecto.huila.todosalhuila.webservice.WS_MiPyme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {

    public static boolean login = false;
    public static String usuario = "";
    public static String nombre = "";
    public static String usuarioD = "";
    public static String contrasenaD = "";

    TextView et_usuario;
    TextView et_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        et_usuario = (TextView) findViewById(R.id.lg_edittext_user);
        et_contrasena = (TextView) findViewById(R.id.lg_edittext_password);

        Button boton = (Button) findViewById(R.id.botonBienvenida);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("".equals(et_usuario.getText().toString()) && "".equals(et_contrasena.getText().toString())){
                    ingreseUsuarioContraseña();
                }else if("".equals(et_usuario.getText().toString())){
                    ingreseUsuario();
                }else if("".equals(et_contrasena.getText().toString())){
                    ingreseContraseña();
                }else {

                    final WS_Login asyncTask = new WS_Login(new WS_Login.AsyncResponse() {

                        @Override
                        public void processFinish(String output) {
                            try {
                                final JSONObject datos;
                                datos = new JSONObject(output);
                                final JSONObject respuesta;
                                respuesta = new JSONObject(datos.get("datos").toString());
                                if("correcto".equals(respuesta.get("estado").toString())){
                                    usuario = respuesta.get("usuario").toString();
                                    nombre = respuesta.get("nombre").toString();
                                    login = true;
                                    usuarioD = et_usuario.getText().toString();
                                    contrasenaD = et_contrasena.getText().toString();

                                    Intent i = new Intent(Login.this, InicioLogin.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    datosInvalidos();
                                }
                                Log.v("login", respuesta.get("estado").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String[] myTaskParams = {et_usuario.getText().toString(), et_contrasena.getText().toString()};
                    asyncTask.execute(myTaskParams);
                }
            }
        });

    }

    public void ingreseUsuarioContraseña() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("Ingrese el usuario y contraseña.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void ingreseUsuario() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("Ingrese el usuario.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void ingreseContraseña() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("Ingrese la contraseña.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void datosInvalidos() {
        new AlertDialog.Builder(this)
                .setTitle("Mensaje")
                .setMessage("No se ha podido iniciar sesión, verifique su usuario y/o contraseña.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
