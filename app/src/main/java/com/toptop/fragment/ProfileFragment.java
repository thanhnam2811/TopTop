package com.toptop.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
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
	ImageView avatar;
	RecyclerView recyclerView;
	Context context;
	NestedScrollView scrollView;
	ConstraintLayout layoutProfile;

	public ProfileFragment() {
	}

	@SuppressLint("SetTextI18n")
	public void updateUI() {
		if (MainActivity.isLoggedIn()) {
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
		query.get().addOnSuccessListener(snapshot -> {
			if (snapshot.exists()) {
				List<Video> videos = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					Video video = new Video(dataSnapshot);
					videos.add(video);
				}
				videos.addAll(videos);
				videos.addAll(videos);
				videos.addAll(videos);
				videos.addAll(videos);
				videos.addAll(videos);
				videos.addAll(videos);
				VideoGridAdapter adapter = new VideoGridAdapter(videos, context);
				recyclerView.setAdapter(adapter);

				recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
			}
		});
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
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

		updateUI();

		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		// Inflate the layout for this fragment
		return view;
	}

}