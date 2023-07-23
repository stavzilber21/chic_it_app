package com.example.chic_it_app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chic_it_app.Adapter.UserAdapter;
import com.example.chic_it_app.Model.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersActivity extends AppCompatActivity {

    private  String id;
    private String title;
    private List<String> idList;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        switch (title) {
            case "followers" :
                getFollowers();
                break;

            case "followings":
                getFollowings();
                break;

            case "likes":
                getLikes();
                break;
        }
    }

//    private void getFollowers() {
//
//        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                idList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    idList.add((snapshot.getKey()));
//                }
//
//                showUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
    private void getFollowers() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getFollowers(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                idList.clear();
                if (response.isSuccessful()) {
                    String ans = response.body().byteStream().toString();
                    int start = ans.indexOf("=");
                    int end = ans.indexOf("]");
                    String[] followersList = ans.substring(start+1,end).split(",");
                    //String[] followersList = ans.split(",");
                    if (followersList != null) {
                        for(String str: followersList){
                            idList.add(str);
                        }

                        showUsers();
                    }
                } else {
                    Log.d("Fail", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }


    //    private void getFollowings() {
//
//        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                idList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    idList.add((snapshot.getKey()));
//                }
//
//                showUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
    private void getFollowings() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getFollowings(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                idList.clear();
                if (response.isSuccessful()) {
                    String ans = response.body().byteStream().toString();
                    int start = ans.indexOf("=");
                    int end = ans.indexOf("]");
                    String[] followingList = ans.substring(start+1,end).split(",");
                    if (followingList != null) {
                        for(String str: followingList){
                            idList.add(str);
                        }

                    showUsers();
                    }
                } else {
                    Log.d("Fail", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }


    private void getLikes() {

        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add((snapshot.getKey()));
                }

                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void showUsers() {
        Call<List<User>> call = RetrofitClient.getInstance().getAPI().homeUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mUsers.clear();
                List<User> usersList = response.body();
                if (usersList != null) {
                    for(User user : usersList){
                        if (idList.contains(user.getId())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure
            }
        });
    }

}