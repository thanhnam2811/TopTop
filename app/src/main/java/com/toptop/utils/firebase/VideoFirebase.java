package com.toptop.utils.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.models.Notification;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

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

		StatisticFirebase.addNewVideo();
	}

	// Update video to firebase
	public static void updateVideo(Video video) {
		videoRef.child(video.getVideoId()).setValue(video);
		Log.i(TAG, "updateVideo: " + video.getVideoId() + " updated to firebase");
	}

	// Delete video to firebase
	public static void deleteVideo(Video video) {
		videoRef.child(video.getVideoId()).removeValue();
		//delete notification from video
		NotificationFirebase.deleteNotificationByRedirectTo(video.getVideoId());
		//delete comment of video
		CommentFirebase.deleteCommentFromVideo(video);
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

		//Add notification like video
		Notification notification = new Notification();
		notification.setUsername(video.getUsername());
		notification.setType(Notification.TYPE_LIKE);
		notification.setContent(MainActivity.getCurrentUser().getUsername() + " đã thích video của bạn");
		notification.setTime(MyUtil.getCurrentTime());
		notification.setRedirectTo(video.getVideoId());
		NotificationFirebase.addNotification(notification);

		StatisticFirebase.addNewLike();
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
		CommentFirebase.getCommentByCommentId(commentId,
				comment -> myCallback.onCallback(comment.getVideoId()),
				databaseError -> Log.e(TAG, "getDataVideoId: " + databaseError.getMessage()));
	}

	//	get video by id from commentId
	public static void getVideoFromCommentId(VideoCallback videoCallback, String commentId) {
		getDataVideoId(value -> {
			Query myQuery = FirebaseUtil.getVideoById(value);
			myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					Video video = dataSnapshot.getChildren().iterator().next().getValue(Video.class);
					System.out.println(video);
					videoCallback.onCallback(video);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					Log.i(TAG, "onCancelled: ", error.toException());
				}
			});
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

	public static void addView(Video video) {
		video.addView();
		updateVideo(video);

		StatisticFirebase.addView();
	}

	// Get video by username
	public static void getVideoByUsername(String username, ListVideoCallback listVideoCallback, FailedCallback failedCallback) {
		videoRef.child("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				List<Video> videos = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					Video video = dataSnapshot.getValue(Video.class);
					videos.add(video);
				}
				listVideoCallback.onCallback(videos);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				failedCallback.onCallback(error);
			}
		});
	}

	// Get list video like content
	public static void getVideoLikeContent(String content, ListVideoCallback listVideoCallback, FailedCallback failedCallback) {
		videoRef.orderByChild("content")
				.startAt(content)
				.endAt(content + "\uf8ff")
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot snapshot) {
						List<Video> videos = new ArrayList<>();
						for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
							Video video = dataSnapshot.getValue(Video.class);
							videos.add(video);
						}
						listVideoCallback.onCallback(videos);
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {
						failedCallback.onCallback(error);
					}
				});
	}

	// Get video by id
	public static void getVideoById(String id, VideoCallback videoCallback, FailedCallback failedCallback) {
		videoRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Video video = new Video(snapshot);
				videoCallback.onCallback(video);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				failedCallback.onCallback(error);
			}
		});
	}


	//	Try fix
	public interface MyCallback {
		void onCallback(String value);
	}

	public interface VideoCallback {
		void onCallback(Video video);
	}

	public interface ListVideoCallback {
		void onCallback(List<Video> videos);
	}

	public interface FailedCallback {
		void onCallback(DatabaseError error);
	}
}
