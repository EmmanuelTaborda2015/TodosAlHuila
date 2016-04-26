package com.proyecto.huila.todosalhuila.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkUtil;
import com.proyecto.huila.todosalhuila.crypto.MCrypt;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_CerrarSesion;
import com.proyecto.huila.todosalhuila.webservice.WS_Login;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {

    public static boolean login = false;
    public static String usuario = "";
    public static String nombre = "";
    public static String usuarioD = "";
    public static String contrasenaD = "";
    public static String idusuario = "";

    private ProgressDialog circuloProgreso;


    TextView et_usuario;
    TextView et_contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        final WS_CerrarSesion asyncTaskCerrarSesion = new WS_CerrarSesion(new WS_CerrarSesion.AsyncResponse() {

            @Override
            public void processFinish(String output) {

            }
        });
        String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        String[] paramsCerrarSesion = {id_dispositivo};
        asyncTaskCerrarSesion.execute(paramsCerrarSesion);

        et_usuario = (TextView) findViewById(R.id.lg_edittext_user);
        et_contrasena = (TextView) findViewById(R.id.lg_edittext_password);

        Button boton = (Button) findViewById(R.id.botonBienvenida);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_usuario.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_contrasena.getWindowToken(), 0);

                final WS_ValidarConexionGoogle asyncTaskConection = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        if (output == "false") {
                            if (new NetworkUtil().getConnectivityStatus(getApplicationContext()) == 0) {
                                sinConexion();
                            } else {
                                conexionNoValida();
                            }
                        } else {
                            if ("".equals(et_usuario.getText().toString()) && "".equals(et_contrasena.getText().toString())) {
                                ingreseUsuarioContraseña();
                            } else if ("".equals(et_usuario.getText().toString())) {
                                ingreseUsuario();
                            } else if ("".equals(et_contrasena.getText().toString())) {
                                ingreseContraseña();
                            } else {

                        /*final WS_Login asyncTask = new WS_Login(new WS_Login.AsyncResponse() {

                            @Override
                            public void processFinish(String output) {

                            }
                        });
                        asyncTask.execute();*/
                                final WS_Login asyncTask = new WS_Login(new WS_Login.AsyncResponse() {

                                    @Override
                                    public void processFinish(String output) {
                                        try {
                                            final JSONObject datos;
                                            datos = new JSONObject(output);
                                            final JSONObject respuesta;
                                            respuesta = new JSONObject(datos.get("datos").toString());
                                            if ("correcto".equals(respuesta.get("estado").toString())) {
                                                usuario = respuesta.get("usuario").toString();
                                                nombre = respuesta.get("nombre").toString();
                                                login = true;
                                                idusuario = respuesta.get("id").toString();

                                                Intent i = new Intent(Login.this, InicioLogin.class);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                datosInvalidos();
                                            }

                                            circuloProgreso.dismiss();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                String id_dispositivo = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

                                try {
                                    MCrypt mcrypt = new MCrypt();
                                    usuarioD = MCrypt.bytesToHex( mcrypt.encrypt(et_usuario.getText().toString()));
                                    contrasenaD = MCrypt.bytesToHex( mcrypt.encrypt(et_contrasena.getText().toString()));
                                    String[] myTaskParams = {usuarioD, contrasenaD, id_dispositivo};
                                    asyncTask.execute(myTaskParams);
                                    Log.v("usuario",usuarioD);
                                    circuloProgreso = ProgressDialog.show(Login.this, "", "Espere por favor ...", true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
                });
                asyncTaskConection.execute();

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
                .setCancelable(false)
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
                .setCancelable(false)
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
                .setCancelable(false)
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
                .setCancelable(false)
                .show();
    }

    public void conexionNoValida() {
        new AlertDialog.Builder(this)
                .setTitle("Conexión no válida!!!")
                .setMessage("La conexión a internet mediante la cual esta tratando de acceder no es válida, por favor verifiquela e intente de nuevo.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void sinConexion() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexión a internet!!!")
                .setMessage("Por favor conéctese a una red WIFI o Móvil.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }
}
