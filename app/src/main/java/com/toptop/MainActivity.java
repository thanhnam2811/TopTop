package com.toptop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.Query;
import com.toptop.adapters.VideoFragementAdapter;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.User;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.Objects;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity", NAV_TAG = "Navigation", SAVE_USER = "SaveUser", SAVE_PASSWORD = "SavePassword";
	private static SharedPreferences mAppPreferences;
	private static SharedPreferences.Editor mEditor;
	private static User currentUser;
	CurvedBottomNavigationView nav;
	CbnMenuItem[] items;

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		// Log
		Log.i(TAG, "setCurrentUser: " + currentUser.getUsername());
		MainActivity.currentUser = currentUser;

		// Save user to memory
		mEditor.putString(SAVE_USER, currentUser.getUsername());
		mEditor.putString(SAVE_PASSWORD, currentUser.getPassword());
		mEditor.apply();
	}

	private void getUserFromMemory() {
		// Get user from memory
		String username = mAppPreferences.getString(SAVE_USER, null);
		String password = mAppPreferences.getString(SAVE_PASSWORD, null);
		if (username != null && password != null) {
			// Set current user
			Query query = FirebaseUtil.getUserByUsername(username);
			query.get().addOnSuccessListener(dataSnapshot -> {
				if (dataSnapshot.exists()) {
					User user = new User(dataSnapshot.getChildren().iterator().next());
					if (user.getPassword().equals(password)) {
						setCurrentUser(user);
						// Log
						Log.i(TAG, "getUserFromMemory: " + user.getUsername());
					} else {
						// Log
						Log.i(TAG, "getUserFromMemory: Password is not correct");
					}
				}
			});
		} else
			// Log
			Log.i(TAG, "getUserFromMemory: User is not found");
	}

	public static final String
			STATUS_BAR_LIGHT_MODE = "status_bar_light_mode",
			STATUS_BAR_DARK_MODE = "status_bar_dark_mode";

	public static final String
			VIDEO_FRAGMENT_TAG = "video",
			SEARCH_FRAGMENT_TAG = "search",
			NOTIFICATION_FRAGMENT_TAG = "notification",
			PROFILE_FRAGMENT_TAG = "profile";

	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAppPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mEditor = mAppPreferences.edit();

		// Get user from memory
		getUserFromMemory();

		init();
	}

	private void init() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

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
				.add(R.id.fragment_container, new SearchFragment(), SEARCH_FRAGMENT_TAG)
				.addToBackStack(SEARCH_FRAGMENT_TAG)
				.add(R.id.fragment_container, new NotificationFragment(), NOTIFICATION_FRAGMENT_TAG)
				.addToBackStack(NOTIFICATION_FRAGMENT_TAG)
				.add(R.id.fragment_container, new ProfileFragment(), PROFILE_FRAGMENT_TAG)
				.addToBackStack(PROFILE_FRAGMENT_TAG)
				.add(R.id.fragment_container, new VideoFragment(), VIDEO_FRAGMENT_TAG)
				.addToBackStack(VIDEO_FRAGMENT_TAG)
				.commit();

		// Execute transaction
		getSupportFragmentManager().executePendingTransactions();

		// Set the items
		nav.setMenuItems(items, 0);

		// Set the listener
		nav.setOnMenuItemClickListener((cbnMenuItem, integer) -> {
			switch (cbnMenuItem.getIcon()) {
				case R.drawable.ic_video:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.fragment_container,
									Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(VIDEO_FRAGMENT_TAG)))
							.commit();
					Log.i(NAV_TAG, "Change to video fragment");
					break;
				case R.drawable.ic_search:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.fragment_container,
									Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG)))
							.commit();
					Log.i(NAV_TAG, "Change to search fragment");
					break;
				case R.drawable.ic_notification:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.fragment_container,
									Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(NOTIFICATION_FRAGMENT_TAG)))
							.commit();
					Log.i(NAV_TAG, "Change to notification fragment");
					break;
				case R.drawable.ic_profile:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.fragment_container,
									Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG)))
							.commit();
					Log.i(NAV_TAG, "Change to profile fragment");
					break;
			}
			return null;
		});

		// Set the default fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container,
						Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(VIDEO_FRAGMENT_TAG)))
				.commit();

		// Set navigation bar color
		getWindow().setNavigationBarColor(Color.WHITE);

		getPermission();
	}

	private void getPermission() {
		// Get INTERNET permission if needed
		if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED)
			requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);

	}

	// Set status bar color mode
	public void setStatusBarColor(String mode) {
		if (mode.equals(STATUS_BAR_DARK_MODE)) {
			getWindow().setStatusBarColor(Color.BLACK);
			getWindow().getDecorView().setSystemUiVisibility(0);
			Log.i(TAG, "setStatusBarColor: DARK MODE");
		} else if (mode.equals(STATUS_BAR_LIGHT_MODE)) {
			getWindow().setStatusBarColor(Color.WHITE);
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			Log.i(TAG, "setStatusBarColor: LIGHT MODE");
		}
	}

	public void changeNavItem(int position) {
		nav.setMenuItems(items, position);
	}

	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed: fragment_comment_container shown? " + (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE));
		// Set onBackPressed when fragment_comment_container is showing
		if (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE) {
			Log.i(TAG, "onBackPressed: HIDE KEYBOARD");
			KeyboardUtils.hideKeyboard(this);
			Log.i(TAG, "onBackPressed: HIDE LAYOUT_COMMENT");
			findViewById(R.id.fragment_comment_container).setVisibility(View.GONE);

			// Enable scroll
			RecyclerView recycler_view_videos = findViewById(R.id.recycler_view_videos);
			recycler_view_videos.removeOnItemTouchListener(VideoFragementAdapter.disableTouchListener);
		} else super.onBackPressed();
	}

	// Open login activity
	public void openLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}