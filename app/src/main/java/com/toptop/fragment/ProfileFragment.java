package com.toptop.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.editinfouser;
import com.toptop.models.User;

public class ProfileFragment extends Fragment {
	// Tag
	public static final String TAG = "ProfileFragment";

	TextView fullname, follow, follower, liketotal;
	ImageView avatar;
	Context context;

	public ProfileFragment() {
	}

	@SuppressLint("SetTextI18n")
	public void updateUI(User user) {
		Log.i(TAG, "updateUI: " + user.toString());
		fullname.setText(user.getFullname());
		follow.setText(user.getNumFollowing().toString());
		follower.setText(user.getNumFollowers().toString());
		liketotal.setText(user.getNumLikes().toString());
		Glide.with(context)
				.load(user.getAvatar())
				.into(avatar);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		this.context = view.getContext();

		// Bind view
		fullname = view.findViewById(R.id.fullname);
		follow = view.findViewById(R.id.follow);
		follower = view.findViewById(R.id.follower);
		liketotal = view.findViewById(R.id.liketotal);
		avatar = view.findViewById(R.id.img_avatar);

		ImageView btnGetToEditUserInfo = view.findViewById(R.id.btnGetToEditUserInfo);

		//event get to Edit User Info
		btnGetToEditUserInfo.setOnClickListener(view1 -> {
			Intent intent = new Intent(requireActivity(), editinfouser.class);
			startActivity(intent);
		});

		if (MainActivity.isLoggedIn())
			updateUI(MainActivity.getCurrentUser());

		((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_LIGHT_MODE);

		// Inflate the layout for this fragment
		return view;
	}
}