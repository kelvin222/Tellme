package com.a3nitysoft.kelvin.tellme;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import es.dmoral.toasty.Toasty;

public class WebViewerActivity extends AppCompatActivity {
    WebView mWebView;
    private Menu optionsMenu;
    WebView webView;
    SwipeRefreshLayout swipe;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    private ValueCallback<Uri> mUploadMessage;
    private Toolbar mainToolbar;

    private String video;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewer);

        mainToolbar = (Toolbar) findViewById(R.id.webvid_toolbar);
        setSupportActionBar(mainToolbar);
        video = getIntent().getStringExtra("videoUrl");
        getSupportActionBar().setTitle("Tellme News");
        constraintLayout = findViewById(R.id.con);
        mWebView = (WebView) findViewById(R.id.webView);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);
        mWebView.loadUrl(video);
        webSettings();
        setMySwipeRefreshLayout();
    }



    final void setMySwipeRefreshLayout(){
        mySwipeRefreshLayout = findViewById(R.id.swipe);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    final public void onRefresh() {
                        mWebView.reload();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    void webSettings(){
        mWebView.setWebViewClient(new WebViewClient()
        {

            public void onReceivedError(WebView mWebView, int i, String s, String d1)
            {
                Toasty.error(getApplicationContext(),"No Internet Connection!").show();
                mWebView.loadUrl("file:///android_asset/net_error.html");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }


        });

        WebSettings webSettings = mWebView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.getSaveFormData();
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
        // webSettings.setSupportMultipleWindows(true); //?a href problem
        webSettings.getJavaScriptEnabled();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        //  webSettings.setJavaScriptCanOpenWindowsAutomatically(false); //(popup)
    }

}
