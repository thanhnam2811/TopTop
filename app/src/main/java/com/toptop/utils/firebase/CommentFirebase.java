package com.toptop.utils.firebase;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.models.Comment;
import com.toptop.models.Video;

public class CommentFirebase {
	// Tag
	public static final String TAG = "CommentFirebase";

	// Add comment to firebase
	public static void addCommentToVideo(Comment comment, Video video) {
		// add comment to video
		DatabaseReference commentRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		comment.setCommentId(commentRef.push().getKey());
		commentRef.child(comment.getCommentId()).setValue(comment);

		// add comment to video
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		videoRef.child(video.getVideoId()).child(FirebaseUtil.TABLE_COMMENTS)
				.child(comment.getCommentId()).setValue(true);

		// log
		Log.i(TAG, "addComment: " + comment.getCommentId() + " added to firebase");
	}

	// Update comment to firebase
	public static void updateComment(Comment comment) {
		DatabaseReference commentRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		commentRef.child(comment.getCommentId()).setValue(comment);
		Log.i(TAG, "updateComment: " + comment.getCommentId() + " updated to firebase");
	}

	// Delete comment from firebase
	public static void deleteComment(Comment comment) {
		DatabaseReference commentRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		commentRef.child(comment.getCommentId()).removeValue();
		Log.i(TAG, "deleteComment: " + comment.getCommentId() + " deleted from firebase");
	}
}
