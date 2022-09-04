package com.a3nitysoft.kelvin.tellme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaggedActivity extends AppCompatActivity {
    private List<Follow> follow_list;

    private FirebaseFirestore firebaseFirestore;
    private FollowAdapter followAdapter;

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

    private View mMainView;


    private LinearLayoutManager mLinearLayout;
    private RecyclerView mTagList;

    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagged);
        firebaseFirestore = FirebaseFirestore.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.tag_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Tagged User List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        follow_list = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        mTagList = (RecyclerView) findViewById(R.id.tag_list);
        followAdapter = new FollowAdapter(follow_list);
        mLinearLayout = new LinearLayoutManager(this);
        mTagList.setHasFixedSize(true);
        mTagList.setLayoutManager(mLinearLayout);
        mTagList.setAdapter(followAdapter);


        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mTagList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if(reachedBottom){

                    loadMorePost();

                }

            }
        });
        Query firebaseSearchQuery = firebaseFirestore.collection("Users/" + user_id + "/tagged").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);

        firebaseSearchQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoad) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        follow_list.clear();

                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String fId = doc.getDocument().getId();
                            Follow follow = doc.getDocument().toObject(Follow.class);

                            if (isFirstPageFirstLoad) {

                                follow_list.add(follow);

                            } else {

                                follow_list.add(0, follow);

                            }


                            followAdapter.notifyDataSetChanged();

                        }


                    }

                    isFirstPageFirstLoad = false;

                }

            }

        });







    }

    public void loadMorePost(){


        Query nextQuery = firebaseFirestore.collection("Users/" + user_id + "/tagged")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(7);

        nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String fId = doc.getDocument().getId();
                            Follow follow = doc.getDocument().toObject(Follow.class);
                            follow_list.add(follow);

                            followAdapter.notifyDataSetChanged();
                        }

                    }
                }

            }
        });



    }

}