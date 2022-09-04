package com.a3nitysoft.kelvin.tellme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private TextView comment_rev;
    private TextView comment_user;

    private TextView mHead;
    private TextView mDate;
    private ImageView mImage;
    private AdView mAdView;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private EditText comment_field;
    private ImageView comment_post_btn;
    private LinearLayoutManager mLinearLayout;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private SwipeRefreshLayout mRefreshLayout;

    private String saveCurrentTime, saveCurrentDate;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private DocumentReference mUserDatabase;
    private DocumentReference mComDatabase;
    private String rev_post_id, rdate,rimage, rhead,rintro,ruser,ruserId;
    private String body;
    private String user;
    private String current_user_id;
    private String head;
    private String date;
    private String dimage,name,image;

    private static final String TAG = "CommentsActivity";

    ;

    private static final int TOTAL_ITEMS_TO_LOAD = 5;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;


    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        MobileAds.initialize(this, "ca-app-pub-4481331290406181~1599005010");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        rev_post_id = getIntent().getStringExtra("rev_post_id");
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");
        rdate = getIntent().getStringExtra("rdate");
        rimage = getIntent().getStringExtra("rimage");
        rhead = getIntent().getStringExtra("rhead");
        rintro = getIntent().getStringExtra("rintro");
        ruser = getIntent().getStringExtra("ruser");
        ruserId = getIntent().getStringExtra("ruserId");


        mHead = (TextView) findViewById(R.id.posthead);
        mDate = (TextView) findViewById(R.id.postdate);
        mImage = (ImageView) findViewById(R.id.postimg);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);
        comment_rev = findViewById(R.id.comment_label);
        comment_user = findViewById(R.id.commuser);


        Picasso.with(CommentsActivity.this).load(rimage).placeholder(R.drawable.splashlogo).into(mImage);
        comment_rev.setText(rintro);
        comment_user.setText(ruser);
        mHead.setText(rhead);
        mDate.setText(rdate);
        comment_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(CommentsActivity.this, ProfilerActivity.class);
                startIntent.putExtra("user_id", ruserId);
                startActivity(startIntent);
            }
        });

        getSupportActionBar().setTitle(ruser + "'s" + " review");

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(this);
        comment_list.setLayoutManager(mLinearLayout);
        comment_list.setAdapter(commentsRecyclerAdapter);



        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        iniComment();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMorePost();


            }
        });


        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                String current_com_ref = "comments/" + rev_post_id ;
                String comment_message = comment_field.getText().toString();
                if (!TextUtils.isEmpty(comment_message)){

                    comment_post_btn.setVisibility(View.INVISIBLE);
                    DatabaseReference user_message_push = mRootRef.child("comments")
                            .child(rev_post_id).push();
                    String messagePushID = user_message_push.getKey();
                    String comm_id = user_message_push.getKey();

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message);
                    commentsMap.put("name", name);
                    commentsMap.put("image", image);
                    commentsMap.put("user_id", current_user_id);
                    commentsMap.put("review_id", rev_post_id);
                    commentsMap.put("comm_id", comm_id);
                    commentsMap.put("mtime", saveCurrentTime);
                    commentsMap.put("mdate", saveCurrentDate);
                    commentsMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_com_ref + "/" + comm_id, commentsMap);
                    comment_field.setText("");
                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }
                            comment_post_btn.setVisibility(View.VISIBLE);
                        }
                    });

                }

            }
        });
    }


    private void iniComment(){
        DatabaseReference messageRef = mRootRef.child("comments").child(rev_post_id);
        messageRef.keepSynced(true);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        messageQuery.keepSynced(true);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Comments comments = dataSnapshot.getValue(Comments.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                commentsList.add(comments);
                commentsRecyclerAdapter.notifyDataSetChanged();

                comment_list.scrollToPosition(commentsList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void loadMorePost(){
        DatabaseReference messageRef = mRootRef.child("comments").child(rev_post_id);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(5);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Comments message = dataSnapshot.getValue(Comments.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    commentsList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                commentsRecyclerAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(CommentsActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
