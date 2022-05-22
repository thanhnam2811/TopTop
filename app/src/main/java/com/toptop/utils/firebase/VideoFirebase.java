package com.toptop.utils.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

	public static void getDataVideoId(MyCallback myCallback, String commentId) {
		Query myQuery = FirebaseUtil.getCommentById(commentId);
		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String value = dataSnapshot.getChildren().iterator().next().child("videoId").getValue(String.class);
				System.out.println(value);
				myCallback.onCallback(value);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.i(TAG, "onCancelled: ", databaseError.toException());
			}
		});
	}

	//	get video by id from commentId
	public static void getVideoFromCommentId(VideoCallback videoCallback, String commentId) {
		getDataVideoId(new MyCallback() {
			@Override
			public void onCallback(String value) {
				Query myQuery = FirebaseUtil.getVideoById(value);
				myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						Video video = dataSnapshot.getChildren().iterator().next().getValue(Video.class);
						System.out.println(video);
						videoCallback.onCallback(video);
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {
						Log.i(TAG, "onCancelled: ", error.toException());
					}
				});
			}
		}, commentId);
	}
	// get Video From VideoId
	public static void getVideoFromVideoId(VideoCallback videoCallback, String videoId) {
		Query myQuery = FirebaseUtil.getVideoById(videoId);
		myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Video video = dataSnapshot.getChildren().iterator().next().getValue(Video.class);
				System.out.println(video);
				videoCallback.onCallback(video);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.i(TAG, "onCancelled: ", error.toException());
			}
		});
	}

	public static void getPreviewVideo(MyCallback myCallback, String commentId) {
		getDataVideoId(value -> {
			Query myQuery = FirebaseUtil.getVideoById(value);
			myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String value = dataSnapshot.getChildren().iterator().next().child("preview").getValue(String.class);
					System.out.println(value);
					myCallback.onCallback(value);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.i(TAG, "onCancelled: ", databaseError.toException());
				}
			});
		}, commentId);

	}

	public static void deleteCommentFromVideo(Comment comment, Video video) {
		// Delete comment
		video.removeComment(comment);

		// Update video
		updateVideo(video);

		// Log
		Log.i(TAG, "deleteCommentFromVideo: " + comment.getCommentId() + " deleted from " + video.getVideoId());
	}


	//	Try fix
	public interface MyCallback {
		void onCallback(String value);
	}

	public interface VideoCallback {
		void onCallback(Video video);
	}
}
