package com.proyecto.huila.todosalhuila.productos;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
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
import com.proyecto.huila.todosalhuila.conexion.CustomSSLSocketFactory;
import com.proyecto.huila.todosalhuila.conexion.NetworkStateReceiver;
import com.proyecto.huila.todosalhuila.crypto.MCrypt;
import com.proyecto.huila.todosalhuila.inicio.InicioLogin;
import com.proyecto.huila.todosalhuila.login.Login;
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
import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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

    private boolean menuActivo = true;



    int seleccion = 0;
    WebView myWebView;
    String urlLogin = "https://huila.travel/app.php";
    String urlProducts = "https://huila.travel/index.php?option=com_virtuemart&view=virtuemart&productsublayout=0&lang=es";
    private int start = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.productos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(R.string.title_activity_productos);

        connetion = (RelativeLayout) findViewById(R.id.conexion);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));



        URL url = null;
        try {
            url = new URL("https://huila.travel/app.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {

        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            connection.setSSLSocketFactory(CustomSSLSocketFactory.getSSLSocketFactory(this));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

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

        //deleteCache(getApplicationContext());
        //clearApplicationData();

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.clearCache(true);
        myWebView.getSettings().setAppCacheMaxSize(0);
        myWebView.getSettings().setDomStorageEnabled(true);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        myWebView.setWebChromeClient(new WebChromeClient() {
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

           // @Override
           // public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
             //   handler.proceed();
                // Ignore SSL certificate errors
            //}

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Productos.this);
                builder.setMessage("No se ha podido validar de manera adecuada el certificado de seguridad.\n ¿Desea continuar?");
                builder.setPositiveButton("continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if (start > 0) {
                    ((Button) Productos.this.findViewById(R.id.botonDetener)).setEnabled(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (urlLogin.equals(url)) {
                    myWebView.loadUrl(urlProducts);
                } else {
                    if (start == 0) {
                        view.clearHistory();
                        start++;
                        circuloProgreso.dismiss();
                    }

                    view.loadUrl("javascript:document.getElementById(\"g-header\").setAttribute(\"style\",\"display:none;\");");
                    //view.loadUrl("javascript:document.getElementById(\"g-container\").setAttribute(\"style\",\"display:none;\");");
                    view.loadUrl("javascript:document.getElementById(\"g-footer\").setAttribute(\"style\",\"display:none;\");");
                    view.loadUrl("javascript:document.getElementById(\"social-toolbar\").setAttribute(\"style\",\"display:none;\");");

                }

                Button botonAnterior = (Button) Productos.this.findViewById(R.id.botonAnterior);

                if (view.canGoBack()) {
                    botonAnterior.setEnabled(true);
                } else {
                    botonAnterior.setEnabled(false);
                }

                Button botonSiguiente = (Button) Productos.this.findViewById(R.id.botonSiguiente);

                if (view.canGoForward()) {
                    botonSiguiente.setEnabled(true);
                }

                ((Button) Productos.this.findViewById(R.id.botonDetener)).setEnabled(false);

            }


        });

        String postData = "function=login&username="+new Login().usuarioD+"&password="+ new Login().contrasenaD;
        myWebView.postUrl(urlLogin, EncodingUtils.getBytes(postData, "BASE64"));

        if(start==0){
            circuloProgreso = ProgressDialog.show(Productos.this, "", Productos.this.getResources().getString(R.string.esperar), true);
        }

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

    public void detener(View view)
    {
        myWebView.stopLoading();
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

