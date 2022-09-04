package com.a3nitysoft.kelvin.tellme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseUser mCurrent_user;
    private FirebaseFirestore firebaseFirestore;
    private String comPostId, cuser_id;
    private String commentUser;
    private String commentRid;
    private String comm_id;
    private FirebaseAuth mAuth;

    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();


        firebaseFirestore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        cuser_id = mCurrent_user.getUid();

        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, final int i) {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        holder.setIsRecyclable(false);
        Comments c = commentsList.get(i);

        String from_user = c.getUser_id();
        holder.comment_message.setText(c.getMessage());
        holder.mtime.setText(c.getMtime());
        holder.mdate.setText(c.getMdate());
        holder.User_name.setText(c.getName());
        commentRid = c.getReview_id();
        comm_id = c.getComm_id();
        String img = c.getImage();
        holder.setImage_url(img, context);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]
                        {
                                "View Profile",
                                "No"
                        };
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Are You Sure");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int b) {
                        if (b == 0) {
                            Intent startIntent = new Intent(context, ProfilerActivity.class);
                            startIntent.putExtra("user_id", from_user);

                            context.startActivity(startIntent);

                        } else if (b == 1) {

                        }
                    }
                });
                builder.show();
            }
        });
        if (from_user.equals(messageSenderId)){
            holder.rev_com_post.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {  CharSequence options[] = new CharSequence[]
                        {
                                "Delete",
                                "Cancel"
                        };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int b) {
                            if (b == 0)
                            {
                                deleteSentMessages(i, holder);
                                commentsList.remove(i);
                                notifyDataSetChanged();
                            }
                            else if (b == 1)
                            {
                            }

                        }
                    });
                    builder.show();
                    return false;
                }
            });
        }else {
        }






}


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CardView rev_com_post;

        CircleImageView imageView;

        private TextView  User_name, comment_message, mdate, mtime;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            imageView = itemView.findViewById(R.id.comment_image);
            mtime = itemView.findViewById(R.id.comm_time);
            mdate = itemView.findViewById(R.id.comm_date);
            User_name = mView.findViewById(R.id.comment_username);
            comment_message = mView.findViewById(R.id.comment_message);
            rev_com_post = itemView.findViewById(R.id.rev_com_post);
        }

        public void  setImage_url(String newsimage_url, Context context){


            if (!newsimage_url.equals("default")) {
                Picasso.with(context).load(newsimage_url).placeholder(R.drawable.splashlogo).into(imageView);

            }
        }

    }


    private void deleteSentMessages(final int i, final ViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("comments")
                .child(commentsList.get(i).getReview_id())
                .child(commentsList.get(i).getComm_id())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occured",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
