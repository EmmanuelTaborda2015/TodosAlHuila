package com.proyecto.huila.todosalhuila.herramientas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import com.proyecto.huila.todosalhuila.R;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.proyecto.huila.todosalhuila.webservice.WS_Marcador;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexion;

import java.util.List;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by etaborda on 23/02/16.
 */
public abstract class ServicioWeb{

    private Context context;

    private Thread thread_validarConexion;
    private String webResponse_conexion;
    private AlertDialog alertaConexion;
    private Handler handler_conexion = new Handler();

    public ServicioWeb(Context context) {
        this.context = context;
        isOnline();
    }

    public abstract void startWebAccess();

    public abstract List<String> getTipoSitioTuristico();

    public abstract List<String> getSitioTuristico();

    public abstract List<String> getNombreSitioTuristico();

    public abstract List<String> getCoordX();

    public abstract List<String> getCoordY();


    public void isOnline() {

        runOnUiThread(new Runnable() {
            public void run() {
                WS_ValidarConexion validacion = new WS_ValidarConexion();
                webResponse_conexion = validacion.startWebAccess();

                handler_conexion.post(conexion);
            }
        });
    }

    final Runnable conexion = new Runnable() {
        public void run() {
            if("false".equals(webResponse_conexion)){
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