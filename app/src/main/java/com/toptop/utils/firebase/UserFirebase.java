package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.models.User;

public class UserFirebase {
	// Tag
	private static final String TAG = "UserFirebase";

	// Add user to firebase
	public static void addUser(User user) {
		DatabaseReference userRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_USERS);
		userRef.child(user.getUsername()).setValue(user);
		Log.i(TAG, "addUser: " + user.getUsername() + " added to firebase");
	}

	// Update user to firebase
	public static void updateUser(User user) {
		DatabaseReference userRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_USERS);
		userRef.child(user.getUsername()).setValue(user);
		Log.i(TAG, "updateUser: " + user.getUsername() + " updated to firebase");
	}

	// Delete user from firebase
	public static void deleteUser(User user) {
		DatabaseReference userRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_USERS);
		userRef.child(user.getUsername()).removeValue();
		Log.i(TAG, "deleteUser: " + user.getUsername() + " deleted from firebase");
	}

}
