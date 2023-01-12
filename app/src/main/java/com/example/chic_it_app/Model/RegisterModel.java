package com.example.chic_it_app.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chic_it_app.MainActivity;
import com.example.chic_it_app.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterModel {
    /*This class is the model of the "RegisterActivity" all the connection to Firebase is done through this class.*/
    private Activity activity;
    DatabaseReference reference;

    public RegisterModel(Activity activity) {
        this.activity = activity;

    }
    //connect to details of user to firebase
    public void register(final String username, final String fullname, String email, String password, String phone , FirebaseAuth auth, ProgressDialog pd){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, (OnCompleteListener<AuthResult>) new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            // to create sub tree in firebase to Users
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("phone", phone);
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/chicit-a5e00.appspot.com/o/placeholder.png?alt=media&token=e355a742-f8f6-4ca6-b4d4-734dfb6091a3");
                            //to enter the fields of User to tree in the firebase
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(activity, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivity(intent);
                                    }

                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(activity,"You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
