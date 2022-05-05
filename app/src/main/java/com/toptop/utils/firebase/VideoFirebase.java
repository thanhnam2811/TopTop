package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.models.Video;

public class VideoFirebase {
	// Tag
	public static final String TAG = "VideoFirebase";

	// Add video to firebase
	public static void addVideo(Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		video.setVideoId(videoRef.push().getKey());
		videoRef.child(video.getVideoId()).setValue(video);
		Log.i(TAG, "addVideo: " + video.getVideoId() + " added to firebase");
	}

	// Update video to firebase
	public static void updateVideo(Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		videoRef.child(video.getVideoId()).setValue(video);
		Log.i(TAG, "updateVideo: " + video.getVideoId() + " updated to firebase");
	}

	// Delete video to firebase
	public static void deleteVideo(Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		videoRef.child(video.getVideoId()).removeValue();
		Log.i(TAG, "deleteVideo: " + video.getVideoId() + " deleted from firebase");
	}

	// Add comment to video
	public static void addCommentToVideo(Comment comment, Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		videoRef.child(video.getVideoId()).child(FirebaseUtil.TABLE_COMMENTS)
				.child(comment.getCommentId()).setValue(true);
		// set comment count
		videoRef.child(video.getVideoId()).child("numComments").setValue(video.getNumComments() + 1);
	}

	// Like video
	public static void likeVideo(Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);

		// Add user to like list
		videoRef.child(video.getVideoId())
				.child("likes")
				.child(MainActivity.getCurrentUser().getUsername()).setValue(true);

		// set like count
		videoRef.child(video.getVideoId())
				.child("numLikes").setValue(video.getNumLikes() + 1);

		// Log
		Log.i(TAG, "likeVideo: " + video.getVideoId() + " liked by " + MainActivity.getCurrentUser().getUsername());
	}

	// Unlike video
	public static void unlikeVideo(Video video) {
		DatabaseReference videoRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);

		// Remove user from like list
		videoRef.child(video.getVideoId())
				.child("likes")
				.child(MainActivity.getCurrentUser().getUsername()).removeValue();

		// set like count
		videoRef.child(video.getVideoId())
				.child("numLikes").setValue(video.getNumLikes() - 1);

		// Log
		Log.i(TAG, "unlikeVideo: " + video.getVideoId() + " unliked by " + MainActivity.getCurrentUser().getUsername());
	}
}
