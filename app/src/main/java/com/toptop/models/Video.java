package com.toptop.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Video {
	private static final String TAG = "Video Model";
	private String video_id, username, content, link_video;
	private Long total_likes, total_comments, total_views;
	private String date_uploaded;
	private HashMap<String, Boolean> likes;
	private HashMap<String, Boolean> comments;

	public Video(String video_id, String username, String content, String link_video, Long total_likes, Long total_comments, Long total_views, String date_uploaded) {
		this.video_id = video_id;
		this.username = username;
		this.content = content;
		this.link_video = link_video;
		this.total_likes = total_likes;
		this.total_comments = total_comments;
		this.total_views = total_views;
		this.date_uploaded = date_uploaded;
	}

	public Video(DataSnapshot dataSnapshot) {
		Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
		this.video_id = dataSnapshot.getKey();
		this.username = map.get("username").toString();
		this.content = map.get("content").toString();
		this.link_video = map.get("link_video").toString();
		this.total_likes = (Long) map.get("total_likes");
		this.total_comments = (Long) map.get("total_comments");
		this.total_views = (Long) map.get("total_views");
		this.date_uploaded = map.get("date_uploaded").toString();
		this.likes = (HashMap<String, Boolean>) map.get("likes");
		this.comments = (HashMap<String, Boolean>) map.get("comments");
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
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
		return link_video;
	}

	public void setLinkVideo(String link_video) {
		this.link_video = link_video;
	}

	public Long getTotalLikes() {
		return total_likes;
	}

	public void setTotalLikes(Long total_likes) {
		this.total_likes = total_likes;
	}

	public Long getTotalComments() {
		return total_comments;
	}

	public void setTotalComments(Long total_comments) {
		this.total_comments = total_comments;
	}

	public Long getTotalViews() {
		return total_views;
	}

	public void setTotalViews(Long total_views) {
		this.total_views = total_views;
	}

	public String getDate() {
		return date_uploaded;
	}

	public void setDate(String date_uploaded) {
		this.date_uploaded = date_uploaded;
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

	public boolean existInVideos(List<Video> videos) {
		for (Video v : videos)
			if (v.getVideo_id().equals(video_id))
				return true;
		return false;
	}
}
