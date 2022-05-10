package com.toptop.utils.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.User;
import com.toptop.models.Video;

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

	// Get Comment by replyToCommentId
	public static Query getCommentByReplyToCommentId(String replyToCommentId) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_COMMENTS);
		return ref.orderByChild("replyToCommentId").equalTo(replyToCommentId);
	}

	// Get user by username
	public static Query getUserByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("username").equalTo(username).limitToFirst(1);
	}

	// Get videos by username
	public static Query getVideosByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("username").equalTo(username);
	}
	//get notifications by username
	public static Query getNotificationsByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_NOTIFICATIONS);
		return ref.orderByChild("username").equalTo(username);
	}
	//	get comment by id
	public static Query getCommentById(String commentId) {
		Log.d(TAG, "getCommentById: " + commentId);
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_COMMENTS);
		return ref.orderByChild("commentId").equalTo(commentId);
	}
	//get video by id
	public static Query getVideoById(String videoId) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("videoId").equalTo(videoId);
	}

	//	get user by string like username
	public static Query getUserByStringLikeUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("username").startAt(username).endAt(username + "\uf8ff");
	}
	//	get video by string like content
	public static Query getVideoByStringLikeContent(String content) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("content").startAt(content).endAt(content + "\uf8ff");
//		return ref.orderByChild("content").startAt("[a-zA-Z0-9]*").endAt(content + "\uf8ff");
	}

}
