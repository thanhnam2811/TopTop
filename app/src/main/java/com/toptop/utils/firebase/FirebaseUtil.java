package com.toptop.utils.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {
	public static final String TABLE_USERS = "users",
			TABLE_COMMENTS = "comments",
			TABLE_NOTIFICATIONS = "notifications",
			TABLE_VIDEOS = "videos",
			TABLE_STATISTICS = "statistics",
			FOLDER_IMAGES = "images",
			FOLDER_VIDEOS = "videos";
	private final static String TAG = "FirebaseUtil";
	private final static String FIREBASE_URL = "https://toptop-4d369-default-rtdb.asia-southeast1.firebasedatabase.app/";

	// Get Database reference
	public static DatabaseReference getDatabase(String tableName) {
		return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(tableName);
	}

	//get notifications by username
	public static Query getNotificationsByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_NOTIFICATIONS);
		return ref.orderByChild("username").equalTo(username);
	}

	//	get comment by id
	public static Query getCommentById(String commentId) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_COMMENTS);
		return ref.orderByChild("commentId").equalTo(commentId);
	}

	//get video by id
	public static Query getVideoById(String videoId) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("videoId").equalTo(videoId);
	}

	// Get video by username
	public static Query getVideoByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("username").equalTo(username);
	}

	// Get user by email
	public static Query getUserByEmail(String email) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("email").equalTo(email).limitToFirst(1);
	}

	// Get user by username
	public static Query getUserByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("username").equalTo(username).limitToFirst(1);
	}


	//	get user by string like username
	public static Query getUserByStringLikeUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("username").startAt(username).endAt(username + "\uf8ff");
	}

	// get storage reference for images
	public static StorageReference getImageStorageReference() {
		return FirebaseStorage.getInstance().getReference().child(FOLDER_IMAGES);
	}

	// get storage reference for videos
	public static StorageReference getVideoStorageReference() {
		return FirebaseStorage.getInstance().getReference().child(FOLDER_VIDEOS);
	}

}
