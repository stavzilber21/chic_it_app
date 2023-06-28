package com.example.chic_it_app.Adapter;

import static android.graphics.Typeface.BOLD;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chic_it_app.Model.User;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.example.chic_it_app.PostDetailActivity;
import com.example.chic_it_app.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    /*This class coordinates between the data in Firebase and the display,
    so that for each post on the home page all its details will appear.
    In this class, all the functions related to various options to be done through the post,
    delete it, contact the creator are implemented.*/
    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setFilter(List<Post> postList) {
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
//        holder.description.setText(post.getDescription());
//        holder.price.setText(post.getPrice());
//        holder.store.setText(post.getStore());
        holder.type.setText(post.getType());

        //to display the username of publisher and his image profile
        Call<User> call = RetrofitClient.getInstance().getAPI().getUserDetails(post.getPublisher());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user.getImageurl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
                }
                holder.username.setText(user.getUsername());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

        isSaved(firebaseUser.getUid(),post.getPostid(), holder.save);

        //If you liked this post it is added to the items you liked.
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().savePost(firebaseUser.getUid(), post.getPostid());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        notifyDataSetChanged();
                        Log.d("savePost", "success");

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("savePost", t.getMessage());
                    }
                });
            }
        });

        /* if you click on the delete icon we check if it is your post, If it's a post you uploaded, we make sure the user really
         wants to delete the post, and if so, we remove it from Firebase*/
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("you sure that you want to delete?");
                builder.setMessage("");
                builder.setPositiveButton("yes", (dialog, which) -> {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().deletePost(post.getPostid(),post.getPublisher(),fUser.getUid());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                });

                builder.setNegativeButton("no", (dialog, which) ->
                {
                    Toast.makeText(mContext, "The post was not deleted!", Toast.LENGTH_SHORT).show();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

//        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
//                        .edit().putString("profileId", post.getPublisher()).apply();
//
//                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });

        /*If a user clicks on the button imageProfile we display the profile picture*/
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                mContext.startActivity(intent);
            }
        });


        /*If a user clicks on the button username we display the profile of creator of post*/
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                mContext.startActivity(intent);
            }
        });


        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getPostItems(post.getPostid());
                call.enqueue(new Callback<ResponseBody>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody itemsList = response.body();
                        String it = null;
                        try {
                            it = itemsList.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (it != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Items");
                            TextView messageTextView = new TextView(mContext);
                            messageTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
//                            messageTextView.setTextColor(Color.BLUE); // Set the text color to blue
                            messageTextView.setBackgroundColor(Color.parseColor("#FFC0CB")); // Pink color
                            messageTextView.setPadding(16, 16, 16, 16); // Adjust padding as needed
                            messageTextView.setTypeface(null, Typeface.BOLD); // Set the text style to bold
                            messageTextView.setTypeface(ResourcesCompat.getFont(mContext, R.font.calibrib));

                            // Split the items based on "},"
                            String[] itemList = it.split("\\},");
                            StringBuilder formattedText = new StringBuilder();
                            for (int i = 0; i < itemList.length; i++) {
                                String item = itemList[i].trim();
                                if (item.endsWith("}")) {
                                    item = item.substring(0, item.length() - 1); // Remove the trailing "}"
                                }
                                formattedText.append(formatItem(item));
                                if (i < itemList.length - 1) {
                                    formattedText.append("\n\n"); // Add a double line break after each item
                                }
                            }

                            messageTextView.setText(formattedText.toString());
                            builder.setView(messageTextView);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Handle the failure case
                        // ...
                    }
                });
            }
        });




//        holder.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
//                        .edit().putString("profileId", post.getPublisher()).apply();
//
//                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });

        /*If a user clicks on the button postImage It goes to this post's page only. */
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();

                Intent intent = new Intent(mContext, PostDetailActivity.class);
                mContext.startActivity(intent);
            }
        });


        /* If a user clicks on the button contact_us we send it to that seller's WhatsApp*/
        holder.contact_us.setOnClickListener(new View.OnClickListener() {
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
                        String Url = a + user_phone[0] + b;
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
//        TextView description;
//        TextView price;
//        TextView store;
        TextView type;
        ImageView item;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            //take all the variable from the xml
            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            delete = itemView.findViewById(R.id.delete);
            username = itemView.findViewById(R.id.username);
            contact_us = itemView.findViewById(R.id.contact_us);
//            description = itemView.findViewById(R.id.description);
//            store = itemView.findViewById(R.id.store);
//            price = itemView.findViewById(R.id.price);
            type = itemView.findViewById(R.id.type);
            item = itemView.findViewById(R.id.item);

        }
    }

    /*when you click on save icon we check if the picture is alredy saved, if it wasn't save before we change the
        icon to be full and add it to the List of saved posts, if it is not the first time we change the icon and we remov
        it from the List of saved posts*/
    private void isSaved( String uid,  final  String postId,  final ImageView image) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().check(uid,postId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    if(res.equals("saved")) {
                        image.setImageResource(R.drawable.ic_liked);
                        image.setTag("saved");
                    } else {
                        image.setImageResource(R.drawable.ic_like);
                        image.setTag("save");
                    }
                } catch (IOException e) {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
    private String formatItem(String item) {
        String[] itemDetails = item.split(",");
        StringBuilder formattedItem = new StringBuilder();
        for (String detail : itemDetails) {
            String[] detailPair = detail.split(":");
            if (detailPair.length == 2) {
                String key = detailPair[0].trim();
                String value = detailPair[1].trim();
                if (key.equalsIgnoreCase("name")) {
                    formattedItem.append("Name: ").append(value).append("\n");
                } else if (key.equalsIgnoreCase("store")) {
                    formattedItem.append("Store: ").append(value).append("\n");
                } else if (key.equalsIgnoreCase("price")) {
                    formattedItem.append("Price: ").append(value).append("\n");
                } else {
                    formattedItem.append(detail).append("\n");
                }
            } else {
                formattedItem.append(detail).append("\n");
            }
        }
        return formattedItem.toString();
    }

    private String formatDetail(String detail) {
        detail = detail.replaceAll("\\{", " ").replaceAll("\\d", " ");
        return detail;
    }

//    private String getItem(String postId){
//        Call<String> call = RetrofitClient.getInstance().getAPI().getPostItems(postId);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
////                ResponseBody itemsList = response.body();
//                String it = response.body();
//                if (it != null) {
//                    return it;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                // Handle the failure case
//                // ...
//            }
//        });
//    }
}