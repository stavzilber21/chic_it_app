package com.example.chic_it_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.api.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private String postId;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postId = getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postid", "none");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        //send post details
        Call<Post> call = RetrofitClient.getInstance().getAPI().getPostDetails(postId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                postList.clear();
                Post post = response.body();
                postList.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }
}
