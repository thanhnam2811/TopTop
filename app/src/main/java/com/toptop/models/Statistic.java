package com.toptop.models;

import com.toptop.utils.MyUtil;

import java.util.Date;

public class Statistic {
	private String date;

	private Long numberOfNewUsers = 0L;
	private Long numberOfNewViews = 0L;
	private Long numberOfNewComments = 0L;
	private Long numberOfNewLikes = 0L;
	private Long numberOfNewReports = 0L;
	private Long numberOfNewVideos = 0L;

	public Statistic() {
		this.date = MyUtil.dateToString(new Date());
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getNumberOfNewUsers() {
		return numberOfNewUsers;
	}

	public void setNumberOfNewUsers(Long numberOfNewUsers) {
		this.numberOfNewUsers = numberOfNewUsers;
	}

	public Long getNumberOfNewViews() {
		return numberOfNewViews;
	}

	public void setNumberOfNewViews(Long numberOfNewViews) {
		this.numberOfNewViews = numberOfNewViews;
	}

	public Long getNumberOfNewComments() {
		return numberOfNewComments;
	}

	public void setNumberOfNewComments(Long numberOfNewComments) {
		this.numberOfNewComments = numberOfNewComments;
	}

	public Long getNumberOfNewLikes() {
		return numberOfNewLikes;
	}

	public void setNumberOfNewLikes(Long numberOfNewLikes) {
		this.numberOfNewLikes = numberOfNewLikes;
	}

	public Long getNumberOfNewReports() {
		return numberOfNewReports;
	}

	public void setNumberOfNewReports(Long numberOfNewReports) {
		this.numberOfNewReports = numberOfNewReports;
	}

	public Long getNumberOfNewVideos() {
		return numberOfNewVideos;
	}

	public void setNumberOfNewVideos(Long numberOfNewVideos) {
		this.numberOfNewVideos = numberOfNewVideos;
	}
}
