package com.toptop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.toptop.models.User;
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

        txtFullnameUser.setText(currentUser.getFullname());
        txtSDT.setText(currentUser.getPhoneNumber());
        txtEmail.setText(currentUser.getEmail());

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