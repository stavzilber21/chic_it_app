package com.example.chic_it_app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chic_it_app.Adapter.PhotoAdapter;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chic_it_app.EditProfileActivity;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.User;
import com.example.chic_it_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewLikes;
    private PhotoAdapter postAdapterLikes;
    private List<Post> myLikedPosts;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if (data.equals("none")) {
            profileId = fUser.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        imageProfile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);
        fullname = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recucler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        //photoAdapter = new PostAdapter(getContext(), myPhotoList);
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewLikes = view.findViewById(R.id.recucler_view_saved);
        recyclerViewLikes.setHasFixedSize(true);
        recyclerViewLikes.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myLikedPosts = new ArrayList<>();
        postAdapterLikes = new PhotoAdapter(getContext(), myLikedPosts);
        recyclerViewLikes.setAdapter(postAdapterLikes);

        userInfo();
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
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            }
        });


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
        return view;
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
        //photoAdapter.notifyDataSetChanged();

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
        //firebaseAuth = FirebaseAuth.getInstance();
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

//        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Post post = snapshot.getValue(Post.class);
//                    String u = post.getPublisher();
//                    if (post.getPublisher().equals(profileId)) counter ++;
//                }
//
//                posts.setText(String.valueOf(counter));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void userInfo() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebaseAuth = FirebaseAuth.getInstance();
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
}