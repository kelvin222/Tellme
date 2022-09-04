package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoadingActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    RelativeLayout rely1, rely2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rely1.setVisibility(View.VISIBLE);
            rely2.setVisibility(View.VISIBLE);
            Intent nIntent = new Intent(LoadingActivity.this, ReviewViewActivity.class);
            startActivity(nIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        rely1 = (RelativeLayout) findViewById(R.id.relya1);
        rely2 = (RelativeLayout) findViewById(R.id.relyb2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        handler.postDelayed(runnable, 3000);

    }
}
