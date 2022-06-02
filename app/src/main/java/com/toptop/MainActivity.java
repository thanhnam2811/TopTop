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
import androidx.core.app.NotificationManagerCompat;
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
import com.toptop.service.NotificationService;
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
					// start service
					startService(new Intent(this, NotificationService.class));
				}
			}, user.getEmail());
		} else {
			currentUser = null;
			updateUI();
			mAuth.signOut();
			//stop service
			stopService(new Intent(this, NotificationService.class));
			// Log
			Log.i(TAG, "setCurrentUser: logged out");
		}
	}

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	public void updateUI() {
		VideoFragment.getInstance().updateUI();
		ProfileFragment.getInstance().updateUI();
		NotificationFragment.getInstance().updateUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (activeFragment == null || activeFragment instanceof VideoFragment) {
			MyUtil.setDarkStatusBar(this);
		} else {
			MyUtil.setLightStatusBar(this);
		}
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
				if (data != null) {
					User user = (User) data.getSerializableExtra(RegisterActivity.USER);
					setCurrentUser(user);
					changeNavItem(0);
					Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
				}
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

		mAuth = FirebaseAuth.getInstance();
		init();

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
					.add(R.id.fragment_container, SearchFragment.getInstance(), SearchFragment.TAG).hide(SearchFragment.getInstance())
					.addToBackStack(SearchFragment.TAG)
					.add(R.id.fragment_container, NotificationFragment.getInstance(), NotificationFragment.TAG).hide(NotificationFragment.getInstance())
					.addToBackStack(NotificationFragment.TAG)
					.add(R.id.fragment_container, NotLoginProfileFragment.getInstance(), NotLoginProfileFragment.TAG).hide(NotLoginProfileFragment.getInstance())
					.addToBackStack(NotLoginProfileFragment.TAG)
					.add(R.id.fragment_container, ProfileFragment.getInstance(), ProfileFragment.TAG).hide(ProfileFragment.getInstance())
					.addToBackStack(ProfileFragment.TAG)
					.add(R.id.fragment_container, VideoFragment.getInstance(), VideoFragment.TAG).hide(VideoFragment.getInstance())
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
					.show(VideoFragment.getInstance())
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
						.runOnCommit(() -> MyUtil.setDarkStatusBar(this))
						.hide(activeFragment).show(VideoFragment.getInstance())
						.commitAllowingStateLoss();
				activeFragment = VideoFragment.getInstance();
				Log.i(NAV_TAG, "Change to video fragment");
				break;
			case R.drawable.ic_search:
				getSupportFragmentManager().beginTransaction()
						.runOnCommit(() -> MyUtil.setLightStatusBar(this))
						.hide(activeFragment).show(SearchFragment.getInstance())
						.commit();
				VideoFragment.getInstance().pauseVideo();
				Log.i(NAV_TAG, "Change to search fragment");
				activeFragment = SearchFragment.getInstance();
				break;
			case R.drawable.ic_notification:
				getSupportFragmentManager().beginTransaction()
						.runOnCommit(() -> MyUtil.setLightStatusBar(this))
						.hide(activeFragment).show(NotificationFragment.getInstance())
						.commit();
				activeFragment = NotificationFragment.getInstance();
				VideoFragment.getInstance().pauseVideo();
				Log.i(NAV_TAG, "Change to notification fragment");
				break;
			case R.drawable.ic_profile:
				if (isLoggedIn()) {
					getSupportFragmentManager().beginTransaction()
							.runOnCommit(() -> MyUtil.setLightStatusBar(this))
							.hide(activeFragment).show(ProfileFragment.getInstance())
							.commit();
					activeFragment = ProfileFragment.getInstance();
				} else {
					getSupportFragmentManager().beginTransaction()
							.runOnCommit(() -> MyUtil.setLightStatusBar(this))
							.hide(activeFragment).show(NotLoginProfileFragment.getInstance())
							.commit();
					activeFragment = NotLoginProfileFragment.getInstance();
				}
				VideoFragment.getInstance().pauseVideo();
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