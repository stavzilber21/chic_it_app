package com.example.chic_it_app;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.example.chic_it_app.Model.User;
import com.squareup.picasso.Picasso;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView close;
    private CircleImageView imageProfile;
    private TextView save;
    private TextView changePhoto;
    private EditText fullname;
    private EditText username;


    private FirebaseUser fUser;
    private String imageurl = null;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    String[] gender_opt ={"male","female"};
    String[] size_opt ={"s","m","l"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        changePhoto = findViewById(R.id.change_photo);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");

        Spinner spin = (Spinner) findViewById(R.id.gender);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender_opt);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        Spinner spin2 = (Spinner) findViewById(R.id.size);
        spin2.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter bb = new ArrayAdapter(this,android.R.layout.simple_spinner_item, size_opt);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin2.setAdapter(bb);

        //fUser = FirebaseAuth.getInstance().getCurrentUser();
        Call<User> call = RetrofitClient.getInstance().getAPI().getUserDetails(fUser.getUid());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                Picasso.get().load( user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                imageurl = user.getImageurl();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //to change my profile picture
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select image from gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent ,1000);

            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender = spin.getSelectedItem().toString();
                String size = spin2.getSelectedItem().toString();
                String full_name = fullname.getText().toString();
                String user_name = username.getText().toString();
                Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().editProfile(fUser.getUid(), gender, size, full_name, user_name, imageurl);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("add user", "success");
                        finish();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("add user", t.getMessage());
                    }
                });
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private void uploadImage() {
        //Upload image to firebase storage
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return  fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        imageurl = url;
                        //FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                mImageUri = data.getData();
                imageProfile.setImageURI(mImageUri);
                uploadImage();
            }
        }
        else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}