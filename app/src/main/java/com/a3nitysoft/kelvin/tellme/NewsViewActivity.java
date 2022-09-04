package com.a3nitysoft.kelvin.tellme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import static java.security.AccessController.getContext;

public class NewsViewActivity extends AppCompatActivity {
    private String head;
    private String tag;
    private String time,date;
    private String source;
    private String intro;
    private String body;
    private String image;
    private String video;
    private String section;


    private Toolbar mainToolbar;
    private TextView mHead;
    private TextView mIntro;
    private TextView mBody;

    private TextView mDate;
    private Button mSource;
    private Button mPlayvid;
    private Button mRevn;
    private ImageView mImage;
    private AdView mAdView;

    private static final String TAG = "NewsViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        MobileAds.initialize(this, "ca-app-pub-4481331290406181~1599005010");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        head = getIntent().getStringExtra("head");
        tag = getIntent().getStringExtra("tag");
        source = getIntent().getStringExtra("source");
        video = getIntent().getStringExtra("video");
        intro = getIntent().getStringExtra("intro");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");
        image = getIntent().getStringExtra("image");
        section = getIntent().getStringExtra("section");

        getSupportActionBar().setTitle(section + " News");
        mBody = (TextView) findViewById(R.id.postbody);
        mHead = (TextView) findViewById(R.id.posthead);
        mIntro = (TextView) findViewById(R.id.postintro);
        mDate = (TextView) findViewById(R.id.postdate);
        mSource = (Button) findViewById(R.id.sourceBtn);
        mRevn = (Button) findViewById(R.id.revBbtn);
        mPlayvid = (Button) findViewById(R.id.playvid);
        mImage = (ImageView) findViewById(R.id.postimg);
        mHead.setText(head);
        mBody.setText(body);
        mIntro.setText(intro);
        mDate.setText(date);
        if (video.equals("default")){
            mPlayvid.setVisibility(View.INVISIBLE);
        }else {
            mPlayvid.setVisibility(View.VISIBLE);
        }

        Picasso.with(this).load(image).placeholder(R.drawable.splashlogo).into(mImage);

        mPlayvid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(NewsViewActivity.this, WebViewerActivity.class);
                newIntent.putExtra("videoUrl", video);
                startActivity(newIntent);
            }
        });

        mSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anewIntent = new Intent(NewsViewActivity.this, WeburlActivity.class);
                anewIntent.putExtra("webdomain", source);
                anewIntent.putExtra("tag", tag);
                startActivity(anewIntent);
            }
        });

        mRevn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsIntent = new Intent(NewsViewActivity.this, PostReviewActivity.class);
                newsIntent.putExtra("head", head);
                newsIntent.putExtra("tag", tag);
                newsIntent.putExtra("section", section);
                newsIntent.putExtra("pic", image);

                startActivity(newsIntent);
            }
        });
    }
}
