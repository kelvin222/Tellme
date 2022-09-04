package com.a3nitysoft.kelvin.tellme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static java.security.AccessController.getContext;

public class Account extends AppCompatActivity {


    private RecyclerView mAccountList;

    private DocumentReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private List<RevPost> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private AccountAdapter accountAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private DatabaseReference mReviewBase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private String head;
    private String image_url;
    private String tag;
    private String source;
    private String body;
    private String section;
    private String date;

    private String mCurrent_user_id;

    private CircleImageView setupImage;
    private Uri postImageUri = null;


    private boolean isChanged = false;
    private DocumentReference mUsers;
    private DocumentReference nUsers;

    private TextView setupName;
    private TextView setupAbout;
    private TextView setupState;
    private TextView setupRev;
    private TextView setupAud;
    private TextView setupTag;
    private EditText setupAboutChange;
    private ImageView setAbtimg;
    private ImageView setImg;
    private RadioButton setAbtSend;
    private static final int GALLERY_PICK = 1;

    private static final String TAG = "Account";

    private StorageReference storageReference;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrent_user_id = mCurrentUser.getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mUsers = firebaseFirestore.collection("Audience").document(mCurrentUser.getUid());
        nUsers = firebaseFirestore.collection("Tagged").document(mCurrentUser.getUid());


        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupAbout = findViewById(R.id.setup_abt);
        setupState = findViewById(R.id.setup_state);
        setupRev = findViewById(R.id.accrev);
        setupAud = findViewById(R.id.accaud);
        setupTag = findViewById(R.id.acctag);
        setupAboutChange = findViewById(R.id.setup_about);
        setAbtimg = findViewById(R.id.abtimg);
        setAbtSend = findViewById(R.id.radioButton);


        String current_uid = mCurrentUser.getUid();

        mUserDatabase = firebaseFirestore.collection("Users").document(current_uid);

        mUserDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                          if (task.isSuccessful()) {
                                                              if (task.getResult().exists()) {
                                                                  String name = task.getResult().getString("name");
                                                                  final String image = task.getResult().getString("image");
                                                                  String state = task.getResult().getString("state");
                                                                  String about = task.getResult().getString("about");

                                                                  setupName.setText(name);
                                                                  setupState.setText(state);
                                                                  setupAbout.setText(about);

                                                                  if (!image.equals("default")) {

                                                                      //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                                                                      Picasso.with(Account.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                                                              .placeholder(R.drawable.dp).into(setupImage, new Callback() {
                                                                          @Override
                                                                          public void onSuccess() {

                                                                          }

                                                                          @Override
                                                                          public void onError() {

                                                                              Picasso.with(Account.this).load(image).placeholder(R.drawable.dp).into(setupImage);

                                                                          }
                                                                      });

                                                                  }

                                                              }
                                                          } else {
                                                              String errorMessage = task.getException().getMessage();
                                                              Toast.makeText(Account.this, "Firestore Load Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                                          }


            }
        });


        blog_list = new ArrayList<>();
        mAccountList = (RecyclerView) findViewById(R.id.accountList);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        accountAdapter = new AccountAdapter(blog_list);
        mAccountList.setLayoutManager(new LinearLayoutManager(this));
        mAccountList.setAdapter(accountAdapter);
        mAccountList.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            Query firstQuery = firebaseFirestore.collection("Reviews").whereEqualTo("user_id", user_id).limit(25);
            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String revPostId = doc.getDocument().getId();
                                RevPost revPost = doc.getDocument().toObject(RevPost.class).withId(revPostId);

                                if (isFirstPageFirstLoad) {

                                    blog_list.add(revPost);

                                } else {

                                    blog_list.add(0, revPost);

                                }


                                accountAdapter.notifyDataSetChanged();

                            }


                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });
        }

        firebaseFirestore.collection("Reviews").whereEqualTo("user_id", mCurrent_user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();

                        setupRev.setText(String.valueOf(count));
                    } else {
                        setupRev.setText(String.valueOf("0"));
                    }
                }
            }
        });
        firebaseFirestore.collection("Follow/" + "Audience/" + mCurrent_user_id ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();

                        setupAud.setText(String.valueOf(count));
                    } else {
                        setupAud.setText(String.valueOf("0"));
                    }
                }
            }
        });


        firebaseFirestore.collection("Follow/" + "Tagged/" + mCurrent_user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        setupTag.setText(String.valueOf(count));
                    } else {
                        setupTag.setText(String.valueOf("0"));
                    }
                }
            }
        });




        setAbtimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAbout.setVisibility(View.INVISIBLE);
                setupAboutChange.setVisibility(View.VISIBLE);
                setAbtSend.setVisibility(View.VISIBLE);
                setAbtimg.setVisibility(View.INVISIBLE);

            }
        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .setOutputCompressQuality(40)
                        .start(Account.this);
            }
        });

        setAbtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String about1 = setupAboutChange.getText().toString();

                Map<String, Object> post1Map = new HashMap<>();
                post1Map.put("about", about1);


                mUserDatabase.update(post1Map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Account.this, "About Updated", Toast.LENGTH_LONG).show();

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(Account.this, "Error : " + error, Toast.LENGTH_LONG).show();

                        }


                    }
                });

                setupAbout.setText(about1);
                setupAbout.setVisibility(View.VISIBLE);
                setAbtSend.setVisibility(View.INVISIBLE);
                setAbtimg.setVisibility(View.VISIBLE);
                setupAboutChange.setVisibility(View.INVISIBLE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                postImageUri = result.getUri();
                setupImage.setImageURI(postImageUri);

                File newImageFile = new File(postImageUri.getPath());
                try {

                    compressedImageFile = new Compressor(Account.this)
                            .setMaxHeight(300)
                            .setMaxWidth(300)
                            .setQuality(30)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] imageData = baos.toByteArray();

                final StorageReference thumb_filepath = storageReference.child("profile_images").child("thumbs").child(user_id + ".jpg");

                final StorageReference ref = storageReference.child("profile_images").child(user_id + ".jpg");
                UploadTask filePath = thumb_filepath.putBytes(imageData);

                ref.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();
                        final String imgUri = downloadUri.toString();
                        Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return thumb_filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri idownloadUri = task.getResult();

                                    final String doeUri = idownloadUri.toString();
                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image", imgUri);
                                    postMap.put("thumb", doeUri);


                                    mUserDatabase.update(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(Account.this, "Display Picture Updated", Toast.LENGTH_LONG).show();

                                            } else {

                                                String error = task.getException().getMessage();
                                                Toast.makeText(Account.this, "Error : " + error, Toast.LENGTH_LONG).show();

                                            }


                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


}


