package com.proyecto.huila.todosalhuila.herramientas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.categorias.Categorias;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexion;


public class AppStatus extends Categorias{



    private Thread thread_validarConexion;
    private String webResponse_conexion;
    private AlertDialog alertaConexion;
    private Handler handler_conexion = new Handler();

    static Context context;

    public  AppStatus (Context ctx) {
        context = ctx;
    }

    public Thread isOnline() {
        thread_validarConexion = new Thread() {
            public void run() {
                Looper.prepare();
                WS_ValidarConexion validarConexion = new WS_ValidarConexion();
                webResponse_conexion = validarConexion.startWebAccess();
                handler_conexion.post(conexion);
            }
        };

        thread_validarConexion.start();

        return thread_validarConexion;
    }

    final Runnable conexion = new Runnable() {

        public void run() {
            if("false".equals(webResponse_conexion)){
                Toast.makeText(context, webResponse_conexion, Toast.LENGTH_LONG).show();
                // wifi connection was lost
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.tituloSinConexion);
                builder.setMessage(R.string.mensaSinConexion);
                builder.setPositiveButton(R.string.botonAceptar,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                //builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);
                alertaConexion = builder.create();
                alertaConexion.show();
            }
        }
    };
}

