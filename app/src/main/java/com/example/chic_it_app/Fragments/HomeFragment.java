package com.example.chic_it_app.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> mPosts;
    private PostAdapter postAdapter;
    private TextView count_post;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        count_post = view.findViewById(R.id.count_post);
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , mPosts);
        recyclerView.setAdapter(postAdapter);

        count_post();
        readPosts();

        return view;
    }

    public void count_post() {
        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.144.68:3000";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    count_post.setText(String.valueOf(response));
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        volleyQueue.add(stringRequest);


    }

    private void readPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    mPosts.add(post);
                    Collections.reverse(mPosts);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}