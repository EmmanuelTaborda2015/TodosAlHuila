package com.proyecto.huila.todosalhuila.productos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.proyecto.huila.todosalhuila.R;
import com.proyecto.huila.todosalhuila.crypto.MCrypt;

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

public class Productos extends Activity {

    WebView myWebView;
    String urlLogin = "http://52.20.189.85/joomlaH/app.php?function=login";
    String urlProducts = "http://52.20.189.85/joomlaH/index.php?option=com_virtuemart&view=virtuemart&productsublayout=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.productos);
        myWebView = (WebView) this.findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        //myWebView.loadUrl();




        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if(urlLogin.equals(url)){
                    myWebView.loadUrl(urlProducts);
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
        myWebView.loadUrl(urlLogin);
    }
}
