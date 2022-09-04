package com.a3nitysoft.kelvin.tellme;

import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.MessageViewHolder>{


    private FirebaseAuth mAuth;
    private String dateString;
    protected List<BlogPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserDatabase;

    public BlogPostAdapter(List<BlogPost> blog_list) {

        this.blog_list = blog_list;


    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item ,parent, false);
        context = parent.getContext();


        return new MessageViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).BlogPostId;

        String head_data = blog_list.get(position).getHead();
        holder.setHeading(head_data);

        String intro_data = blog_list.get(position).getIntro();
        holder.setIntro(intro_data);
        String body_data = blog_list.get(position).getBody();
        String section = blog_list.get(position).getSection();

        String image_url = blog_list.get(position).getImage_url();
        holder.setImage_url(image_url, context);
        String tag = blog_list.get(position).getTag();
        String source = blog_list.get(position).getSource();
        String user_id = blog_list.get(position).getUser_id();
        String video = blog_list.get(position).getVideo();


        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
             dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, NewsViewActivity.class);
                newIntent.putExtra("head", head_data);
                newIntent.putExtra("tag", tag);
                newIntent.putExtra("source", source);
                newIntent.putExtra("video", video);
                newIntent.putExtra("intro", intro_data);
                newIntent.putExtra("body", body_data);
                newIntent.putExtra("date", dateString);
                newIntent.putExtra("image", image_url);
                newIntent.putExtra("section", section);

                context.startActivity(newIntent);

            }
        });

        holder.Heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, NewsViewActivity.class);
                newIntent.putExtra("head", head_data);
                newIntent.putExtra("tag", tag);
                newIntent.putExtra("source", source);
                newIntent.putExtra("video", video);
                newIntent.putExtra("intro", intro_data);
                newIntent.putExtra("body", body_data);
                newIntent.putExtra("date", dateString);
                newIntent.putExtra("image", image_url);
                newIntent.putExtra("section", section);

                context.startActivity(newIntent);

            }
        });

        holder.Intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(context, NewsViewActivity.class);
                newIntent.putExtra("head", head_data);
                newIntent.putExtra("tag", tag);
                newIntent.putExtra("source", source);
                newIntent.putExtra("video", video);
                newIntent.putExtra("intro", intro_data);
                newIntent.putExtra("body", body_data);
                newIntent.putExtra("date", dateString);
                newIntent.putExtra("image", image_url);
                newIntent.putExtra("section", section);

                context.startActivity(newIntent);

            }
        });

        holder.blogRevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsIntent = new Intent(context, PostReviewActivity.class);
                newsIntent.putExtra("head", head_data);
                newsIntent.putExtra("tag", tag);
                newsIntent.putExtra("section", section);
                newsIntent.putExtra("pic", image_url);

                context.startActivity(newsIntent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView Heading,Intro,Body,Date;
        ImageView imageView;
        private CardView newsView;

        private Button blogRevBtn;

        public MessageViewHolder(View itemView) {
            super(itemView);

            newsView = itemView.findViewById(R.id.main_blog_post);
            blogRevBtn = itemView.findViewById(R.id.revew);

            imageView = itemView.findViewById(R.id.blog_user_image);


        }

        public void setHeading(String newsHeading){


            Heading = itemView.findViewById(R.id.blog_heading);
            Heading.setText(newsHeading);

        }

        public void setIntro(String newsIntro) {


            Intro = itemView.findViewById(R.id.noti_body);
            Intro.setText(newsIntro);
        }

        public void  setImage_url(String newsimage_url, Context context){

            if(!newsimage_url.equals("default")) {

                //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                Picasso.with(context).load(newsimage_url).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.splashlogo).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(context).load(newsimage_url).placeholder(R.drawable.splashlogo).into(imageView);

                    }
                });

            }

        }

        public void setBlogImage(String downloadUri, Context context){

            Picasso.with(context).load(downloadUri).placeholder(R.drawable.splashlogo).into(imageView);

        }

        public void setTime(String dateString) {

            Date = itemView.findViewById(R.id.noti_date);
            Date.setText(dateString);

        }
    }






}