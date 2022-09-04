package com.a3nitysoft.kelvin.tellme;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class WeburlActivity extends AppCompatActivity {
    WebView mWebView;
    private Menu optionsMenu;
    WebView webView;
    SwipeRefreshLayout swipe;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    ConstraintLayout constraintLayout;
    private Toolbar mainToolbar;
    ProgressBar progressBar;
    private ValueCallback<Uri> mUploadMessage;

    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private View mCustomView;
    private String website;
    private String tag;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weburl);

        mainToolbar = (Toolbar) findViewById(R.id.web_toolbar);
        setSupportActionBar(mainToolbar);

        website = getIntent().getStringExtra("webdomain");
        tag = getIntent().getStringExtra("tag");

        getSupportActionBar().setTitle("Tellme News {" + tag+ "}");
        constraintLayout = findViewById(R.id.cont);
        mWebView = (WebView) findViewById(R.id.webView2);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);
        mWebView.loadUrl(website);
        webSettings();
        setMySwipeRefreshLayout();
    }
    final void setMySwipeRefreshLayout(){
        mySwipeRefreshLayout = findViewById(R.id.webswipe);
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
    @Override
    final public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebViewFragment inside Fragment
            // Use RESULT_OK only if you're implementing WebViewFragment inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    void webSettings(){
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDescription,
                                        String mimetype, long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String fileName = URLUtil.guessFileName(url,contentDescription,mimetype);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

                DownloadManager dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                dManager.enqueue(request);

                Toasty.info(getApplicationContext(), R.string.download_info, Toast.LENGTH_SHORT, true).show();
            }
        });

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


        mWebView.setWebChromeClient(new WebChromeClient(){

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            final protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*,video/*,audio/*,file/*");
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(WeburlActivity.this, "Error: Unable to open file browser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }


            public void onProgressChanged(WebView view, int newProgress){
                progressBar.setProgress(newProgress);
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }
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
