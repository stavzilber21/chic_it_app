package com.example.chic_it_app;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import java.util.HashMap;


public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ImageView close;
    ImageView imageAdded;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String imageUrl;
    EditText description;
    EditText store;
    EditText price;
    TextView post;
    Spinner type;
    Spinner gen;
    String[] gender_opt ={"male","female"};
    String[] types={"inspiration","rent","For Sale"};
    String choose_type;
    String choose_gender;
    Button add_details;
    static HashMap<String , Object> map = new HashMap<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        store = findViewById(R.id.store);
        price = findViewById(R.id.price);
        add_details = findViewById(R.id.add_details);

        type = (Spinner) findViewById(R.id.typeSpinner);
        gen = (Spinner) findViewById(R.id.gender);
        gen.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter bb = new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender_opt);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        gen.setAdapter(bb);

        type.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        type.setAdapter(aa);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , CreatingcontentActivity.class));
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

        add_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this , PostDescriction.class));
            }
        });





    }
    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        choose_type = types[position];
        choose_gender = gender_opt[position];
//        Toast.makeText(getApplicationContext(), types[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

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

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    map.put("postid" , postId);
                    map.put("imageurl" , imageUrl);
                    map.put("description" , description.getText().toString());
                    map.put("store" , store.getText().toString());
                    map.put("price" , price.getText().toString());
                    map.put("type", choose_type);
                    map.put("gender", choose_gender);
                    map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "The post has been uploaded!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this , CreatingcontentActivity.class));
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

    public static void add_Item(String item, String size, String length){
        HashMap<String , Object> map_item = new HashMap<>();
//        map_item.put("item",item);
//        map_item.put("size",size);
//        map_item.put("length", length);
        map_item.put("item",item);
        map_item.put("size",size);
        map_item.put("length",length);
//        Toast.makeText(PostActivity.this, map.get("item").toString(), Toast.LENGTH_SHORT).show();

        map.put("Item",map_item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            imageAdded.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , CreatingcontentActivity.class));
            finish();
        }
    }

}