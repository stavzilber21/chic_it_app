package com.example.chic_it_app.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chic_it_app.EditProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileModel {
    private Activity activity;
    private StorageTask uploadTask;

    public EditProfileModel(Activity activity) {
        this.activity = activity;

    }
    public void firebase_username(FirebaseUser fUser, EditText fullname, EditText username, CircleImageView imageProfile){
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                Picasso.get().load(user.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void pust_gender_size(String text_gender, String text_size,FirebaseUser fUser){
        HashMap<String, Object> map = new HashMap<>();
        map.put("gender", text_gender);
        map.put("size", text_size);
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
    }

    public void update_profile(EditText fullname, EditText username,FirebaseUser fUser){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", fullname.getText().toString());
        map.put("username", username.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
    }

    public void uploadImage(Uri mImageUri,StorageReference storageRef,FirebaseUser fUser) {
        //Upload image to firebase storage
        final ProgressDialog pd = new ProgressDialog(activity);
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

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();
                    } else {
                        Toast.makeText(activity, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}
