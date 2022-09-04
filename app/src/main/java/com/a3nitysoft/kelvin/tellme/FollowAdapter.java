package com.a3nitysoft.kelvin.tellme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MessageViewHolder>{


    private FirebaseAuth mAuth;
    FirebaseUser mCurrent_user;
    private String dateString, name, image, about;
    private String user_id, foluser_id;
    protected List<Follow> follow_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;

    private DocumentReference mUserData;

    public FollowAdapter(List<Follow> follow_list) {

        this.follow_list = follow_list;


    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follow_list ,parent, false);
        context = parent.getContext();


        return new MessageViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String folowId = follow_list.get(position).getUid();

        foluser_id = folowId;

        firebaseFirestore.collection("Users").document(foluser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name = task.getResult().getString("name");
                about = task.getResult().getString("about");
                image = task.getResult().getString("thumb");

                holder.setAbout(about);
                holder.setUsername(name);
                holder.setImage_url(image, context);
            }
        });
        holder.fView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, ProfileActivity.class);
                newIntent.putExtra("user_id", foluser_id);
                context.startActivity(newIntent);

            }
        });


    }






    @Override
    public int getItemCount() {
        return follow_list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView About, Username,Image;
        ImageView imageView;
        private CardView fView;

        private Button CommentRevBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);

            fView = itemView.findViewById(R.id.fcard4);

            imageView = itemView.findViewById(R.id.fcircleImageView);


        }

        public void setAbout(String about){


            About = itemView.findViewById(R.id.f_about);
            About.setText(about);

        }

        public void setUsername(String name){


            Username = itemView.findViewById(R.id.f_name);
            Username.setText(name);

        }

        public void  setImage_url(String fimage_url, Context context){


            if (!fimage_url.equals("default")) {
                Picasso.with(context).load(fimage_url).placeholder(R.drawable.splashlogo).into(imageView);

            }


        }

    }





}