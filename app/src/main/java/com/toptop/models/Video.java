package com.toptop.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.toptop.MainActivity;
import com.toptop.utils.MyUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Video implements Serializable {
	// TAG
	public static final String TAG = "Video";
	public static final String VIDEO_ID = "videoId";

	private String videoId, username, content, linkVideo;
	private Long numLikes, numComments, numViews;
	private String dateUploaded = MyUtil.dateTimeToString(new Date());
	private HashMap<String, Boolean> likes;
	private HashMap<String, Boolean> comments;

	public Video() {
		likes = new HashMap<>();
		comments = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public Video(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		if (data != null) {
			this.videoId = dataSnapshot.getKey();
			this.username = (String) data.get("username");
			this.content = (String) data.get("content");
			this.linkVideo = (String) data.get("linkVideo");
			this.numViews = (Long) data.get("numViews");
			this.dateUploaded = (String) data.get("dateUploaded");
			this.likes = (HashMap<String, Boolean>) data.get("likes");
			this.comments = (HashMap<String, Boolean>) data.get("comments");
		}

		if (comments == null)
			numComments = 0L;
		else
			numComments = (long) comments.size();

		if (likes == null)
			numLikes = 0L;
		else
			numLikes = (long) likes.size();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		boolean isEqual = false;
		if (obj instanceof Video) {
			Video video = (Video) obj;
			isEqual = this.videoId.equals(video.getVideoId());
		}
		return isEqual;
	}

	public boolean hasChanged(Video video) {
		boolean hasChanged = false;
		hasChanged = !this.content.equals(video.getContent()) ||
				!this.likes.equals(video.getLikes()) ||
				!this.comments.equals(video.getComments()) ||
				!this.numViews.equals(video.getNumViews());
		return hasChanged;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLinkVideo() {
		return linkVideo;
	}

	public void setLinkVideo(String linkVideo) {
		this.linkVideo = linkVideo;
	}

	public Long getNumLikes() {
		if (numLikes == null)
			numLikes = 0L;
		return numLikes;
	}

	public void setNumLikes(Long numLikes) {
		this.numLikes = numLikes;
	}

	public Long getNumComments() {
		return numComments;
	}

	public void setNumComments(Long numComments) {
		this.numComments = numComments;
	}

	public Long getNumViews() {
		if (numViews == null)
			numViews = 0L;
		return numViews;
	}

	public void setNumViews(Long numViews) {
		this.numViews = numViews;
	}

	public String getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(String dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public HashMap<String, Boolean> getLikes() {
		return likes;
	}

	public void setLikes(HashMap<String, Boolean> likes) {
		this.likes = likes;
	}

	public HashMap<String, Boolean> getComments() {
		return comments;
	}

	public void setComments(HashMap<String, Boolean> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Video {" +
				"videoId='" + videoId + '\'' +
				", username='" + username + '\'' +
				", content='" + content + '\'' +
				", linkVideo='" + linkVideo + '\'' +
				", numLikes=" + numLikes +
				", numComments=" + numComments +
				", numViews=" + numViews +
				", dateUploaded='" + dateUploaded + '\'' +
				", likes=" + likes +
				", comments=" + comments +
				'}';
	}


	@Exclude
	// Like
	public boolean isLiked() {
		return likes != null && likes.containsKey(MainActivity.getCurrentUser().getUsername());
	}

	public void addComment(Comment comment) {
		if (comments == null)
			comments = new HashMap<>();

		comments.put(comment.getCommentId(), true);
		numComments++;
	}

	public void addLike(String username) {
		if (likes == null)
			likes = new HashMap<>();

		likes.put(username, true);
		numLikes++;
	}

	public void removeLike(String username) {
		if (likes != null && likes.containsKey(username)) {
			likes.remove(username);
			numLikes--;
		}
	}

	public void removeComment(Comment comment) {
		if (comments != null && comments.containsKey(comment.getCommentId())) {
			comments.remove(comment.getCommentId());
			numComments--;
		}
		Log.i(TAG, "removeComment: " + comment.getCommentId());
		Log.i(TAG, "removeComment: " + comments.toString());
	}

	public void addView() {
		numViews++;
	}
}
