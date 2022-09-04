package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostReviewActivity extends AppCompatActivity {
    private Uri postImageUri = null;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    private Toolbar mainToolbar;

    private String head;
    private String tag;
    private String section;
    private String name;
    private String image;
    private String pic;


    private TextView mHead;
    private TextView mSection;
    private TextView mUsername;
    private EditText mBody;
    private FloatingActionButton mRevbtn;
    private ImageView mImg;
    private CircleImageView mPicture;
    private DocumentReference mUserDatabase;
    private CollectionReference mADatabase;
    private StorageReference storageReference;
    private FirebaseUser mCurrentUser;


    private AdView mAdView;

    private static final String TAG = "PostReviewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

        MobileAds.initialize(this, "ca-app-pub-4481331290406181~1599005010");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        head = getIntent().getStringExtra("head");
        tag = getIntent().getStringExtra("tag");
        section = getIntent().getStringExtra("section");
        pic = getIntent().getStringExtra("pic");

        getSupportActionBar().setTitle(tag);
        mBody = (EditText) findViewById(R.id.RevBody);
        mHead = (TextView) findViewById(R.id.RevHead);
        mSection = (TextView) findViewById(R.id.RevSect);
        mPicture = (CircleImageView) findViewById(R.id.RevPic);
        mUsername = (TextView) findViewById(R.id.RevUsers);
        mImg = (ImageView) findViewById(R.id.RevImg);
        mHead.setText(head);
        mSection.setText(section);
        mRevbtn = (FloatingActionButton) findViewById(R.id.RevButton);

        Picasso.with(PostReviewActivity.this).load(pic).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.dp).into(mImg, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(PostReviewActivity.this).load(pic).placeholder(R.drawable.dp).into(mImg);

            }
        });

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = firebaseFirestore.collection("Users").document(current_uid);

        mUserDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        final String name = task.getResult().getString("name");
                        mUsername.setText(name);
                        final String image = task.getResult().getString("thumb");
                        Picasso.with(PostReviewActivity.this).load(image).placeholder(R.drawable.dp).into(mPicture);


                        List<String> list = new ArrayList<>();
                        mADatabase = firebaseFirestore.collection("Users/" + current_uid + "/audience");
                        mADatabase.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String pip = document.getId();
                                        list.add(pip);
                                    }
                                }
                            }
                        });


                        mRevbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DocumentReference docRef = firebaseFirestore.collection("Reviews").document();
                                String rev_id = docRef.getId();
                                String body = mBody.getText().toString();
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("image_url", pic);
                                postMap.put("head", head);
                                postMap.put("tag", tag);
                                postMap.put("section", section);
                                postMap.put("body", body);
                                postMap.put("audience", list);
                                postMap.put("rev_id", rev_id);
                                postMap.put("user_id", current_uid);
                                postMap.put("username", name);
                                postMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Reviews").document(rev_id).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(PostReviewActivity.this, "Review posted", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(PostReviewActivity.this, ReviewViewActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {


                                        }
                                    }
                                });
                            }
                        });

                    }
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(PostReviewActivity.this, "Firestore Load Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }

            }

        });

    }
}
