package com.a3nitysoft.kelvin.tellme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class FeedviewActivity extends AppCompatActivity {
    private Toolbar mainToolbar;
    private String name, mail, message, date;
    private TextView fname, fmail, fdate, fmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedview);


        mainToolbar = (Toolbar) findViewById(R.id.feedview_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = getIntent().getStringExtra("name");
        mail = getIntent().getStringExtra("mail");
        message = getIntent().getStringExtra("message");
        date = getIntent().getStringExtra("fdate");
        getSupportActionBar().setTitle("Feedbacks from "+name);
        fdate = (TextView) findViewById(R.id.fe_time);
        fname = (TextView) findViewById(R.id.fe_name);
        fmail = (TextView) findViewById(R.id.fe_mail);
        fmessage = (TextView) findViewById(R.id.fe_message);
        fname.setText(name);
        fmessage.setText(message);
        fmail.setText(mail);
        fdate.setText(date);



    }
}
