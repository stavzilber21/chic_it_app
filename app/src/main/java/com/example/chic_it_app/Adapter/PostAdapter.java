package com.example.chic_it_app.Adapter;

import static android.graphics.Typeface.BOLD;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
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
import com.example.chic_it_app.PostActivity;
import com.example.chic_it_app.PostDetailActivity;
import com.example.chic_it_app.ProfileActivity;
import com.example.chic_it_app.SearchActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String[] sizeGender;

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
        sizeGender = new String[2];
        final Post post = mPosts.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImage);
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
                sizeGender[0]= user.getSize();
                sizeGender[1]= user.getGender();
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
//        holder.delete.setOnClickListener(new View.OnClickListener() {

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setTitle("Are you sure that you want to delete?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().deletePost(post.getPostid(), post.getPublisher(), fUser.getUid());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            // If the deletion is successful, remove the post from the list
                            mPosts.remove(post);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(mContext, "Failed to delete post!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                builder.setNegativeButton("No", (dialog, which) ->
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
                            builder.setTitle("Details: ");

                            // Change the data type to SpannableStringBuilder
                            SpannableStringBuilder formattedText = new SpannableStringBuilder();

                            TextView messageTextView = new TextView(mContext);
                            messageTextView.setTextAppearance(android.R.style.TextAppearance_Medium);

                            messageTextView.setBackgroundColor(Color.parseColor("#FFC0CB")); // Pink color
                            messageTextView.setPadding(16, 16, 16, 16); // Adjust padding as needed
                            messageTextView.setTypeface(null, Typeface.BOLD); // Set the text style to bold
                            messageTextView.setTypeface(ResourcesCompat.getFont(mContext, R.font.calibrib));

                            HashMap<String, String> resultMap = new HashMap<>();

                            // Split the items based on "},"
                            it=  it.substring(2);
                            String[] itemList = it.split("\\},");

                            for (int i = 0; i < itemList.length; i++) {
                                String item = itemList[i].trim();

                                if (item.endsWith("}")) {
                                    item = item.substring(0, item.length() - 2); // Remove the trailing "}"
                                }

                                // Match the key-value pairs using regex
                                Pattern pattern = Pattern.compile("\"(\\w+)\":\"([^\"]*)\"");
                                Matcher matcher = pattern.matcher(item);

                                while (matcher.find()) {
                                    String key = matcher.group(1);
                                    String value = matcher.group(2);

                                    if (key.equals("name")) {
                                        // Create a SpannableString to apply custom size, color, and underline to the "name" field
                                        SpannableString spannableName = new SpannableString(value + "\n");
                                        spannableName.setSpan(new AbsoluteSizeSpan(24, true), 0, spannableName.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannableName.setSpan(new ForegroundColorSpan(Color.parseColor("#1FBED6")), 0, spannableName.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        spannableName.setSpan(new UnderlineSpan(), 0, spannableName.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        formattedText.append(spannableName);
                                    } else if (key.equals("more")) {
                                        resultMap.put(key, value);
                                    } else {
                                        // For other keys, put them in their natural order (order of appearance in the input string)
                                        resultMap.put(key, value);
                                    }
                                }

                                StringBuilder sb = new StringBuilder();
                                for (String key : resultMap.keySet()) {
                                    String value = resultMap.get(key);
                                    sb.append(key).append(": ").append(value).append("\n ");
                                }
                                formattedText.append(sb);

                                if (i < itemList.length - 1) {
                                    formattedText.append("\n\n"); // Add a double line break after each item
                                }
                            }
//                            sizeAndGender(post.getPublisher());
                            String size = sizeGender[0];
                            String gender = sizeGender[1];
                            formattedText.append("\n");
                            formattedText.append("Size: " + size+ "\n");
                            formattedText.append("Gender: " + gender);

                            messageTextView.setText(formattedText, TextView.BufferType.SPANNABLE);
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
                Call<User> call = RetrofitClient.getInstance().getAPI().getUserDetails(post.getPublisher());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        user_phone[0] = user.getPhone();
                        String a = "https://wa.me/";
                        String b = "?text=Hi! I'm from chic it, I want ask you about your look.";
                        String Url = a + user_phone[0] + b;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Url));
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("Fail", t.getMessage());
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




}