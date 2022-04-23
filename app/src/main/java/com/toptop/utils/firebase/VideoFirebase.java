package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
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
}
