package com.a3nitysoft.kelvin.tellme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilerActivity extends AppCompatActivity {
    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileState, mProfileAbout;
    private TextView mProfileRev, mProfileAud, mProfileTag;
    private ImageButton mProfileTagBtn, mProfileUntagBtn;
    private DocumentReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mAudienceDatabase;
    private DatabaseReference mTaggedDatabase;
    private CollectionReference mNotificationDatabase;
    private CollectionReference amNotificationDatabase;
    private DatabaseReference mUsers;
    private DatabaseReference nUsers;
    private DatabaseReference pUsers;

    private DatabaseReference mRootRef;

    private DocumentReference PUserDatabase;
    private DocumentReference UserDatabase;
    private FirebaseUser mCurrent_user;
    private Toolbar mToolbar;

    private String mCurrent_state, mCuser;
    private String Pname, Pimage, Uname, Uimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiler);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.Pro_page_toolbar);
        setSupportActionBar(mToolbar);


        firebaseFirestore = FirebaseFirestore.getInstance();

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mCuser = mCurrent_user.getUid();
        PUserDatabase = firebaseFirestore.collection("Users").document(user_id);
        UserDatabase = firebaseFirestore.collection("Users").document(mCuser);
        mAudienceDatabase = FirebaseDatabase.getInstance().getReference().child("Audience");
        mTaggedDatabase = FirebaseDatabase.getInstance().getReference().child("Tagged");
        mNotificationDatabase = firebaseFirestore.collection("Users/" + user_id + "/notification");
        amNotificationDatabase = firebaseFirestore.collection("Users/" + mCuser + "/notification");
        nUsers = FirebaseDatabase.getInstance().getReference().child("Audience").child(user_id);
        mUsers = FirebaseDatabase.getInstance().getReference().child("Tagged").child(user_id);

        pUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProfileImage = (CircleImageView) findViewById(R.id.setup_image);
        mProfileName = (TextView) findViewById(R.id.setup_name);
        mProfileState = (TextView) findViewById(R.id.setup_sta);
        mProfileAbout = (TextView) findViewById(R.id.setup_avt);
        mProfileAud = (TextView) findViewById(R.id.aud);
        mProfileRev = (TextView) findViewById(R.id.rev);
        mProfileTag = (TextView) findViewById(R.id.tag);
        mProfileTagBtn = (ImageButton) findViewById(R.id.tagbtn);
        mProfileUntagBtn = (ImageButton) findViewById(R.id.untag);

        mCurrent_state = "not_friends";

        mProfileUntagBtn.setVisibility(View.INVISIBLE);
        mProfileUntagBtn.setEnabled(false);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        firebaseFirestore.collection("Reviews").whereEqualTo("user_id", user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();

                        mProfileRev.setText(String.valueOf(count));
                    } else {
                        mProfileRev.setText(String.valueOf("0"));
                    }
                }
            }
        });
        firebaseFirestore.collection("Follow/" + "Audience/" + user_id ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();

                        mProfileAud.setText(String.valueOf(count));
                    } else {
                        mProfileAud.setText(String.valueOf("0"));
                    }
                }
            }
        });


        firebaseFirestore.collection("Follow/" + "Tagged/" + user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        mProfileTag.setText(String.valueOf(count));
                    } else {
                        mProfileTag.setText(String.valueOf("0"));
                    }
                }
            }
        });


        PUserDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Pname = task.getResult().getString("name");
                        Pimage = task.getResult().getString("image");
                        String state = task.getResult().getString("state");
                        String about = task.getResult().getString("about");

                        mProfileName.setText(Pname);
                        mProfileState.setText(state);
                        mProfileAbout.setText(about);

                        getSupportActionBar().setTitle(Pname + "'s" + " profile");

                        if (!Pimage.equals("default")) {
                            Picasso.with(ProfilerActivity.this).load(Pimage).placeholder(R.drawable.dp).into(mProfileImage);

                        }





                    }
                    if (mCurrent_user.getUid().equals(user_id)) {

                        mProfileUntagBtn.setEnabled(false);
                        mProfileUntagBtn.setVisibility(View.INVISIBLE);

                        mProfileTagBtn.setEnabled(false);
                        mProfileTagBtn.setVisibility(View.INVISIBLE);
                        mProgressDialog.dismiss();

                    }else {

                        firebaseFirestore.collection("Follow/" + "Audience/" + user_id).document(mCuser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.getResult().exists()) {
                                    String req_type = task.getResult().getString("type");

                                    if (req_type.equals("tagged")) {
                                        mCurrent_state = "tagged";

                                        mProfileUntagBtn.setVisibility(View.VISIBLE);
                                        mProfileUntagBtn.setEnabled(true);

                                        mProfileTagBtn.setVisibility(View.INVISIBLE);
                                        mProfileTagBtn.setEnabled(true);

                                        mProgressDialog.dismiss();
                                    }


                                } else {


                                    mCurrent_state = "untagged";

                                    mProfileTagBtn.setVisibility(View.VISIBLE);
                                    mProfileTagBtn.setEnabled(true);


                                    mProfileUntagBtn.setVisibility(View.INVISIBLE);
                                    mProfileUntagBtn.setEnabled(false);


                                }

                                mProgressDialog.dismiss();

                            }

                        });


                        mProfileUntagBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mProfileUntagBtn.setVisibility(View.INVISIBLE);
                                // - -------------- CANCEL REQUEST STATE ------------

                                if (mCurrent_state.equals("tagged")) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are You Sure");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int b) {
                                            if (b == 0) {
                                                firebaseFirestore.collection("Follow/" + "Tagged/" + mCuser).document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        firebaseFirestore.collection("Follow/" + "Audience/" + user_id).document(mCuser).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                firebaseFirestore.collection("Users/" + user_id + "/audience").document(mCuser).delete();
                                                                firebaseFirestore.collection("Users/" + mCuser + "/tagged").document(user_id).delete();
                                                                mProfileTagBtn.setEnabled(true);
                                                                mProfileTagBtn.setVisibility(View.VISIBLE);
                                                                mCurrent_state = "untagged";
                                                                mProfileTagBtn.setEnabled(false);
                                                            }
                                                        });
                                                    }
                                                });
                                            } else if (b == 1) {

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        });


                        mProfileTagBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mProfileTagBtn.setVisibility(View.INVISIBLE);

                                // --------------- NOT FRIENDS STATE ------------

                                if (mCurrent_state.equals("untagged")) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setTitle("Are You Sure");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int b) {
                                            if (b == 0) {


                                                DocumentReference newNotificationref = mNotificationDatabase.document();
                                                String newNotificationId = newNotificationref.getId();

                                                HashMap<String, Object> notificationData = new HashMap<>();
                                                notificationData.put("from", mCurrent_user.getUid());
                                                notificationData.put("not_id", newNotificationId);
                                                notificationData.put("type", "tagged");
                                                notificationData.put("title", "New Audience");
                                                notificationData.put("message", "has tagged you");
                                                notificationData.put("timestamp", FieldValue.serverTimestamp());


                                                HashMap<String, Object> notificateData = new HashMap<>();
                                                notificateData.put("from", user_id);
                                                notificateData.put("not_id", newNotificationId);
                                                notificateData.put("type", "tagged");
                                                notificateData.put("title", "New Tag");
                                                notificateData.put("message", "You have tagged");
                                                notificateData.put("timestamp", FieldValue.serverTimestamp());




                                                amNotificationDatabase.document(newNotificationId).set(notificateData);

                                                mNotificationDatabase.document(newNotificationId).set(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            HashMap<String, String> follow = new HashMap<>();
                                                            follow.put("type", "tagged");
                                                            follow.put("name", Pname);
                                                            follow.put("image", Pimage);
                                                            firebaseFirestore.collection("Follow/" + "Audience/" + user_id).document(mCuser).set(follow);
                                                            firebaseFirestore.collection("Follow/" + "Tagged/" + mCuser).document(user_id).set(follow);
                                                            HashMap<String, Object> audi = new HashMap<>();
                                                            audi.put("type", "true");
                                                            audi.put("uid",user_id);
                                                            audi.put("timestamp", FieldValue.serverTimestamp());

                                                            HashMap<String, Object> audi2 = new HashMap<>();
                                                            audi2.put("type", "true");
                                                            audi2.put("uid", mCuser);
                                                            audi2.put("timestamp", FieldValue.serverTimestamp());



                                                            firebaseFirestore.collection("Users/" + user_id + "/audience").document(mCuser).set(audi2);
                                                            firebaseFirestore.collection("Users/" + mCuser + "/tagged").document(user_id).set(audi);

                                                            mCurrent_state = "tagged";
                                                            mProfileTagBtn.setEnabled(false);
                                                            mProfileUntagBtn.setVisibility(View.VISIBLE);
                                                            mProfileUntagBtn.setEnabled(true);

                                                        } else {

                                                            Toast.makeText(ProfilerActivity.this, "Unable to Tag this user, Try later. ", Toast.LENGTH_SHORT).show();


                                                        }

                                                    }


                                                });


                                            } else if (b == 1) {

                                            }
                                        }
                                    });
                                    builder.show();

                                }

                            }

                        });

                    }
                }
            }
        });
    }
}
