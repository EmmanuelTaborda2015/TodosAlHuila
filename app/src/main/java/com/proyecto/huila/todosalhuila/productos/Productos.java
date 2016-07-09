package com.proyecto.huila.todosalhuila.productos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.crypto.MCrypt;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.webservice.WS_ValidarConexionGoogle;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Productos extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;

    public void networkAvailable() {
        connetion.setVisibility(View.GONE);
    }

    @Override
    public void networkUnavailable() {
        connetion.setVisibility(View.VISIBLE);
        sinConexion();
    }

    private RelativeLayout connetion;

    private ProgressDialog circuloProgreso;

    private ProgressBar progressBar;



    int seleccion = 0;
    WebView myWebView;
    String urlLogin = "http://52.20.189.85/joomlaH/app.php?function=login";
    String urlProducts = "http://52.20.189.85/joomlaH/index.php/tiendavirtual/productos";
    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.productos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.setTitle(R.string.title_activity_productos);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        final WS_ValidarConexionGoogle asyncTask = new WS_ValidarConexionGoogle(new WS_ValidarConexionGoogle.AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output == "false") {
                    connetion.setVisibility(View.VISIBLE);
                }
            }
        });
        asyncTask.execute();

        connetion.setVisibility(View.GONE);


        myWebView = (WebView) this.findViewById(R.id.webView);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);
        myWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.getSettings().setAllowContentAccess(true);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                Productos.this.setProgress(newProgress * 1000);

                progressBar.incrementProgressBy(newProgress);

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        });

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (urlLogin.equals(url)) {
                    myWebView.loadUrl(urlProducts);
                } else {
                    if(start==0){
                        view.clearHistory();
                        start++;
                    }

                    view.loadUrl("javascript:document.getElementById(\"g-header\").setAttribute(\"style\",\"display:none;\");");
                    //view.loadUrl("javascript:document.getElementById(\"g-container\").setAttribute(\"style\",\"display:none;\");");
                    view.loadUrl("javascript:document.getElementById(\"g-footer\").setAttribute(\"style\",\"display:none;\");");
                }

                Button botonAnterior = (Button) Productos.this.findViewById(R.id.botonAnterior);

                if (view.canGoBack())
                {
                    botonAnterior.setEnabled(true);
                }
                else
                {
                    botonAnterior.setEnabled(false);
                }

                Button botonSiguiente = (Button) Productos.this.findViewById(R.id.botonSiguiente);

                if (view.canGoForward())
                {
                    botonSiguiente.setEnabled(true);
                }

            }


        });

        myWebView.clearCache(true);
        myWebView.loadUrl(urlLogin);
    }

    public void salir() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cerrar_aplicacion)
                .setMessage(R.string.salir_aplicacion)
                .setPositiveButton(R.string.opcionSi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton(R.string.opcionNo, null)
                .show();
    }

    public void anterior(View view)
    {
        myWebView.goBack();
    }

    public void siguiente(View view)
    {
        myWebView.goForward();
    }

    @Override
    public void onBackPressed() {
      salir();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        if (id == R.id.action_salir) {
            salir();
        } else if (id == R.id.action_home) {
            Intent i = new Intent(Productos.this, InicioLogin.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categorias, menu);
        return true;
    }

    public void conexionNoValida() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloConexion)
                .setMessage(R.string.mensaConexion)
                .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = 0;
                        Intent i = new Intent(Productos.this, InicioLogin.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void sinConexion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tituloSinConexion)
                .setMessage(R.string.mensaSinConexion)
                .setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion = 0;
                        Intent i = new Intent(Productos.this, InicioLogin.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

}
