package com.toptop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.adapters.VideoGridAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.List;

public class WatchProfileActivity extends AppCompatActivity {
	// Tag
	private static final String TAG = "WatchProfileActivity";

	private static final int HEADER_HEIGHT = 8 * 2 + 32 + 1;
	TextView fullname, numFollowers, numFollowing, numLikes, username, txt_follow_status;
	ImageView avatar, ic_back;
	RecyclerView recyclerView;
	Context context;
	NestedScrollView scrollView;
	ConstraintLayout layoutProfile;
	User user;
	List<Video> videos = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_profile);

		context = this;

		// Get the Intent that started this activity and extract the string
		Bundle extras = getIntent().getExtras();
		String usernameText = extras.getString(User.TAG);
		Log.d("WatchProfileActivity", "usernameText: " + usernameText);

		// Bind view
		fullname = findViewById(R.id.txt_fullname);
		numFollowers = findViewById(R.id.txt_num_followers);
		numFollowing = findViewById(R.id.txt_num_following);
		numLikes = findViewById(R.id.txt_num_likes);
		username = findViewById(R.id.txt_email);
		avatar = findViewById(R.id.img_avatar);
		recyclerView = findViewById(R.id.recycler_view_videos_grid);
		scrollView = findViewById(R.id.scroll_view);
		layoutProfile = findViewById(R.id.layout_profile);
		txt_follow_status = findViewById(R.id.txt_follow_status);

		ic_back = findViewById(R.id.ic_back);
		ic_back.setOnClickListener(v -> finish());

		txt_follow_status.setOnClickListener(v -> {
			if (MainActivity.getCurrentUser().isFollowing(user.getUsername())) {
				UserFirebase.unfollowUser(user.getUsername());
			} else {
				UserFirebase.followUser(user.getUsername());

			}
		});

		loadData(usernameText);

		MyUtil.setLightStatusBar(this);
	}

	private void loadData(String usernameText) {
		// For first time load data
//		query.get().addOnSuccessListener(snapshot -> {
//			if (snapshot.exists()) {
//				Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
//				User newUser = new User(snapshot.getChildren().iterator().next());
//				updateUI(newUser);
//			}
//		});
		UserFirebase.getUserByUsername(usernameText,
				this::updateUI, error -> {
					Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, "loadData: " + error.getMessage());
				});
	}

	@SuppressLint("SetTextI18n")
	private void updateUI(User user) {
		this.user = user;
		fullname.setText(user.getFullname());
		numFollowers.setText(user.getNumFollowers() + "");
		numFollowing.setText(user.getNumFollowing() + "");
		numLikes.setText(user.getNumLikes() + "");
		username.setText(user.getUsername());
		if (MainActivity.isLoggedIn())
			setFollowStatus(MainActivity.getCurrentUser().isFollowing(user.getUsername()));
		else
			txt_follow_status.setVisibility(View.GONE);
		try {
			Glide.with(context)
					.load(user.getAvatar())
					.error(R.drawable.default_avatar)
					.into(avatar);
		} catch (Exception e) {
			Log.w(TAG, "Glide error: " + e.getMessage());
		}

		prepareRecyclerView(user);
	}

	private void setFollowStatus(boolean following) {
		if (following) {
			txt_follow_status.setText(getResources().getString(R.string.following));
			txt_follow_status.setTextColor(getResources().getColor(R.color.secondary_color));
			txt_follow_status.setBackground(getResources().getDrawable(R.drawable.text_box_bg_second));
		} else {
			txt_follow_status.setText(getResources().getString(R.string.follow));
			txt_follow_status.setTextColor(getResources().getColor(R.color.main_color));
			txt_follow_status.setBackground(getResources().getDrawable(R.drawable.text_box_bg));
		}
	}

	private void prepareRecyclerView(User user) {
		VideoFirebase.getVideoByUsernameOneTime(user.getUsername(),
				listvideos -> {
					if (listvideos.size() > 0) {
						if (videos == null || !videos.equals(listvideos)) {
							videos = listvideos;
						}
						if (recyclerView.getAdapter() == null) {
							VideoGridAdapter adapter = new VideoGridAdapter(videos, context);
							recyclerView.setAdapter(adapter);
							recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
						} else {
							((VideoGridAdapter) recyclerView.getAdapter()).setVideos(videos);
						}
					}
				},
				error -> Toast.makeText(context, "Failed to load videos", Toast.LENGTH_SHORT).show()
		);
	}
}