package com.toptop;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.RemoteInput;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.fragment.NotLoginProfileFragment;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.Comment;
import com.toptop.models.User;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import kotlin.Unit;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends FragmentActivity {
	private FirebaseAuth mAuth;
	public static final String EXTRA_REGISTER = "register";
	public static final String EXTRA_LOGIN = "login";
	private static final String TAG = "MainActivity", NAV_TAG = "Navigation";
	public static final int LOGIN_REQUEST_CODE = 1;
	public static final int REGISTER_REQUEST_CODE = 2;
	public static final int REQUEST_CHANGE_AVATAR = 3;
	public static final int REQUEST_ADD_VIDEO = 4;
	private static User currentUser;
	private static final String KEY_TEXT_REPLY = "key_text_reply";
	private static final int NOTIFICATION_ID = 1;
	private static final String COMMENT_NOTIFICATION = "comment_notification";

	@SuppressLint("StaticFieldLeak")
	CurvedBottomNavigationView nav;
	CbnMenuItem[] items;
	Fragment activeFragment;
	VideoFragment videoFragment = VideoFragment.getInstance();
	ProfileFragment profileFragment = ProfileFragment.getInstance();
	NotLoginProfileFragment notLoginProfileFragment = NotLoginProfileFragment.getInstance();
	SearchFragment searchFragment = SearchFragment.getInstance();
	NotificationFragment notificationFragment = NotificationFragment.getInstance();

	public static User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User user) {
		if (user != null) {
			UserFirebase.getUserByEmail(u -> {
				if (u != null) {
					Log.i(TAG, "setCurrentUser: " + u.getUsername());
					currentUser = u;
					updateUI();
				}
			}, user.getEmail());
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
		profileFragment.updateUI();

		videoFragment.updateUI();

		notificationFragment.updateUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			User user = new User();
			user.setEmail(currentUser.getEmail());
			setCurrentUser(user);
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
		} else {
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
//				Toast.makeText(this, "Reply: " + reply, Toast.LENGTH_SHORT).show();
				Bundle extras = getIntent().getExtras();
				String commentId = extras.getString(COMMENT_NOTIFICATION);
//				Toast.makeText(this, "CommentId: " + commentId, Toast.LENGTH_SHORT).show();
				if (commentId != null && !commentId.isEmpty()) {
					//Get video by commentId
					VideoFirebase.getVideoFromCommentId(video -> {
						if (video != null) {
							//create new comment
							Comment commentReply = new Comment();
							commentReply.setContent(reply);
							commentReply.setUsername(currentUser.getUsername()); // ? or currentUser.getUsername()
							commentReply.setVideoId(video.getVideoId());

							//add comment to database
							CommentFirebase.addCommentToVideo(commentReply, video);
							Toast.makeText(this, "Đã trả lời bình luận!", Toast.LENGTH_SHORT).show();
						}
					}, commentId);
				}

			}
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(NOTIFICATION_ID);
		}
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
					new CbnMenuItem(R.drawable.ic_notification, R.drawable.ic_notification_avd, 0),
					new CbnMenuItem(R.drawable.ic_profile, R.drawable.ic_profile_avd, 0)
			};

			// Add fragment to the container
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, searchFragment, SearchFragment.TAG).hide(searchFragment)
					.addToBackStack(SearchFragment.TAG)
					.add(R.id.fragment_container, notificationFragment, NotificationFragment.TAG).hide(notificationFragment)
					.addToBackStack(NotificationFragment.TAG)
					.add(R.id.fragment_container, notLoginProfileFragment, NotLoginProfileFragment.TAG).hide(notLoginProfileFragment)
					.addToBackStack(NotLoginProfileFragment.TAG)
					.add(R.id.fragment_container, profileFragment, ProfileFragment.TAG).hide(profileFragment)
					.addToBackStack(ProfileFragment.TAG)
					.add(R.id.fragment_container, videoFragment, VideoFragment.TAG).hide(videoFragment)
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
					.show(videoFragment)
					.commit();
			activeFragment = VideoFragment.getInstance();

			// Set navigation bar color
			getWindow().setNavigationBarColor(Color.WHITE);

			getPermission();

		}
	}

	private Unit handleMenuItemClick(CbnMenuItem cbnMenuItem) {
		switch (cbnMenuItem.getIcon()) {
			case R.drawable.ic_video:
				getSupportFragmentManager().beginTransaction()
						.hide(activeFragment).show(VideoFragment.getInstance())
						.commit();
				activeFragment = VideoFragment.getInstance();
				Log.i(NAV_TAG, "Change to video fragment");
				break;
			case R.drawable.ic_search:
				getSupportFragmentManager().beginTransaction()
						.hide(activeFragment).show(SearchFragment.getInstance())
						.commit();
				activeFragment = SearchFragment.getInstance();
				Log.i(NAV_TAG, "Change to search fragment");
				break;
			case R.drawable.ic_notification:
				getSupportFragmentManager().beginTransaction()
						.hide(activeFragment).show(NotificationFragment.getInstance())
						.commit();
				activeFragment = NotificationFragment.getInstance();
				Log.i(NAV_TAG, "Change to notification fragment");
				break;
			case R.drawable.ic_profile:
				if (isLoggedIn()) {
					getSupportFragmentManager().beginTransaction()
							.hide(activeFragment).show(ProfileFragment.getInstance())
							.commit();
					activeFragment = ProfileFragment.getInstance();
				} else {
					getSupportFragmentManager().beginTransaction()
							.hide(activeFragment).show(NotLoginProfileFragment.getInstance())
							.commit();
					activeFragment = NotLoginProfileFragment.getInstance();
				}
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
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.exit_confirm);
			builder.setPositiveButton(R.string.yes, (dialog, which) -> finish());
			builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
			builder.show();
		}
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