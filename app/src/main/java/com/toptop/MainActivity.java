package com.toptop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.User;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.Objects;

import kotlin.Unit;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends FragmentActivity {
	public static final String EXTRA_REGISTER = "register";
	public static final String EXTRA_LOGIN = "login";
	private static final String TAG = "MainActivity", NAV_TAG = "Navigation", SAVE_USER = "SaveUser";
	private static final int LOGIN_REQUEST_CODE = 1;
	private static final int REGISTER_REQUEST_CODE = 2;
	private static SharedPreferences mAppPreferences;
	private static SharedPreferences.Editor mEditor;
	private static User currentUser;

	@SuppressLint("StaticFieldLeak")
	CurvedBottomNavigationView nav;
	CbnMenuItem[] items;

	public static User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User user) {
		changeNavItem(0);
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

						if (currentUser != null && !currentUser.getUsername().equals(mAppPreferences.getString(SAVE_USER, ""))) {
							// Save user to memory
							mEditor.putString(SAVE_USER, currentUser.getUsername());
							mEditor.apply();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {

					}
				});
			}
		} else {
			currentUser = null;
			mEditor.clear();
			mEditor.apply();
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
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOGIN_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					User user = (User) data.getSerializableExtra(LoginActivity.USER);
					setCurrentUser(user);
				}
			} else {
				if (data != null && data.getBooleanExtra(EXTRA_REGISTER, false)) {
					openRegisterActivity();
				} else {
					Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (requestCode == REGISTER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				User user = (User) data.getSerializableExtra(RegisterActivity.USER);
				setCurrentUser(user);
			} else {
				if (data != null && data.getBooleanExtra(EXTRA_LOGIN, false)) {
					openLoginActivity();
				} else {
					Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
				}
			}
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

		mAppPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mEditor = mAppPreferences.edit();

		init();

		// Get user from memory
		String username = mAppPreferences.getString(SAVE_USER, null);
		if (username != null) {
			Query query = FirebaseUtil.getUserByUsername(username);
			query.get().addOnSuccessListener(documentSnapshot -> {
				if (documentSnapshot.exists()) {
					User user = new User(documentSnapshot.getChildren().iterator().next());
					setCurrentUser(user);
				} else {
					Log.i(TAG, "getUserFromMemory: User not found");
				}
				init();
			});
		}
	}

	private void init() {
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
				.add(R.id.fragment_container, new SearchFragment(), SearchFragment.TAG)
				.addToBackStack(SearchFragment.TAG)
				.add(R.id.fragment_container, new NotificationFragment(), NotificationFragment.TAG)
				.addToBackStack(NotificationFragment.TAG)
				.add(R.id.fragment_container, new ProfileFragment(), ProfileFragment.TAG)
				.addToBackStack(ProfileFragment.TAG)
				.add(R.id.fragment_container, new VideoFragment(), VideoFragment.TAG)
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
		nav.onMenuItemClick(position);
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
	}
}