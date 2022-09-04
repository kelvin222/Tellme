package com.a3nitysoft.kelvin.tellme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifRecyclerAdapter extends RecyclerView.Adapter<NotifRecyclerAdapter.ViewHolder> {
    public List<Notif> notsList;
    public Context context;
    private FirebaseUser mCurrent_user;
    private FirebaseFirestore firebaseFirestore;
    private String not_Id, not_message, cuser_id;
    private String not_from;
    private String not_title;
    private String not_type;
    private String name, image;
    private FirebaseAuth mAuth;

    public NotifRecyclerAdapter(List<Notif> notsList){

        this.notsList = notsList;

    }


    @Override
    public NotifRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_list_item, parent, false);
        context = parent.getContext();


        firebaseFirestore = FirebaseFirestore.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        cuser_id = mCurrent_user.getUid();

        return new NotifRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotifRecyclerAdapter.ViewHolder holder, final int i) {

        String userId = mAuth.getCurrentUser().getUid();
        holder.setIsRecyclable(false);
        Notif c = notsList.get(i);

        not_from = c.getFrom();
        not_title = c.getTitle();
        holder.title.setText(c.getTitle());
        firebaseFirestore.collection("Users").document(not_from).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        name = task.getResult().getString("name");
                        image = task.getResult().getString("thumb");
                if (not_title.equals("New Tag")){
                    holder.notif_message.setText(c.getMessage() + " " + name);
                }else {
                    holder.notif_message.setText(name + " " + c.getMessage());
                }
                holder.setImage_url(image, context);
            }
        });











    }


    @Override
    public int getItemCount() {
        return notsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CardView not_post;

        CircleImageView imageView;

        private TextView title, notif_message;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            imageView = itemView.findViewById(R.id.not_image);
            title = itemView.findViewById(R.id.not_title);
            notif_message = itemView.findViewById(R.id.not_message);
            not_post = itemView.findViewById(R.id.not_card);
        }

        public void  setImage_url(String newsimage_url, Context context){

            if (!newsimage_url.equals("default")) {
                Picasso.with(context).load(newsimage_url).placeholder(R.drawable.splashlogo).into(imageView);

            }
        }

    }


}
