package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {
private Toolbar mainToolbar;
private Button poli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        mainToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("About Tellme");
        poli = (Button) findViewById(R.id.policyBtn);
        poli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent polIntent = new Intent(AboutActivity.this, PolicyActivity.class);
                startActivity(polIntent);
            }
        });
    }
}
