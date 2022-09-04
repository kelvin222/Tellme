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

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MessageViewHolder>{
    private FirebaseAuth mAuth;
    FirebaseUser mCurrent_user;
    private String dateString, name, email;
    protected List<Feed> feed_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;

    private DocumentReference mUserData;

    public FeedAdapter(List<Feed> feed_list) {

        this.feed_list = feed_list;


    }
    @Override
    public FeedAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedlist ,parent, false);
        context = parent.getContext();


        return new FeedAdapter.MessageViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final FeedAdapter.MessageViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String FeedId = feed_list.get(position).getFeed_id();

        long millisecond = feed_list.get(position).getTimestamp().getTime();
        dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        String Feed_name = feed_list.get(position).getName();
        holder.setName(Feed_name);

        String Feed_mail = feed_list.get(position).getEmail();
        holder.setUserEmail(Feed_mail);
        String Feed_message = feed_list.get(position).getMessage();
        String second = feed_list.get(position).getTimestamp().toString();


        holder.setFeedtime(dateString);

        holder.feeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, FeedviewActivity.class);
                newIntent.putExtra("feed_id", FeedId);
                newIntent.putExtra("name", Feed_name);
                newIntent.putExtra("fdate", second);
                newIntent.putExtra("mail", Feed_mail);
                newIntent.putExtra("message", Feed_message);
                context.startActivity(newIntent);

            }
        });




        holder.feeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {



                    CharSequence options[] = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("delete this Feedback");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int b) {
                            if (b == 0)
                            {
                                deleteFeedMessages(position, holder);
                                feed_list.remove(position);
                                notifyDataSetChanged();

                            }
                            else if (b == 1)
                            {

                            }
                        }
                    });
                    builder.show();



                return false;}
        });

    }
    @Override
    public int getItemCount() {
        return feed_list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView UserName,UserEmail,Feedtime;
        private CardView feeView;

        private Button CommentRevBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);

            feeView = itemView.findViewById(R.id.fconf);

        }

        public void setName(String feedname){


            UserName = itemView.findViewById(R.id.fnamef);
            UserName.setText(feedname);

        }

        public void setUserEmail(String feedmail){


            UserEmail = itemView.findViewById(R.id.femailf);
            UserEmail.setText(feedmail);

        }

        public void setFeedtime(String feedtime) {


            Feedtime = itemView.findViewById(R.id.ftimef);
            Feedtime.setText(feedtime);
        }





    }

    private void deleteFeedMessages(final int i, final FeedAdapter.MessageViewHolder holder) {
        firebaseFirestore.collection("Feedback").document().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
