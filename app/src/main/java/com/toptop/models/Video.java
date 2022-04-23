package com.toptop.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class Video {
	private String videoId, preview, username, content, linkVideo;
	private Long numLikes, numComments, numViews;
	private String dateUploaded;
	private HashMap<String, Boolean> likes;
	private HashMap<String, Boolean> comments;

	public Video() {
		likes = new HashMap<String, Boolean>();
		comments = new HashMap<String, Boolean>();
	}
	public Video(String videoId, String preview, String username,
	             String content, String linkVideo, Long numLikes,
	             Long numComments, Long numViews, String dateUploaded){
		this.videoId = videoId;
		this.preview = preview;
		this.username = username;
		this.content = content;
		this.linkVideo = linkVideo;
		this.numLikes = numLikes;
		this.numComments = numComments;
		this.numViews = numViews;
		this.dateUploaded = dateUploaded;
	}

	public Video(String videoId, String preview, String username,
	             String content, String linkVideo, Long numLikes,
	             Long numComments, Long numViews, String dateUploaded,
	             HashMap<String, Boolean> likes, HashMap<String, Boolean> comments) {
		this.videoId = videoId;
		this.preview = preview;
		this.username = username;
		this.content = content;
		this.linkVideo = linkVideo;
		this.numLikes = numLikes;
		this.numComments = numComments;
		this.numViews = numViews;
		this.dateUploaded = dateUploaded;
		this.likes = likes;
		this.comments = comments;
	}

	public Video(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		this.videoId = dataSnapshot.getKey();
		this.preview = (String) data.get("preview");
		this.username = (String) data.get("username");
		this.content = (String) data.get("content");
		this.linkVideo = (String) data.get("linkVideo");
		this.numLikes = (Long) data.get("numLikes");
		this.numComments = (Long) data.get("numComments");
		this.numViews = (Long) data.get("numViews");
		this.dateUploaded = (String) data.get("dateUploaded");
		this.likes = (HashMap<String, Boolean>) data.get("likes");
		this.comments = (HashMap<String, Boolean>) data.get("comments");
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
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
				", preview='" + preview + '\'' +
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
}
