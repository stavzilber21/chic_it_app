package com.example.chic_it_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chic_it_app.Adapter.PhotoAdapter;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLikes;
    private PhotoAdapter postAdapterLikes;
    private List<Post> myLikedPosts;
    private TextView followers;
    private TextView following;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private List<Post> myPhotoList;

    private CircleImageView imageProfile;
    private TextView posts;
    private TextView fullname;
    private TextView username;

    private ImageView myPictures;
    private ImageView savedPictures;

    private Button editProfile;

    private FirebaseUser fUser;

    String profileId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            profileId = fUser.getUid();
        } else {
            profileId = data;
            getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        imageProfile = findViewById(R.id.image_profile);
        posts = findViewById(R.id.posts);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        myPictures = findViewById(R.id.my_pictures);
        savedPictures = findViewById(R.id.saved_pictures);
        editProfile = findViewById(R.id.edit_profile);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        recyclerView = findViewById(R.id.recucler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewLikes = findViewById(R.id.recucler_view_saved);
        recyclerViewLikes.setHasFixedSize(true);
        recyclerViewLikes.setLayoutManager(new GridLayoutManager(this, 3));
        myLikedPosts = new ArrayList<>();
        postAdapterLikes = new PhotoAdapter(this, myLikedPosts);
        recyclerViewLikes.setAdapter(postAdapterLikes);

        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        getSavedPosts();

        if (profileId.equals(fUser.getUid())) {
            editProfile.setText("Edit profile");
        } else {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if (btnText.equals("Edit profile")) {
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                }
            }
        });
        followers.setText("3");
        following.setText("2");
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewLikes.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewLikes.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewLikes.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followings");
                startActivity(intent);
            }
        });
    }

    private void getSavedPosts() {
        myLikedPosts.clear();
        Call<List<Post>> call = RetrofitClient.getInstance().getAPI().mySavedPosts(fUser.getUid());
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postsList = response.body();
                if (postsList != null) {
                    myLikedPosts.addAll(postsList);
                    Collections.reverse(myLikedPosts);
                    postAdapterLikes.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }

    private void myPhotos() {
        myPhotoList.clear();
        Call<List<Post>> call = RetrofitClient.getInstance().getAPI().getMyPhoto(fUser.getUid());
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postsList = response.body();
                if (postsList != null) {
                    myPhotoList.addAll(postsList);
                    Collections.reverse(myPhotoList);
                    photoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

    }

    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPostCount() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Call<Integer> call = RetrofitClient.getInstance().getAPI().countPost(fUser.getUid());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int counter = response.body();
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });


    }

    private void userInfo() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Call<User> call = RetrofitClient.getInstance().getAPI().getUserDetails(fUser.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                Picasso.get().load( user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }
    private void getFollowersAndFollowingCount() {
        System.out.println("stav zilber");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getFollowersAndFollowingCount(fUser.getUid());
        System.out.println(fUser.getUid());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
                    String countsResponse = response.body().toString();
//                    System.out.println("hodaya: "+ countsResponse);
//                    if (countsResponse != null) {
                    String[] counts = countsResponse.split(", ");
                    if (counts.length == 2) {
                        String followersCount = counts[0].substring(counts[0].indexOf(":") + 1).trim();
                        String followingCount = counts[1].substring(counts[1].indexOf(":") + 1).trim();
                        System.out.println("stav: -----------------------------------------------"+followersCount);

                        followers.setText(followersCount);
                        following.setText(followingCount);
//                        }
//                    }
//                } else {
//                    Log.d("Fail", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }



}