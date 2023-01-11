package com.example.chic_it_app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chic_it_app.Adapter.PhotoAdapter;
import com.example.chic_it_app.Model.ProfileModel;
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

public class ProfileFragment extends Fragment {
    ProfileModel model = new ProfileModel(this);

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
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewLikes = view.findViewById(R.id.recucler_view_saved);
        recyclerViewLikes.setHasFixedSize(true);
        recyclerViewLikes.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myLikedPosts = new ArrayList<>();
        postAdapterLikes = new PhotoAdapter(getContext(), myLikedPosts);
        recyclerViewLikes.setAdapter(postAdapterLikes);

        model.userInfo(profileId,fullname,username,imageProfile);
        model.getPostCount(profileId,posts);
        model.myPhotos(myPhotoList,profileId,photoAdapter);
        model.getSavedPosts(fUser,myLikedPosts,postAdapterLikes);

        if (profileId.equals(fUser.getUid())) {
            editProfile.setText("Edit profile");
        } else {
//            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if (btnText.equals("Edit profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));}
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




//    private void checkFollowingStatus() {
//
//        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(profileId).exists()) {
//                    editProfile.setText("following");
//                } else {
//                    editProfile.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }




}