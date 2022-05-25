package com.toptop;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.Comment;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.service.NotificationService;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.Objects;

import kotlin.Unit;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends FragmentActivity {
	private static final String CHANNEL_ID = "channel_service";
	private FirebaseAuth mAuth;
	public static final String EXTRA_REGISTER = "register";
	public static final String EXTRA_LOGIN = "login";
	private static final String TAG = "MainActivity", NAV_TAG = "Navigation";
	public static final int LOGIN_REQUEST_CODE = 1;
	public static final int REGISTER_REQUEST_CODE = 2;
	public static final int REQUEST_CHANGE_AVATAR = 3;
	public static final int REQUEST_ADD_VIDEO = 4;
	private static User currentUser;
	private NotificationManagerCompat notificationManagerCompat;
	private static final String KEY_TEXT_REPLY = "key_text_reply";
	private static final int NOTIFICATION_COMMENT_ID = 1;
	private static final String COMMENT_NOTIFICATION = "comment_notification";

	@SuppressLint("StaticFieldLeak")
	CurvedBottomNavigationView nav;
	CbnMenuItem[] items;

	public static User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User user) {
		if (user != null) {
			// Log
			Log.i(TAG, "setCurrentUser: " + user.getUsername());

			// If first time login or change user
			if (currentUser == null || !currentUser.getUsername().equals(user.getUsername())) {
				// Set current user
				Query query = FirebaseUtil.getUserByUsername(user.getUsername());
				query.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot snapshot) {
						if (snapshot.exists()) {
							currentUser = new User(snapshot.getChildren().iterator().next());
							updateUI();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {

					}
				});
			}
		} else {
			currentUser = null;
			updateUI();
			mAuth.signOut();
			// Log
			Log.i(TAG, "setCurrentUser: logged out");
		}
	}

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public void updateUI() {
		ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG);
		if (profileFragment != null) {
			profileFragment.updateUI();
		}
		VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
		if (videoFragment != null) {
			videoFragment.updateUI();
		}
		NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG);
		if (notificationFragment != null) {
			notificationFragment.updateUI();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			Query query = FirebaseUtil.getUserByEmail(currentUser.getEmail());
			query.get().addOnSuccessListener(documentSnapshot -> {
				if (documentSnapshot.exists()) {
					User user = new User(documentSnapshot.getChildren().iterator().next());
					setCurrentUser(user);
					//get notification for current user and send notification unseen
					//start notification service
					startService(new Intent(this, NotificationService.class));
				} else {
					setCurrentUser(null);
					//stop service
					Intent intentService = new Intent(this, NotificationService.class);
					stopService(intentService);
				}
				init();
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == LOGIN_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					User user = (User) data.getSerializableExtra(LoginActivity.USER);
					setCurrentUser(user);
					changeNavItem(0);
					Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (data != null && data.getBooleanExtra(EXTRA_REGISTER, false)) {
					openRegisterActivity();
				} else {
					Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (requestCode == REGISTER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				User user = (User) data.getSerializableExtra(RegisterActivity.USER);
				setCurrentUser(user);
				changeNavItem(0);
				Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
			} else {
				if (data != null && data.getBooleanExtra(EXTRA_LOGIN, false)) {
					openLoginActivity();
				} else {
					Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (requestCode == REQUEST_CHANGE_AVATAR || requestCode == REQUEST_ADD_VIDEO) {
			// Don't do anything
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void openRegisterActivity() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivityForResult(intent, REGISTER_REQUEST_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_DARK_MODE, this);

		mAuth = FirebaseAuth.getInstance();
		init();

		//get reply from notification
		Intent intent = this.getIntent();
		Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
		if (remoteInput != null) {
			String reply = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString();
			if (reply != null && !reply.isEmpty()) {
				Bundle extras = getIntent().getExtras();
				String commentId = extras.getString(COMMENT_NOTIFICATION);
				if (commentId != null && !commentId.isEmpty()) {
					//Get video by commentId
					VideoFirebase.getVideoFromCommentId(video -> {
						if (video != null) {
							//create new comment
							Comment commentReply = new Comment();
							commentReply.setContent(reply);
							commentReply.setUsername(currentUser.getUsername());
							commentReply.setVideoId(video.getVideoId());

							//add comment to database
							CommentFirebase.addCommentToVideo(commentReply, video);
							Toast.makeText(this, "Đã trả lời bình luận!", Toast.LENGTH_SHORT).show();
						}
					}, commentId);
				}

			}
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(NOTIFICATION_COMMENT_ID);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void init() {
		if (nav == null) {
			Log.w(TAG, "init: nav is null");
			// Bind the view using the id
			nav = findViewById(R.id.nav);

			// Set list of items
			items = new CbnMenuItem[]{
					new CbnMenuItem(R.drawable.ic_video, R.drawable.ic_video_avd, 0),
					new CbnMenuItem(R.drawable.ic_search, R.drawable.ic_search_avd, 0),
//					add label to show the number of notifications

					new CbnMenuItem(R.drawable.ic_notification, R.drawable.ic_notification_avd, 0),
					new CbnMenuItem(R.drawable.ic_profile, R.drawable.ic_profile_avd, 0)
			};

			// Add fragment to the container
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, SearchFragment.getInstance(), SearchFragment.TAG)
					.addToBackStack(SearchFragment.TAG)
					.add(R.id.fragment_container, NotificationFragment.getInstance(), NotificationFragment.TAG)
					.addToBackStack(NotificationFragment.TAG)
					.add(R.id.fragment_container, ProfileFragment.getInstance(), ProfileFragment.TAG)
					.addToBackStack(ProfileFragment.TAG)
					.add(R.id.fragment_container, VideoFragment.getInstance(), VideoFragment.TAG)
					.addToBackStack(VideoFragment.TAG)
					.commit();

			// Execute transaction
			getSupportFragmentManager().executePendingTransactions();

			// Set the items
			nav.setMenuItems(items, 0);

			// Set the listener
			nav.setOnMenuItemClickListener((cbnMenuItem, integer) -> handleMenuItemClick(cbnMenuItem));

			// Set the default fragment
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container,
							Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG)))
					.commit();

			// Set navigation bar color
			getWindow().setNavigationBarColor(Color.WHITE);

			getPermission();

		}
	}

	private Unit handleMenuItemClick(CbnMenuItem cbnMenuItem) {
		switch (cbnMenuItem.getIcon()) {
			case R.drawable.ic_video:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container,
								Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG)))
						.commit();
				Log.i(NAV_TAG, "Change to video fragment");
				break;
			case R.drawable.ic_search:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container,
								Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SearchFragment.TAG)))
						.commit();
				Log.i(NAV_TAG, "Change to search fragment");
				break;
			case R.drawable.ic_notification:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container,
								Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(NotificationFragment.TAG)))
						.commit();
				Log.i(NAV_TAG, "Change to notification fragment");
				break;
			case R.drawable.ic_profile:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container,
								Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG)))
						.commit();
				Log.i(NAV_TAG, "Change to profile fragment");
				break;
		}
		return null;
	}

	private void getPermission() {
		// Get INTERNET permission if needed
		if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED)
			requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);

	}

	public void changeNavItem(int position) {
		if (nav != null) {
			nav.onMenuItemClick(position);
		}
	}

	public void goToProfileUser(String username) {
		Intent intent = new Intent(this, WatchProfileActivity.class);
		intent.putExtra(User.TAG, username);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed: fragment_comment_container shown? " + (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE));
		// Set onBackPressed when fragment_comment_container is showing
		if (findViewById(R.id.fragment_comment_container) != null &&
				findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE) {
			hideCommentFragment();
		} else super.onBackPressed();
	}

	// Open login activity
	public void openLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, LOGIN_REQUEST_CODE);
	}

	public void hideCommentFragment() {
		Log.i(TAG, "onBackPressed: HIDE KEYBOARD");
		KeyboardUtils.hideKeyboard(this);
		Log.i(TAG, "onBackPressed: HIDE LAYOUT_COMMENT");
		findViewById(R.id.fragment_comment_container).setVisibility(View.GONE);

		// Enable scroll
		RecyclerView recycler_view_videos = findViewById(R.id.recycler_view_videos);
		recycler_view_videos.removeOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
	}

	public void logOut() {
		setCurrentUser(null);
		changeNavItem(0);
	}
}