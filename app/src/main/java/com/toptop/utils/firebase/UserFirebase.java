package com.toptop.utils.firebase;

import static com.toptop.utils.firebase.FirebaseUtil.getUserByUsername;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.models.User;

public class UserFirebase {
	// Tag
	private static final String TAG = "UserFirebase";
	private static final DatabaseReference userRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_USERS);

	// Add user to firebase
	public static void addUser(User user) {
		userRef.child(user.getUsername()).setValue(user);
		Log.i(TAG, "addUser: " + user.getUsername() + " added to firebase");
	}

	// Update user to firebase
	public static void updateUser(User user) {
		userRef.child(user.getUsername()).setValue(user);
		Log.i(TAG, "updateUser: " + user.getUsername() + " updated to firebase");
	}

	// Delete user from firebase
	public static void deleteUser(User user) {
		userRef.child(user.getUsername()).removeValue();
		Log.i(TAG, "deleteUser: " + user.getUsername() + " deleted from firebase");
	}

	// Follow user
	public static void followUser(String username) {
		// Get current user
		User user = MainActivity.getCurrentUser();
		user.follow(username);

		// Update user to firebase
		updateUser(user);

		Log.i(TAG, "followUser: " + user.getUsername() + " followed " + username);

		// Add user to followers of username
		userRef.child(username).get().addOnSuccessListener(dataSnapshot -> {
			User userToFollow = new User(dataSnapshot);
			userToFollow.addFollower(user.getUsername());
			updateUser(userToFollow);
		});
	}

	// Unfollow user
	public static void unfollowUser(String username) {
		// Get current user
		User user = MainActivity.getCurrentUser();
		user.unfollow(username);

		// Update user to firebase
		updateUser(user);

		Log.i(TAG, "unfollowUser: " + user.getUsername() + " unfollowed " + username);

		// Remove user from followers of username
		userRef.child(username).get().addOnSuccessListener(dataSnapshot -> {
			User userToUnfollow = new User(dataSnapshot);
			userToUnfollow.removeFollower(user.getUsername());
			updateUser(userToUnfollow);
		});
	}

	public interface MyCallback {
		void onCallback(String value);
	}

	public static void readDataUser(MyCallback myCallback, String username) {
		Query myQuery = getUserByUsername(username);
		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				String value = dataSnapshot.getChildren().iterator().next().child("avatar").getValue(String.class);
				System.out.println(value);
				myCallback.onCallback(value);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Log.e(TAG, "onCancelled: ", databaseError.toException());
			}
		});
	}
}
