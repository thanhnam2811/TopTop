package com.toptop.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.EditProfileActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.VideoGridAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
	// Tag
	public static final String TAG = "ProfileFragment";

	private static final int HEADER_HEIGHT = 8 * 2 + 32 + 1;

	TextView fullname, numFollowers, numFollowing, numLikes, username;
	ImageView avatar, ic_menu, ic_edit;
	RecyclerView recyclerView;
	Context context;
	NestedScrollView scrollView;
	ConstraintLayout layoutProfile;
	NavigationView navigationView;
	Boolean isLogin = false;

	public ProfileFragment() {
	}

	@SuppressLint("SetTextI18n")
	public void updateUI() {
		if (isLogin) {
			User user = MainActivity.getCurrentUser();
			Log.i(TAG, "updateUI: " + user.toString());
			fullname.setText(user.getFullname());
			numFollowers.setText(user.getNumFollowers() + "");
			numFollowing.setText(user.getNumFollowing() + "");
			numLikes.setText(user.getNumLikes() + "");
			username.setText(user.getUsername());
			Glide.with(context)
					.load(user.getAvatar())
					.into(avatar);
			prepareRecyclerView(user);
		} else {
			Log.e(TAG, "updateUI: User is null");
		}
	}

	private void prepareRecyclerView(User user) {
		Query query = FirebaseUtil.getVideoByUsername(user.getUsername());

		// First time load
		query.get().addOnSuccessListener(snapshot -> {
			if (snapshot.exists()) {
				List<Video> videos = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					Video video = new Video(dataSnapshot);
					videos.add(video);
				}
				VideoGridAdapter adapter = new VideoGridAdapter(videos, context);
				recyclerView.setAdapter(adapter);

				recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
			}
		});

		// Update when any video is changed
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				VideoGridAdapter videoGridAdapter = (VideoGridAdapter) recyclerView.getAdapter();
				if (videoGridAdapter != null) {
					List<Video> videos = videoGridAdapter.getVideos();
					for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
						Video video = new Video(dataSnapshot);
						if (videos.contains(video)) {
							videos.set(videos.indexOf(video), video);
						} else {
							videos.add(video);
						}
					}
					videoGridAdapter.setVideos(videos);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		if (MainActivity.isLoggedIn()) {
			isLogin = true;
			View view = inflater.inflate(R.layout.fragment_profile, container, false);
			this.context = view.getContext();

			// Bind view
			fullname = view.findViewById(R.id.txt_fullname);
			numFollowers = view.findViewById(R.id.txt_num_followers);
			numFollowing = view.findViewById(R.id.txt_num_following);
			numLikes = view.findViewById(R.id.txt_num_likes);
			username = view.findViewById(R.id.txt_username);
			avatar = view.findViewById(R.id.img_avatar);
			recyclerView = view.findViewById(R.id.recycler_view_videos_grid);
			scrollView = view.findViewById(R.id.scroll_view);
			layoutProfile = view.findViewById(R.id.layout_profile);
			ic_menu = view.findViewById(R.id.ic_menu);
			ic_edit = view.findViewById(R.id.ic_edit);
			navigationView = view.findViewById(R.id.nav_view);
			DrawerLayout drawer = view.findViewById(R.id.drawer_layout);

			ic_menu.setOnClickListener(v -> drawer.open());

			ic_edit.setOnClickListener(v -> openEditProfileActivity());

			navigationView.setNavigationItemSelectedListener(v -> {
				switch (v.getItemId()) {
					case R.id.log_out:
						MainActivity mainActivity = (MainActivity) requireActivity();
						mainActivity.logOut();
						Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
				}

				drawer.close();
				return false;
			});



			updateUI();

			return view;
		} else {
			isLogin = false;
			View view = inflater.inflate(R.layout.fragment_profile_no_login, container, false);
			this.context = view.getContext();

			// Bind view
			Button btnLogin = view.findViewById(R.id.btn_login);
			btnLogin.setOnClickListener(v -> ((MainActivity) requireActivity()).openLoginActivity());

			Button btnRegister = view.findViewById(R.id.btn_register);
			btnRegister.setOnClickListener(v -> ((MainActivity) requireActivity()).openRegisterActivity());

			return view;
		}
	}

	private void openEditProfileActivity() {
		Intent intent = new Intent(context, EditProfileActivity.class);
		intent.putExtra(User.TAG, MainActivity.getCurrentUser());
		startActivity(intent);
	}

}