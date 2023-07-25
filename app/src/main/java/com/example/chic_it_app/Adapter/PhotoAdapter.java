package com.example.chic_it_app.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.PostDetailActivity;
import com.example.chic_it_app.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>
{

    private Context mContext; // the post
    private List<Post> mPosts; // list of all the posts

    public PhotoAdapter(Context mContext, List<Post> mPosts) //post and list of posts
    {
        this.mContext = mContext; // enter the data
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return  new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();

                Intent intent = new Intent(mContext, PostDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
        }
    }

}