package com.a3nitysoft.kelvin.tellme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class NotifFragment extends Fragment {
    private RecyclerView mNotList;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private List<Notif> not_list;

    private FirebaseFirestore firebaseFirestore;
    private NotifRecyclerAdapter notifRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private DatabaseReference mTaggedBase;
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

    public NotifFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_notif, container, false);


        not_list = new ArrayList<>();
        mNotList = (RecyclerView) mMainView.findViewById(R.id.not_list_view);


        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        notifRecyclerAdapter = new NotifRecyclerAdapter(not_list);
        mNotList.setLayoutManager(new LinearLayoutManager(getContext()));
        mNotList.setAdapter(notifRecyclerAdapter);
        mNotList.setHasFixedSize(true);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            mNotList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        loadMorePost();

                    }

                }
            });



            Query firstQuery = firebaseFirestore.collection("Users/" + user_id + "/notification").orderBy("timestamp", Query.Direction.DESCENDING).limit(7);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            not_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String notifId = doc.getDocument().getId();
                                Notif notif = doc.getDocument().toObject(Notif.class).withId(notifId);

                                if (isFirstPageFirstLoad) {

                                    not_list.add(notif);

                                } else {

                                    not_list.add(0, notif);

                                }


                                notifRecyclerAdapter.notifyDataSetChanged();

                            }


                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });
        }



        // Inflate the layout for this fragment
        return mMainView;
    }
    public void loadMorePost(){


        Query nextQuery = firebaseFirestore.collection("Users/" + user_id + "/notification").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(5);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String notifId = doc.getDocument().getId();
                            Notif notif = doc.getDocument().toObject(Notif.class).withId(notifId);
                            not_list.add(notif);

                            notifRecyclerAdapter.notifyDataSetChanged();
                        }

                    }
                }

            }
        });



    }


}
