package com.example.chic_it_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Fragments.CustomDialogFragment;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CustomDialogFragment.DialogListener {
    ImageView close;
    ImageView imageAdded;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String imageUrl;
    private List<Post> mPosts;
    TextView post;
    Spinner type;
    private List<String> idList;
    String[] types={"inspiration","For rent - used","For rent - new","For sale - new","For sale - used"};
    String choose;
    private HashMap<String, HashMap<String, String>> items;
    private PostAdapter postAdapter;
    private FirebaseUser fUser;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        items = new HashMap<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(this , mPosts);
        type = (Spinner) findViewById(R.id.typeSpinner);
        idList = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        Button openDialogButton = findViewById(R.id.add_item);
        openDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment dialogFragment = new CustomDialogFragment();
                dialogFragment.setDialogListener((CustomDialogFragment.DialogListener) PostActivity.this); // Set the listener to the activity
                dialogFragment.show(getSupportFragmentManager(), "CustomDialogFragment");
            }
        });



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , SearchActivity.class));
                finish();
            }
        });

        imageAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to select picture to post
                selectImage();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        type.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        type.setAdapter(aa);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        choose = types[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    private void readPosts() {
        Call<List<Post>> call = RetrofitClient.getInstance().getAPI().homePosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                mPosts.clear();
                List<Post> post_unfollow = new ArrayList<>();
                List<Post> postsList = response.body();
                if (postsList != null) {
                    for(Post post : postsList){
                        if (idList != null && idList.contains(post.getPublisher())) {
                            mPosts.add(post);
                        }
                        else{
                            post_unfollow.add(post);
                        }
                    }
                    mPosts.addAll(post_unfollow);

                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Handle failure
            }
        });

    }
    private void getFollowings() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getFollowings(fUser.getUid());
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
                            if(!str.equals(fUser.getUid()))
                                idList.add(str);
                        }
                    }
                    readPosts();
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

    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        //if the uri of the picture is null
        if (imageUri != null){
            storageReference = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask uploadtask = storageReference.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return storageReference .getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    JSONObject jsonItems = new JSONObject(items);
                    String jsonString = jsonItems.toString();
                    Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().addPost(imageUrl,
                            FirebaseAuth.getInstance().getCurrentUser().getUid(), jsonString, choose);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("makePost", "success");

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("makePost", t.getMessage());
                        }
                    });
                    getFollowings();
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "The post has been uploaded!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this , SearchActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            imageAdded.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , SearchActivity.class));
            finish();
        }
    }

    @Override
    public void onSave(String name, String store, String price, String more) {
        // Handle the entered values in the main activity
        // You can save the values to variables, update UI, or perform any desired actions

        // Example: Log the entered values
        HashMap<String, String> innerHashMap = new HashMap<>();
        innerHashMap.put("name", name);
        innerHashMap.put("store", store);
        innerHashMap.put("price", price);
        innerHashMap.put("more", more);

        items.put(UUID.randomUUID().toString(),innerHashMap);

    }
}



