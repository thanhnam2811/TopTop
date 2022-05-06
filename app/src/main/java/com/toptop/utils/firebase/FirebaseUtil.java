package com.toptop.utils.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.Video;

import java.util.concurrent.CountDownLatch;

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
	//get notifications by username
	public static Query getNotificationsByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_NOTIFICATIONS);
		return ref.orderByChild("username").equalTo(username);
	}
	//get user by username
	public static Query getUserByUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
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
//	get user by string like username
	public static Query getUserByStringLikeUsername(String username) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		return ref.orderByChild("username").startAt("[a-zA-Z0-9]*").endAt(username + "\uf8ff");
	}
//	get video by string like content
	public static Query getVideoByStringLikeContent(String content) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_VIDEOS);
		return ref.orderByChild("content").startAt(content).endAt(content + "\uf8ff");
//		return ref.orderByChild("content").startAt("[a-zA-Z0-9]*").endAt(content + "\uf8ff");
	}
//	check user is following user
	public static Boolean checkUserIsFollowingUser(String username, String currentUsername) {
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_USERS);
		Query query = ref.child(username).child("followers").orderByChild(currentUsername).equalTo(true);
		//check query is exist
		if (query == null) {
			return false;
		}
		return true;
	}
//	Try fix
	public interface VideoCallback {
		void onCallback(Video video);
	}
	public interface MyCallback {
		void onCallback(String value);
	}
	public static void readData(MyCallback myCallback,String username) {
		Query myQuery = getUserByUsername(username);
		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String value = dataSnapshot.getChildren().iterator().next().child("avatar").getValue(String.class);
				System.out.println(value);
				myCallback.onCallback(value);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.e(TAG, "onCancelled: ", databaseError.toException());
			}
		});
	}

	public static void getDataVideoId(MyCallback myCallback ,String commentId) {
		Query myQuery = getCommentById(commentId);
		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String value = dataSnapshot.getChildren().iterator().next().child("videoId").getValue(String.class);
				System.out.println(value);
				myCallback.onCallback(value);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.e(TAG, "onCancelled: ", databaseError.toException());
			}
		});
	}
//	get video by id from commentId
	public static void getVideoFromCommentId(VideoCallback videoCallback ,String commentId) {
		getDataVideoId(new MyCallback(){
			@Override
			public void onCallback(String value) {
				Query myQuery = getVideoById(value);
				myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						Video video = dataSnapshot.getChildren().iterator().next().getValue(Video.class);
						System.out.println(video);
						videoCallback.onCallback(video);
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {
						Log.e(TAG, "onCancelled: ", error.toException());
					}
				});
			}
		},commentId);
	}
	public static void getPreviewVideo(MyCallback myCallback ,String commentId) {
		getDataVideoId(new MyCallback() {
			@Override
			public void onCallback(String value) {
				Query myQuery = getVideoById(value);
				myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						String value = dataSnapshot.getChildren().iterator().next().child("preview").getValue(String.class);
						System.out.println(value);
						myCallback.onCallback(value);
					}
					@Override
					public void onCancelled(DatabaseError databaseError) {
						Log.e(TAG, "onCancelled: ", databaseError.toException());
					}
				});
			}
		},commentId);

	}
	//	get value avatar by username
//	public static String getAvatarByUsername(String username) {
//		Query myQuery = getUserByUsername(username);
//		final String[] avatar = new String[1];
//		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//			@Override
//			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//				avatar[0] = dataSnapshot.getChildren().iterator().next().child("avatar").getValue(String.class);
//				Log.d(TAG, "getLinkAvatar: " + avatar[0]);
//			}
//			@Override
//			public void onCancelled(@NonNull DatabaseError error) {
//				Log.d(TAG, "onCancelled: " + error.getMessage());
//			}
//		});
//		return avatar[0];
//	}
//	get redirectTo from notification by notification id
	public static String getRedirectTo(String notificationId) {
		final String[] redirectTo = {""};
		DatabaseReference ref = FirebaseDatabase.getInstance(FIREBASE_URL).getReference(TABLE_NOTIFICATIONS);
		ref.child(notificationId).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				redirectTo[0] = dataSnapshot.child("redirectTo").getValue(String.class);
				Log.d(TAG, "getRedirectTo: " + redirectTo[0]);
			}
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.d(TAG, "onCancelled: " + error.getMessage());
			}
		});
		System.out.println("redirectTo: ---" + redirectTo[0]);
		return redirectTo[0];
	}

}
