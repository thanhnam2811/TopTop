package com.toptop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptop.fragment.ProfileFragment;
import com.toptop.models.User;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.UserFirebase;

public class editinfouser extends AppCompatActivity {
    public static final String TAG = "UserFirebase";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfouser);

        //binding
        EditText txtFullnameUser = findViewById(R.id.txtFullnameUser);
        EditText txtSDT = findViewById(R.id.txtSDT);
        EditText txtEmail = findViewById(R.id.txtEmail);

        ImageView btnBackToProfile= findViewById(R.id.btnBackToProfile);
        Button btnUpdateInfo= findViewById(R.id.btnUpdateInfo);

        User currentUser= MainActivity.getCurrentUser();

        txtFullnameUser.setText(currentUser.getFullname().toString());
        txtSDT.setText(currentUser.getPhoneNumber().toString());
        txtEmail.setText(currentUser.getEmail().toString());

//        User newUser = new  User(currentUser.getUsername().toString(),currentUser.getPassword().toString()
//                ,txtFullnameUser.getText().toString(),
//                txtSDT.getText().toString(),
//                txtEmail.getText().toString(),
//                currentUser.getAvatar().toString(),
//                currentUser.getNumFollowers().toString(),
//                currentUser.getNumFollowing().toString(),
//                currentUser.getNumLikes(),
//                currentUser.getFollowings().hashCode(),
//                currentUser.getFollowers());





        //Event
        btnUpdateInfo.setOnClickListener(view -> {
            User user = MainActivity.getCurrentUser();
            user.setFullname(txtFullnameUser.getText().toString());
            user.setPhoneNumber(txtSDT.getText().toString());
            user.setEmail(txtEmail.getText().toString());

            UserFirebase.updateUser(user);
            finish();
        });
        btnBackToProfile.setOnClickListener(view -> {
            finish();
        });
    }

}