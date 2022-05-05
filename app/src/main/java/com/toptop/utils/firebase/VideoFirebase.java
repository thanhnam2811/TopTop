package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.models.Video;

public class VideoFirebase {
	// Tag
	public static final String TAG = "VideoFirebase";
	public static final DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);

	// Add video to firebase
	public static void addVideo(Video video) {
		if (video.getVideoId() == null)
			video.setVideoId(videoRef.push().getKey());
		videoRef.child(video.getVideoId()).setValue(video);
		Log.i(TAG, "addVideo: " + video.getVideoId() + " added to firebase");
	}

	// Update video to firebase
	public static void updateVideo(Video video) {
		videoRef.child(video.getVideoId()).setValue(video);
		Log.i(TAG, "updateVideo: " + video.getVideoId() + " updated to firebase");
	}

	// Delete video to firebase
	public static void deleteVideo(Video video) {
		videoRef.child(video.getVideoId()).removeValue();
		Log.i(TAG, "deleteVideo: " + video.getVideoId() + " deleted from firebase");
	}

	// Add comment to video
	public static void addCommentToVideo(Comment comment, Video video) {
		// Add comment to video
		video.addComment(comment);

		// Update video
		updateVideo(video);

		// Log
		Log.i(TAG, "addCommentToVideo: " + comment.getCommentId() + " added to " + video.getVideoId());
	}

	// Like video
	public static void likeVideo(Video video) {
		// Add user to like list
		video.addLike(MainActivity.getCurrentUser().getUsername());

		// Log
		Log.i(TAG, "likeVideo: " + video.getVideoId() + " liked by " + MainActivity.getCurrentUser().getUsername());

		// Update video
		updateVideo(video);
	}

	// Unlike video
	public static void unlikeVideo(Video video) {
		// Remove user from like list
		video.removeLike(MainActivity.getCurrentUser().getUsername());

		// Log
		Log.i(TAG, "unlikeVideo: " + video.getVideoId() + " unliked by " + MainActivity.getCurrentUser().getUsername());

		// Update video
		updateVideo(video);
	}
}
