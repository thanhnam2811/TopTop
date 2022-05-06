package com.toptop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.LoginActivity;
import com.toptop.formEditIProfile;
import com.toptop.MainActivity;
import com.toptop.editinfouser;
import com.toptop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.toptop.adapters.CommentFragmentAdapter;
import com.toptop.adapters.VideoFragementAdapter;
import com.toptop.databinding.ActivityRegisterBinding;
import com.toptop.models.Comment;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    // Tag
    private static final String TAG = "ProfileFragment";
    //    FirebaseDatabase database;
    DatabaseReference database;
    private User user;


    public ProfileFragment() {
        // Required empty public constructor
//        MainActivity.getCurrentUser();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (!MainActivity.isLoggedIn()) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            user = MainActivity.getCurrentUser();

            TextView fullname = view.findViewById(R.id.fullname);
            TextView follow = view.findViewById(R.id.follow);
            TextView follower = view.findViewById(R.id.follower);
            TextView liketotal = view.findViewById(R.id.liketotal);
            ImageView btnGetToEditUserInfo = view.findViewById(R.id.btnGetToEditUserInfo);

            DatabaseReference dbUser = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_USERS);
            dbUser.child(user.getUsername()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = new User(snapshot);
                    fullname.setText(user.getFullname());
                    follow.setText(user.getNumFollowing().toString());
                    follower.setText(user.getNumFollowers().toString());
                    liketotal.setText(user.getNumLikes().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    fullname.setText(user.getFullname());
                    follow.setText(user.getNumFollowing().toString());
                    follower.setText(user.getNumFollowers().toString());
                    liketotal.setText(user.getNumLikes().toString());
                }
            });
            //event get to Edit User Info
            btnGetToEditUserInfo.setOnClickListener(view1 -> {
                Intent intent = new Intent(requireActivity(), editinfouser.class);

                startActivity(intent);
            });
        }


        // Set status bar color

        ((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_LIGHT_MODE);


        // Inflate the layout for this fragment
        return view;
    }


}