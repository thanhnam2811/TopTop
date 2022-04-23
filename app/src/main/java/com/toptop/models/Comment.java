package com.toptop.models;

import com.google.firebase.database.DataSnapshot;
import com.toptop.utils.MyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment {
	private String commentId, username, videoId, content;
	private Long numReplies, numLikes;
	private String time;
	private HashMap<String, Boolean> replies;
	private HashMap<String, Boolean> likes;

	public Comment() {
		replies = new HashMap<String, Boolean>();
		likes = new HashMap<String, Boolean>();
	}

	public Comment(String commentId, String username, String videoId, String content, Long numReplies, Long numLikes, String time, HashMap<String, Boolean> replies, HashMap<String, Boolean> likes) {
		this.commentId = commentId;
		this.username = username;
		this.videoId = videoId;
		this.content = content;
		this.numReplies = numReplies;
		this.numLikes = numLikes;
		this.time = time;
		this.replies = replies;
		this.likes = likes;
	}

	public Comment(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		this.commentId = dataSnapshot.getKey();
		this.username = (String) data.get("username");
		this.videoId = (String) data.get("videoId");
		this.content = (String) data.get("content");
		this.numReplies = (Long) data.get("numReplies");
		this.numLikes = (Long) data.get("numLikes");
		this.time = (String) data.get("time");
		this.replies = (HashMap<String, Boolean>) data.get("replies");
		this.likes = (HashMap<String, Boolean>) data.get("likes");
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getNumReplies() {
		return numReplies;
	}

	public void setNumReplies(Long numReplies) {
		this.numReplies = numReplies;
	}

	public Long getNumLikes() {
		return numLikes;
	}

	public void setNumLikes(Long numLikes) {
		this.numLikes = numLikes;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public HashMap<String, Boolean> getReplies() {
		return replies;
	}

	public void setReplies(HashMap<String, Boolean> replies) {
		this.replies = replies;
	}

	public HashMap<String, Boolean> getLikes() {
		return likes;
	}

	public void setLikes(HashMap<String, Boolean> likes) {
		this.likes = likes;
	}

	// Sort by time
	public static void sortByTimeNewsest(List<Comment> comments) {
		// Sort comments by time
		comments.sort((o1, o2) -> {
			Date time1 = MyUtil.getDateFromFormattedDateString(o1.getTime());
			Date time2 = MyUtil.getDateFromFormattedDateString(o2.getTime());
			if (time1 != null && time2 != null) {
				return time2.compareTo(time1);
			}
			return 0;
		});
	}

	@Override
	public String toString() {
		return "Comment {" +
				"commentId='" + commentId + '\'' +
				", username='" + username + '\'' +
				", videoId='" + videoId + '\'' +
				", content='" + content + '\'' +
				", numReplies=" + numReplies +
				", numLikes=" + numLikes +
				", time='" + time + '\'' +
				", replies=" + replies +
				", likes=" + likes +
				'}';
	}
}
