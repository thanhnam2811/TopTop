package com.toptop.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class User {
	private String username, password, fullname, phoneNumber, email, avatar;
	private Long numFollowers, numFollowing, numLikes;
	private HashMap<String, Boolean> followings, followers;

	public User() {

	}

	public User(String username, String password, String fullname,
	            String phoneNumber, String email) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public User(String username, String password, String fullname,
	            String phoneNumber, String email, String avatar,
	            Long numFollowers, Long numFollowing, Long numLikes) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.avatar = avatar;
		this.numFollowers = numFollowers;
		this.numFollowing = numFollowing;
		this.numLikes = numLikes;
		this.followings = new HashMap<>();
		this.followers = new HashMap<>();
	}

	public User(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		this.username = (String) data.get("username");
		this.password = (String) data.get("password");
		this.fullname = (String) data.get("fullname");
		this.phoneNumber = (String) data.get("phoneNumber");
		this.email = (String) data.get("email");
		this.avatar = (String) data.get("avatar");
		this.numFollowers = (Long) data.get("numFollowers");
		this.numFollowing = (Long) data.get("numFollowing");
		this.numLikes = (Long) data.get("numLikes");
		followings = (HashMap<String, Boolean>) data.get("followings");
		followers = (HashMap<String, Boolean>) data.get("followers");
		/*
            if (numFollowing != followings.size()) this.numFollowing = (long) followings.size();
            if (numFollowers != followers.size()) this.numFollowers = (long) followers.size();
        */
	}

	public String getUsername() {
		return username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Long getNumFollowers() {
		return numFollowers;
	}

	public void setNumFollowers(Long numFollowers) {
		this.numFollowers = numFollowers;
	}

	public Long getNumFollowing() {
		return numFollowing;
	}

	public void setNumFollowing(Long numFollowing) {
		this.numFollowing = numFollowing;
	}

	public Long getNumLikes() {
		return numLikes;
	}

	public void setNumLikes(Long numLikes) {
		this.numLikes = numLikes;
	}

	public HashMap<String, Boolean> getFollowings() {
		return followings;
	}

	public void setFollowings(HashMap<String, Boolean> followings) {
		this.followings = followings;
	}

	public HashMap<String, Boolean> getFollowers() {
		return followers;
	}

	public void setFollowers(HashMap<String, Boolean> followers) {
		this.followers = followers;
	}

	@Override
	public String toString() {
		return "User {" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", fullname='" + fullname + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", email='" + email + '\'' +
				", avatar='" + avatar + '\'' +
				", numFollowers=" + numFollowers +
				", numFollowing=" + numFollowing +
				", numLikes=" + numLikes +
				", followings=" + followings +
				", followers=" + followers +
				'}';
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		if (this.username == null) this.username = username;
	}

	public void setPassword(String password) {
		if (this.password == null) this.password = password;
	}
}
