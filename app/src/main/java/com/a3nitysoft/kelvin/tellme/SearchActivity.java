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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private View mView;

    private TextInputEditText mSearchField;
    private ImageView mSearchBtn;
    private RecyclerView mResultList;


    private FirestoreRecyclerAdapter<Search, UsersViewHolder> adapter;
    private FirebaseFirestore firebaseFirestore;
    private String searchText;
    private String user_id;

    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.search_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Search By Username");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");


        mSearchField = (TextInputEditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageView) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mSF = mSearchField.getText().toString();
                searchText = mSF.toLowerCase();
                firebaseUserSearch(searchText);

            }
        });
    }


    private void firebaseUserSearch(String msearch) {
        Query firebaseSearchQuery = firebaseFirestore.collection("Users")
                .orderBy("search")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .limit(7);

        FirestoreRecyclerOptions<Search> options = new FirestoreRecyclerOptions.Builder<Search>()
                .setQuery(firebaseSearchQuery, Search.class).build();

        adapter = new FirestoreRecyclerAdapter<Search, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Search model) {
                final String list_user_id = model.getUid();
                user_id = list_user_id;
                holder.userName.setText(model.getName());
                holder.userAbout.setText(model.getAbout());
                holder.setImage(getApplicationContext(),model.getThumb());

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup,false);
                        UsersViewHolder viewHolder = new UsersViewHolder(view);
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent profileIntent = new Intent(SearchActivity.this, ProfileActivity.class);

                                profileIntent.putExtra("user_id", user_id);
                                startActivity(profileIntent);
                            }
                        });
                        return viewHolder;
                    }
                };
        mResultList.setAdapter(adapter);
        adapter.startListening();

    }


    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userAbout;
        CircleImageView profileImage;


        public UsersViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.name_text);
            userAbout = itemView.findViewById(R.id.status_text);
            profileImage = itemView.findViewById(R.id.profile_image);


        }

        public void setImage(final Context ctx, final String userImage){

            final ImageView user_image = itemView.findViewById(R.id.profile_image);


            if(!user_image.equals("default")) {

                //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                Picasso.with(ctx).load(userImage).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.dp).into(user_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ctx).load(userImage).placeholder(R.drawable.dp).into(user_image);

                    }
                });

            }


        }




    }
}
