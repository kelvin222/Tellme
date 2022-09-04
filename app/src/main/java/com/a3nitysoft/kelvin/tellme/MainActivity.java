package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private String current_user_id;
    private BottomNavigationView mainbottomNav;
    private NijaFragment nijaFragment;
    private SportFragment sportFragment;
    private WorldFragment worldFragment;
    private EntFragment entFragment;
    private Button mRev;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ViewPager mViewPager;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Tellme");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mainbottomNav = findViewById(R.id.mainBottomNav);
            mRev = findViewById(R.id.RevBtn);

            // FRAGMENTS
            worldFragment = new WorldFragment();
            nijaFragment = new NijaFragment();
            sportFragment = new SportFragment();
            entFragment = new EntFragment();

            initializeFragment();
            mRev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reviewIntent = new Intent(MainActivity.this, ReviewViewActivity.class);
                    startActivity(reviewIntent);
                }
            });

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.bottom_world:

                            replaceFragment(worldFragment, currentFragment);
                            return true;

                        case R.id.bottom_nija:

                            replaceFragment(nijaFragment, currentFragment);
                            return true;

                        case R.id.bottom_sport:

                            replaceFragment(sportFragment, currentFragment);
                            return true;

                        case R.id.bottom_ent:

                            replaceFragment(entFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });


        }


        }




    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToStart();

        } else {



        }
        // Check if user is signed in (non-null) and update UI accordingly.


    }


    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, Splash.class);
        startActivity(startIntent);
        finish();

    }

    private void logOut() {


        mAuth.signOut();
        sendToStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.action_logout_btn){

            logOut();
        }

        if(item.getItemId() == R.id.action_settings_btn){

            Intent settingsIntent = new Intent(MainActivity.this, Account.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_search_btn){

            Intent settingsIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_post_btn){

            Intent settingsIntent = new Intent(MainActivity.this, PostNewsActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_feedback_btn){

            Intent settingsIntent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_feed_btn){

            Intent settingsIntent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId() == R.id.action_about_btn){

            Intent settingsIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(settingsIntent);

        }


        return true;
    }
    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        fragmentTransaction.add(R.id.main_container, nijaFragment);
        fragmentTransaction.add(R.id.main_container, sportFragment);
        fragmentTransaction.add(R.id.main_container, entFragment);
        fragmentTransaction.add(R.id.main_container, worldFragment);

        fragmentTransaction.hide(nijaFragment);
        fragmentTransaction.hide(sportFragment);
        fragmentTransaction.hide(entFragment);

        fragmentTransaction.commit();

    }
    private void replaceFragment(Fragment fragment, Fragment currentFragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment == worldFragment) {

            fragmentTransaction.hide(nijaFragment);
            fragmentTransaction.hide(sportFragment);
            fragmentTransaction.hide(entFragment);

        }

        if (fragment == nijaFragment) {

            fragmentTransaction.hide(worldFragment);
            fragmentTransaction.hide(sportFragment);
            fragmentTransaction.hide(entFragment);

        }

        if (fragment == sportFragment) {

            fragmentTransaction.hide(worldFragment);
            fragmentTransaction.hide(nijaFragment);
            fragmentTransaction.hide(entFragment);

        }

        if (fragment == entFragment) {

            fragmentTransaction.hide(worldFragment);
            fragmentTransaction.hide(nijaFragment);
            fragmentTransaction.hide(sportFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

}
