package com.toptop.utils.firebase;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.models.Notification;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;

import java.util.Map;

public class CommentFirebase {
	// Tag
	public static final String TAG = "CommentFirebase";
	public static final DatabaseReference commentRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);

	// Add comment to firebase
	public static void addCommentToVideo(Comment comment, Video video) {
		// add comment to video
		String commentId = addComment(comment);

		//Add notification to video owner
		Notification notification = new Notification();
		notification.setUsername(video.getUsername());
		notification.setContent(MainActivity.getCurrentUser().getUsername() + " bình luận video của bạn");
		notification.setType(Notification.TYPE_COMMENT);
		notification.setTime(MyUtil.getCurrentTime());
		notification.setRedirectTo(commentId);
		NotificationFirebase.addNotification(notification);

		// add comment to video
		VideoFirebase.addCommentToVideo(comment, video);
	}

	// Delete comment from firebase
	public static void deleteCommentFromVideo(Comment comment, Video video) {
		// delete comment from video
		deleteComment(comment);
		//Delete notification of comment
		NotificationFirebase.deleteNotificationByRedirectTo(comment.getCommentId());
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

		// delete notification of comment
		NotificationFirebase.deleteNotificationByRedirectTo(comment.getCommentId());
		Log.i(TAG, "deleteComment: " + comment.getCommentId() + " deleted from firebase");
	}

	// Add comment to firebase
	public static String addComment(Comment comment) {
		String commentId = commentRef.push().getKey();
		if (comment.getCommentId() == null)
			comment.setCommentId(commentId);
		commentRef.child(comment.getCommentId()).setValue(comment);
		Log.i(TAG, "addComment: " + comment.getCommentId() + " added to firebase");
		return commentId;
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

	//delete comment by videoID
	public static void deleteCommentByVideoId(String videoId) {
		commentRef.orderByChild("videoId").equalTo(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					Comment comment = data.getValue(Comment.class);
					deleteComment(comment);
					//delete notification of comment
					NotificationFirebase.deleteNotificationByRedirectTo(comment.getCommentId());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	// delete comment by commentId
	public static void deleteCommentByCommentId(String commentId) {
		commentRef.child(commentId).removeValue();
		// delete notification of comment
		NotificationFirebase.deleteNotificationByRedirectTo(commentId);
	}

	 public static void deleteCommentFromVideo(Video video) {
		for (Map.Entry<String, Boolean> entry : video.getComments().entrySet()) {
			deleteCommentByCommentId(entry.getKey());
		}
	 }
}
