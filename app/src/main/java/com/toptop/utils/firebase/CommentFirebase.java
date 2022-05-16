package com.toptop.utils.firebase;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.models.Video;

public class CommentFirebase {
	// Tag
	public static final String TAG = "CommentFirebase";
	public static final DatabaseReference commentRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);

	// Add comment to firebase
	public static void addCommentToVideo(Comment comment, Video video) {
		// add comment to video
		addComment(comment);

		// add comment to video
		VideoFirebase.addCommentToVideo(comment, video);
	}

	// Delete comment from firebase
	public static void deleteCommentFromVideo(Comment comment, Video video) {
		// delete comment from video
		deleteComment(comment);

		// delete comment from video
		VideoFirebase.deleteCommentFromVideo(comment, video);
	}


	// Update comment to firebase
	public static void updateComment(Comment comment) {
		commentRef.child(comment.getCommentId()).setValue(comment);
		Log.i(TAG, "updateComment: " + comment.getCommentId() + " updated to firebase");
	}

	// Delete comment from firebase
	public static void deleteComment(Comment comment) {
		commentRef.child(comment.getCommentId()).removeValue();
		Log.i(TAG, "deleteComment: " + comment.getCommentId() + " deleted from firebase");
	}

	// Add comment to firebase
	public static void addComment(Comment comment) {
		if (comment.getCommentId() == null)
			comment.setCommentId(commentRef.push().getKey());
		commentRef.child(comment.getCommentId()).setValue(comment);
		Log.i(TAG, "addComment: " + comment.getCommentId() + " added to firebase");
	}

	public static void unlikeComment(Comment comment) {
		comment.removeLikes(MainActivity.getCurrentUser().getUsername());
		updateComment(comment);

		// Log
		Log.i(TAG, "unlikeComment: " + comment.getCommentId() + " unliked");
	}

	public static void likeComment(Comment comment) {
		comment.addLikes(MainActivity.getCurrentUser().getUsername());
		updateComment(comment);

		// Log
		Log.i(TAG, "likeComment: " + comment.getCommentId() + " liked");
	}
}
