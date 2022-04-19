package com.toptop.models;

import java.util.Date;

public class Video {
    private String videoId, username, content, linkVideo;
    private Long totalLikes, totalComments, totalViews;
    private Date date;
//  image description video
    private int imageVideo;

    public Video(String videoId, String username, String content, int imageVideo, Date date) {
        this.videoId = videoId;
        this.username = username;
        this.content = content;
        this.imageVideo = imageVideo;
        this.date = date;
    }

    public Video(String videoId, String username, String content, String linkVideo, Long totalLikes, Long totalComments, Long totalViews, Date date) {
        this.videoId = videoId;
        this.username = username;
        this.content = content;
        this.linkVideo = linkVideo;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
        this.totalViews = totalViews;
        this.date = date;
    }

    public Video(String videoId, String username, String content, String linkVideo, int totalLikes, int totalComments, int totalViews, Date date) {
        this.videoId = videoId;
        this.username = username;
        this.content = content;
        this.linkVideo = linkVideo;
        this.totalLikes = (long) totalLikes;
        this.totalComments = (long) totalComments;
        this.totalViews = (long) totalViews;
        this.date = date;
    }

    public int getImageVideo() {
        return imageVideo;
    }

    public void setImageVideo(int imageVideo) {
        this.imageVideo = imageVideo;
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

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
