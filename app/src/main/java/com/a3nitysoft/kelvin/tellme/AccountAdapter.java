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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MessageViewHolder>{


    private FirebaseAuth mAuth;
    FirebaseUser mCurrent_user;
    private String dateString, name, image;
    protected List<RevPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;

    private DocumentReference mUserData;

    public AccountAdapter(List<RevPost> blog_list) {

        this.blog_list = blog_list;


    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review ,parent, false);
        context = parent.getContext();


        return new MessageViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String revPostId = blog_list.get(position).getRev_id();

        long millisecond = blog_list.get(position).getTimestamp().getTime();
        dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        String head_data = blog_list.get(position).getHead();
        holder.setHeading(head_data);

        String body_data = blog_list.get(position).getBody();
        holder.setBody(body_data);
        String section = blog_list.get(position).getSection();
        String user = blog_list.get(position).getUsername();
        holder.setUsername(user);

        String image_url = blog_list.get(position).getImage_url();
        holder.setImage_url(image_url, context);
        String tag = blog_list.get(position).getTag();
        String source = blog_list.get(position).getSource();
        String user_id = blog_list.get(position).getUser_id();
        String second = blog_list.get(position).getTimestamp().toString();


        holder.setTime(dateString);
        mUserData = firebaseFirestore.collection("Users").document(mCurrent_user.getUid());
        mUserData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        name = task.getResult().getString("name");
                        image = task.getResult().getString("image");


                    }
                } else {

                }


            }
        });


        holder.revsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, CommentsActivity.class);
                newIntent.putExtra("rev_post_id", revPostId);
                newIntent.putExtra("name", name);
                newIntent.putExtra("image", image);
                newIntent.putExtra("rdate", second);
                newIntent.putExtra("rimage", image_url);
                newIntent.putExtra("rhead", head_data);
                newIntent.putExtra("rintro", body_data);
                newIntent.putExtra("ruser", user);
                context.startActivity(newIntent);

            }
        });




        holder.revsView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (user_id.equals(mCurrent_user.getUid())){

                    CharSequence options[] = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("delete this review");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int b) {
                            if (b == 0)
                            {
                                deleteSentMessages(position, holder);
                                blog_list.remove(position);
                                notifyDataSetChanged();

                            }
                            else if (b == 1)
                            {

                            }
                        }
                    });
                    builder.show();

                }

                return false;}
        });
        firebaseFirestore.collection("Reviews/" + revPostId + "/Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });

    }






    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView Heading,Username,Body,Date, CommCount;
        ImageView imageView;
        private CardView revsView;

        private Button CommentRevBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);

            revsView = itemView.findViewById(R.id.rev_blog_post);

            imageView = itemView.findViewById(R.id.rev_image);


        }

        public void setHeading(String newsHeading){


            Heading = itemView.findViewById(R.id.rev_heading);
            Heading.setText(newsHeading);

        }

        public void setBody(String newsBody){


            Body = itemView.findViewById(R.id.rev_body);
            Body.setText(newsBody);

        }

        public void setUsername(String newsUser) {


            Username = itemView.findViewById(R.id.rev_user);
            Username.setText(newsUser);
        }

        public void  setImage_url(String newsimage_url, Context context){


            Picasso.with(context).load(newsimage_url).placeholder(R.drawable.splashlogo).into(imageView);

        }

        public void setBlogImage(String downloadUri, Context context){

            Picasso.with(context).load(downloadUri).placeholder(R.drawable.splashlogo).into(imageView);

        }

        public void setTime(String dateString) {

            Date = itemView.findViewById(R.id.rev_date);
            Date.setText(dateString);

        }

        public void updateLikesCount(int count){


            CommCount = itemView.findViewById(R.id.CommCount);
            CommCount.setText(count + " Comments");

        }
    }

    private void deleteSentMessages(final int i, final MessageViewHolder holder) {
        firebaseFirestore.collection("Reviews").document().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occured", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



}