package com.a3nitysoft.kelvin.tellme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FeedActivity extends AppCompatActivity {
    private List<Feed> feed_list;
    private RecyclerView mFeedList;
    private FirebaseAuth firebaseAuth;
    private FeedAdapter feedAdapter;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar mainToolbar;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        feed_list = new ArrayList<>();
        mFeedList = (RecyclerView) findViewById(R.id.feedRcy);
        mainToolbar = (Toolbar) findViewById(R.id.feed_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Admin Feedbacks");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        feedAdapter = new FeedAdapter(feed_list);
        mFeedList.setLayoutManager(new LinearLayoutManager(this));
        mFeedList.setAdapter(feedAdapter);
        mFeedList.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            Query firstQuery = firebaseFirestore.collection("Feedback").limit(25);
            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            feed_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String feedId = doc.getDocument().getId();
                                Feed feed = doc.getDocument().toObject(Feed.class).withId(feedId);

                                if (isFirstPageFirstLoad) {

                                    feed_list.add(feed);

                                } else {

                                    feed_list.add(0, feed);

                                }


                                feedAdapter.notifyDataSetChanged();

                            }


                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });
        }


    }
}
