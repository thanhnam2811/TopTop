package com.toptop.utils;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.toptop.models.Comment;
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
		return ref.orderByChild("video_id").equalTo(videoId);
	}

	// Add comment to database
	public static void addComment(Video video, Comment comment) {
		Log.i(TAG, "addComment: " + comment.getComment_id());

		// Add comment to firebase
		DatabaseReference
				mDB_comment = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		mDB_comment.child(comment.getComment_id()).setValue(comment);

		// Add comment to video
		DatabaseReference mDB_video = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		mDB_video.child(video.getVideo_id()).child(FirebaseUtil.TABLE_COMMENTS).child(comment.getComment_id()).setValue(true);
	}

	// Set number of comments for video
	public static void setNumberOfComments(long numberOfComments, Video video) {
		DatabaseReference mDB_video = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		video.setTotalComments(numberOfComments);
		mDB_video.child(video.getVideo_id()).child("total_comments").setValue(video.getTotalComments());
		Log.i(TAG, "setNumberOfComments: " + video.getTotalComments());
	}
}
