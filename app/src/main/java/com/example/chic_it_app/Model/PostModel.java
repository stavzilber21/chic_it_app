package com.example.chic_it_app.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chic_it_app.CreatingcontentActivity;
import com.example.chic_it_app.PostActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class PostModel {
    private Activity activity;
    StorageReference storageReference;
    String imageUrl;
    ProgressDialog progressDialog;



    public PostModel(Activity activity) {
        this.activity = activity;

    }
    public void uploadImage(Uri imageUri,EditText description,EditText store,EditText price,String choose) {

        progressDialog = new ProgressDialog(activity);
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

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String , Object> map = new HashMap<>();
                    map.put("postid" , postId);
                    map.put("imageurl" , imageUrl);
                    map.put("description" , description.getText().toString());
                    map.put("store" , store.getText().toString());
                    map.put("price" , price.getText().toString());
                    map.put("type", choose);
                    map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    progressDialog.dismiss();
                    Toast.makeText(activity, "The post has been uploaded!", Toast.LENGTH_SHORT).show();
                    activity.startActivity(new Intent(activity , CreatingcontentActivity.class));
                    activity.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(activity, "No image was selected!", Toast.LENGTH_SHORT).show();
        }

    }
    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.getContentResolver().getType(uri));

    }
}