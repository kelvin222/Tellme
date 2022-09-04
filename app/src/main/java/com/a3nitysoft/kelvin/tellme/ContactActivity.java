package com.a3nitysoft.kelvin.tellme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {
    private Toolbar mainToolbar;
    private TextInputLayout feedback;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mFeedProgress;
    private TextView fromView;
    private Button feedsend;
    private DocumentReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFeedProgress = new ProgressDialog(this);
        fromView = (TextView) findViewById(R.id.textView30);
        feedback = (TextInputLayout) findViewById(R.id.textInputLayout);
        feedsend = (Button) findViewById(R.id.button);
            mainToolbar = (Toolbar) findViewById(R.id.contact_toolbar);
            setSupportActionBar(mainToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle("Contact Us");
        if (mAuth.getCurrentUser() != null) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            String current_uid = mUser.getUid();

            mUserDatabase = firebaseFirestore.collection("Users").document(current_uid);

            mUserDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()) {
                            final String name = task.getResult().getString("name");
                            final String email = task.getResult().getString("email");

                            fromView.setText("from : "+ name);
                            feedsend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String mFeedback = feedback.getEditText().getText().toString();

                                    if (!TextUtils.isEmpty(mFeedback)) {
                                        mFeedProgress.setTitle("Connecting to Tellme");
                                        mFeedProgress.setMessage("Please wait, Sending your Feedback !");
                                        mFeedProgress.setCanceledOnTouchOutside(false);
                                        mFeedProgress.show();
                                        sendFeed(name,email,mFeedback);
                                    }
                                }
                            });
                        }
                    }
                }
            });




        }
    }

    private void sendFeed(final String name,final  String email,final String mFeedback) {
        DocumentReference docRef = firebaseFirestore.collection("Feedback").document();
        String feed_id = docRef.getId();
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("name", name);
        postMap.put("email", email);
        postMap.put("message", mFeedback);
        postMap.put("feed_id", feed_id);
        postMap.put("timestamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Feedback").document(feed_id).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFeedProgress.setTitle("Tellme");
                    mFeedProgress.setMessage("The Feedback has been sent !");
                    mFeedProgress.setCanceledOnTouchOutside(false);
                    mFeedProgress.dismiss();
                    feedback.getEditText().setText("");

                } else {


                }
            }
        });
    }
}
