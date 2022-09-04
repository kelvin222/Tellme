package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

public class ReviewViewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tellme Reviews");

        //Tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void sendToStart() {

        Intent startIntent = new Intent(ReviewViewActivity.this, Splash.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_revmenu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.action_logout_btn){


        }

        if(item.getItemId() == R.id.action_settings_btn){

            Intent settingsIntent = new Intent(ReviewViewActivity.this, Account.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_search_btn){

            Intent settingsIntent = new Intent(ReviewViewActivity.this, SearchActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_audience_btn){

            Intent settingsIntent = new Intent(ReviewViewActivity.this, AudienceActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_tagged_btn){

            Intent settingsIntent = new Intent(ReviewViewActivity.this, TaggedActivity.class);
            startActivity(settingsIntent);

        }



        return true;
    }
}
