package com.example.chic_it_app.Adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chic_it_app.Model.User;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.example.chic_it_app.ProfileActivity;
import com.example.chic_it_app.R;
import com.example.chic_it_app.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFargment;
    private FirebaseUser firebaseUser;




    // function to enter the data
    public UserAdapter(Context mContext, List<User> mUsers, boolean isFargment)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFargment = isFargment;
    }
/*to create instances of a ViewHolder , including working with the ViewHolder to set up the widgets.
Since our widgets are defined in a layout resource, we will need a LayoutInflater to accomplish this*/
  @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent , false);
        return new UserAdapter.ViewHolder(view);
    }
//to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        isFollowed(user.getId() , holder.btnFollow);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }
/*
 executes a certain functionality when a button is clicked.
 */
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().followUser(firebaseUser.getUid(),user.getId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        notifyDataSetChanged();
                        Log.d("followUser", "success");

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("followUser", t.getMessage());
                    }
                });

            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (isFargment) {
////                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();
////
////                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
////                } else {
////                    Intent intent = new Intent(mContext, MainActivity.class);
////                    intent.putExtra("publisherId", user.getId());
////                    mContext.startActivity(intent);
////                }
////            }
////        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFargment) {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();

                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, StartActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });


    }

    private void isFollowed(final String id, final Button btnFollow) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().checkFollows(firebaseUser.getUid(),id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    if(res.equals("following")) {
                        btnFollow.setText("following");

                    } else {
                        btnFollow.setText("follow");
                    }
                } catch (IOException e) {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //the data for user that connect to chic _it
        public CircleImageView imageProfile;
        public TextView username;
        public TextView fullname;
        public Button btnFollow;

        //describes an item view and metadata about its place within the RecyclerView.
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
//add the data to hashmap - save data
    private void addNotification(String userId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", userId);
        map.put("text", "started following you.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }

}