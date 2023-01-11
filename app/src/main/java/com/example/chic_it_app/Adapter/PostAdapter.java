package com.example.chic_it_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chic_it_app.CreatingcontentActivity;
import com.example.chic_it_app.Fragments.HomeFragment;
import com.example.chic_it_app.Fragments.PostDetailFragment;
import com.example.chic_it_app.LoginActivity;
import com.example.chic_it_app.MainActivity;
import com.example.chic_it_app.Model.User;
import com.example.chic_it_app.PostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.chic_it_app.Fragments.ProfileFragment;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setFilter(List<Post> postList){
        this.mPosts = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImage);
        holder.description.setText(post.getDescription());
        holder.price.setText(post.getPrice());
        holder.store.setText(post.getStore());
        holder.type.setText(post.getType());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
                }
                holder.username.setText(user.getUsername());
//                holder.author.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        isSaved(post.getPostid(), holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(post.getPostid()).child(post.getPublisher()).getKey().equals(fUser.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(true);
                    builder.setTitle("you sure that you want to delete?");
                    builder.setMessage("");
                    builder.setPositiveButton("yes", (dialog, which) ->
                    {
                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                .child(post.getPostid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "deleted!", Toast.LENGTH_SHORT).show();
                                            HomeFragment homeFragment = new HomeFragment();
                                            homeFragment.count_post();
                                        }

                                    }

                                });
                    });

                    builder.setNegativeButton("no", (dialog, which) ->
                    {
                        Toast.makeText(mContext, "The post was not deleted!", Toast.LENGTH_SHORT).show();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(mContext, "YOU CANT!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.contact_us.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String[] user_phone = new String[1];
                FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        user_phone[0] = user.getPhone();

                        String a = "https://wa.me/";
                        String b = "?text=Hi! I'm from chic it, I want ask you about your look " + post.getDescription();
                        String Url = a+user_phone[0]+b;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Url));
                        mContext.startActivity(intent);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public ImageView delete;
        public ImageView postImage;
        public ImageView save;
        public TextView username;
        public TextView contact_us;
        public TextView author;
        TextView description;
        TextView price;
        TextView store;
        TextView type;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            delete = itemView.findViewById(R.id.delete);
            username = itemView.findViewById(R.id.username);
            contact_us = itemView.findViewById(R.id.contact_us);
            description = itemView.findViewById(R.id.description);
            store = itemView.findViewById(R.id.store);
            price = itemView.findViewById(R.id.price);
            type = itemView.findViewById(R.id.type);

        }
    }

    private void isSaved (final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_liked);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_like);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}