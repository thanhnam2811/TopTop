package com.toptop.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.toptop.MainActivity;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment {
	// Tag
	private static final String TAG = "Comment";
	private String commentId, username, videoId, content, replyToCommentId;
	private Long numReplies = 0L, numLikes = 0L;
	private String time;
	private HashMap<String, Boolean> replies;
	private HashMap<String, Boolean> likes;

	public Comment(String commentId, String username, String videoId, String content, String replyToCommentId, Long numLikes, String time, HashMap<String, Boolean> likes) {
		this.commentId = commentId;
		this.username = username;
		this.videoId = videoId;
		this.content = content;
		this.replyToCommentId = replyToCommentId;
		this.numLikes = numLikes;
		this.time = time;
		this.likes = likes;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof Comment) {
			Comment comment = (Comment) obj;
			return comment.getCommentId().equals(this.getCommentId());
		}
		return false;
	}

	public Comment() {
		replies = new HashMap<>();
		likes = new HashMap<>();
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

	@SuppressWarnings({"unchecked"})
	public Comment(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		if (data != null) {
			this.commentId = dataSnapshot.getKey();
			this.replyToCommentId = (String) data.get("replyToCommentId");
			this.username = (String) data.get("username");
			this.videoId = (String) data.get("videoId");
			this.content = (String) data.get("content");

			this.time = data.get("time") == null ? MyUtil.getFormattedDateStringFromDate(new Date()) :
					(String) data.get("time");
			if (data.get("numReplies") != null)
				this.numReplies = (Long) data.get("numReplies");
			else
				this.numReplies = 0L;
			if (data.get("numLikes") != null)
				this.numLikes = (Long) data.get("numLikes");
			else
				this.numLikes = 0L;
			if (data.get("replies") != null)
				replies = (HashMap<String, Boolean>) data.get("replies");
			else
				replies = new HashMap<>();
			if (data.get("likes") != null)
				likes = (HashMap<String, Boolean>) data.get("likes");
			else
				likes = new HashMap<>();
		}

		boolean hasChanged = false;
		if (this.numReplies != replies.size()) {
			this.numReplies = (long) this.replies.size();
			hasChanged = true;
		}
		if (this.numLikes != likes.size()) {
			this.numLikes = (long) this.likes.size();
			hasChanged = true;
		}
		if (this.time == null) {
			this.time = MyUtil.getFormattedDateStringFromDate(new Date());
			hasChanged = true;
		}

		// Set no replies if this comment replies to another comment
		if (this.replyToCommentId != null && this.numReplies != 0 && this.replies.size() != 0) {
			this.replies = new HashMap<>();
			this.numReplies = 0L;
			hasChanged = true;
		}

		if (hasChanged) CommentFirebase.updateComment(this);
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

	@NonNull
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

	public boolean isEqual(Comment comment) {
		return comment.getContent().equals(this.getContent()) &&
				comment.getNumLikes().equals(this.getNumLikes()) &&
				comment.getNumReplies().equals(this.getNumReplies()) &&
				comment.getLikes().equals(this.getLikes()) &&
				comment.getReplies().equals(this.getReplies());
	}

	public String getReplyToCommentId() {
		return replyToCommentId;
	}

	public void setReplyToCommentId(String replyToCommentId) {
		this.replyToCommentId = replyToCommentId;
	}

	public boolean isReply() {
		return this.replyToCommentId != null;
	}

	public boolean isLiked() {
		return likes != null && MainActivity.getCurrentUser() != null
				&& likes.containsKey(MainActivity.getCurrentUser().getUsername());
	}

	public boolean isOwner() {
		if (MainActivity.isLoggedIn()) {
			if (username.equals(MainActivity.getCurrentUser().getUsername())) {
				Log.i(TAG, "isOwner for comment \"" + content + "\"" + ": true (owner is: " + username + ")");
			}
		}
		return MainActivity.isLoggedIn() && username.equals(MainActivity.getCurrentUser().getUsername());
	}

	public void addLikes(String username) {
		if (likes == null) {
			likes = new HashMap<>();
		}
		likes.put(username, true);

		numLikes++;
	}

	public void removeLikes(String username) {
		if (likes != null && likes.containsKey(username)) {
			likes.remove(username);
			numLikes--;
		}
	}

	public String getInfo() {
		return "Comment: " + content + " | " + numLikes + " likes | " + numReplies + " replies" + " | " + "Owner: " + username;
	}
}
