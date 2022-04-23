package com.toptop.utils.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseUtil {
	private final static String TAG = "FirebaseUtil";
	private final static String FIREBASE_URL = "https://toptop-4d369-default-rtdb.asia-southeast1.firebasedatabase.app/";
	public static final String TABLE_USERS = "users",
			TABLE_COMMENTS = "comments",
			TABLE_NOTIFICATIONS = "notifications",
			TABLE_VIDEOS = "videos";

	// Get Database reference
	public static DatabaseReference getDatabase(String tableName) {
		return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(tableName);
	}

	// Get comments by video id
	public static Query getCommentsByVideoId(String videoId) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_COMMENTS);
		return ref.orderByChild("videoId").equalTo(videoId);
	}
}
