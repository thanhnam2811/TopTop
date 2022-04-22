package com.toptop.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.toptop.utils.MyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment {
	private static final String TAG = "Comment Model";
	private String comment_id, username, video_id, content;
	private String time;
	private HashMap<String, Object> reply_to = new HashMap<>();

	public Comment() {
	}

	public Comment(DataSnapshot dataSnapshot) {
		HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
		this.comment_id = dataSnapshot.getKey();
		this.username = (String) map.get("username");
		this.video_id = (String) map.get("video_id");
		this.content = (String) map.get("content");
		this.time = (String) map.get("time");
		if (dataSnapshot.child("reply_to").getValue() != null)
			for (DataSnapshot snapshot : dataSnapshot.child("reply_to").getChildren())
				this.reply_to.put(snapshot.getKey(), snapshot.getValue());
	}

	public Comment(String comment_id, String username, String video_id, String content, Date time) {
		this.comment_id = comment_id;
		this.username = username;
		this.video_id = video_id;
		this.content = content;
		this.time = MyUtil.getFormattedDateStringFromDate(time);
	}

	public Comment(String comment_id, String username, String video_id, String content, String formattedTime) {
		this.comment_id = comment_id;
		this.username = username;
		this.video_id = video_id;
		this.content = content;
		if (MyUtil.isValidDate(formattedTime)) {
			this.time = formattedTime;
		} else {
			Log.i(TAG, "formattedTime is not valid, set to current time");
			this.time = MyUtil.getFormattedDateStringFromDate(new Date());
		}
	}

	public Comment(String comment_id, String username, String video_id, String content, Date time, HashMap<String, Object> reply_to) {
		this.comment_id = comment_id;
		this.username = username;
		this.video_id = video_id;
		this.content = content;
		this.time = MyUtil.getFormattedDateStringFromDate(time);
		this.reply_to = reply_to;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = MyUtil.getFormattedDateStringFromDate(time);
	}

	public HashMap<String, Object> getReply_to() {
		return reply_to;
	}

	public void setReply_to(HashMap<String, Object> reply_to) {
		this.reply_to = reply_to;
	}

	public void addReply_to(String key, Object value) {
		this.reply_to.put(key, value);
	}

	public void removeReply_to(String key) {
		this.reply_to.remove(key);
	}

	@Override
	public String toString() {
		return "Comment{" +
				"comment_id='" + comment_id + '\'' +
				", username='" + username + '\'' +
				", video_id='" + video_id + '\'' +
				", content='" + content + '\'' +
				", time=" + time +
				", reply_to=" + reply_to +
				'}';
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
}
