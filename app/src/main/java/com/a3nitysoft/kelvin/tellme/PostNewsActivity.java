


package com.a3nitysoft.kelvin.tellme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


import id.zelory.compressor.Compressor;

public class PostNewsActivity extends AppCompatActivity {

    private Uri postImageUri = null;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    private Toolbar mainToolbar;

    private Spinner mSection;
    private TextInputLayout mHead;
    private TextInputLayout mTag;
    private TextInputLayout mUrl;
    private TextInputLayout mIntro;
    private TextInputLayout mBody;
    private TextInputLayout mVideo;
    private FloatingActionButton mPostbtn;
    private ImageView mPicture;
    private DatabaseReference mPostDatabase;
    private StorageReference storageReference;
    private FirebaseUser mCurrentUser;

    private String saveCurrentTime, saveCurrentDate, ssVideo;


    //Progress
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final String current = mCurrentUser.getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Post News");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);


        mSection = (Spinner) findViewById(R.id.spinsection);
        mHead = (TextInputLayout) findViewById(R.id.posthead);
        mTag = (TextInputLayout) findViewById(R.id.posttag);
        mUrl = (TextInputLayout) findViewById(R.id.posturl);
        mIntro = (TextInputLayout) findViewById(R.id.postintro);
        mBody = (TextInputLayout) findViewById(R.id.postbody);
        mVideo = (TextInputLayout) findViewById(R.id.postvid);
        mPostbtn = (FloatingActionButton) findViewById(R.id.postbtn);
        mPicture = (ImageView) findViewById(R.id.postimg);
        String[] sect = {"", "World", "Nigeria", "Sports","Entertainment"};

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(PostNewsActivity.this,
                        android.R.layout.simple_spinner_item, sect);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSection.setAdapter(adapter1);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(PostNewsActivity.this);

            }
        });

        mPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String section = mSection.getSelectedItem().toString();
                String head = mHead.getEditText().getText().toString();
                String tag = mTag.getEditText().getText().toString();
                String source = mUrl.getEditText().getText().toString();
                String video = mVideo.getEditText().getText().toString();
                String intro = mIntro.getEditText().getText().toString();
                String body = mBody.getEditText().getText().toString();
                if (!TextUtils.isEmpty(video)){
                    ssVideo = video;
                }else {
                    ssVideo = ("default");
                }

                mProgress.show();


                if (!TextUtils.isEmpty(section) || !TextUtils.isEmpty(head) || !TextUtils.isEmpty(tag) || !TextUtils.isEmpty(source)  || !TextUtils.isEmpty(intro) || !TextUtils.isEmpty(body) || postImageUri != null) {





                    if(section == "World"){


                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(PostNewsActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        final StorageReference ref = storageReference.child("post_images").child(randomName + ".jpg");
                        UploadTask filePath = ref.putBytes(imageData);
                        Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    final String doeUri = downloadUri.toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_url", doeUri);
                                    postMap.put("head", head);
                                    postMap.put("tag", tag);
                                    postMap.put("source", source);
                                    postMap.put("video", ssVideo);
                                    postMap.put("section", section);
                                    postMap.put("intro", intro);
                                    postMap.put("body", body);
                                    postMap.put("user_id", current);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());



                                    firebaseFirestore.collection("World").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(PostNewsActivity.this, "News was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(PostNewsActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {


                                            }

                                            mProgress.dismiss();

                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


                    }

                    if(section == "Nigeria"){


                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(PostNewsActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        final StorageReference ref = storageReference.child("post_images").child(randomName + ".jpg");
                        UploadTask filePath = ref.putBytes(imageData);
                        Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    final String doeUri = downloadUri.toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_url", doeUri);
                                    postMap.put("head", head);
                                    postMap.put("tag", tag);
                                    postMap.put("source", source);
                                    postMap.put("video", ssVideo);
                                    postMap.put("section", section);
                                    postMap.put("intro", intro);
                                    postMap.put("body", body);
                                    postMap.put("user_id", current);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());



                                    firebaseFirestore.collection("Nigeria").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(PostNewsActivity.this, "News was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(PostNewsActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {


                                            }

                                            mProgress.dismiss();

                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


                    }
                    if(section == "Sports"){

                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(PostNewsActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();

                        final StorageReference ref = storageReference.child("post_images").child(randomName + ".jpg");
                        UploadTask filePath = ref.putBytes(imageData);
                        Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    final String doeUri = downloadUri.toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_url", doeUri);
                                    postMap.put("head", head);
                                    postMap.put("tag", tag);
                                    postMap.put("source", source);
                                    postMap.put("video", ssVideo);
                                    postMap.put("section", section);
                                    postMap.put("intro", intro);
                                    postMap.put("body", body);
                                    postMap.put("user_id", current);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());



                                    firebaseFirestore.collection("Sports").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(PostNewsActivity.this, "News was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(PostNewsActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {


                                            }

                                            mProgress.dismiss();

                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


                    }

                    if(section == "Entertainment"){

                        final String randomName = UUID.randomUUID().toString();

                        // PHOTO UPLOAD
                        File newImageFile = new File(postImageUri.getPath());
                        try {

                            compressedImageFile = new Compressor(PostNewsActivity.this)
                                    .setMaxHeight(720)
                                    .setMaxWidth(720)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageData = baos.toByteArray();


                        final StorageReference ref = storageReference.child("post_images").child(randomName + ".jpg");
                        UploadTask filePath = ref.putBytes(imageData);
                        Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    final String doeUri = downloadUri.toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", doeUri);
                                        postMap.put("head", head);
                                        postMap.put("tag", tag);
                                        postMap.put("source", source);
                                        postMap.put("video", ssVideo);
                                        postMap.put("section", section);
                                        postMap.put("intro", intro);
                                        postMap.put("body", body);
                                        postMap.put("user_id", current);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());



                                        firebaseFirestore.collection("Entertainment").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){

                                                    Toast.makeText(PostNewsActivity.this, "News was added", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(PostNewsActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                } else {


                                                }

                                                mProgress.dismiss();

                                            }
                                        });
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


                    }

                }

            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                mPicture.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
