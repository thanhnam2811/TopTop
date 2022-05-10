package com.toptop.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.Query;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.UserFirebase;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
	// Tag
	public static final String TAG = "User";

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

	@SuppressWarnings("unchecked")
	public User(DataSnapshot dataSnapshot) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
		if (data != null) {
			this.username = (String) data.get("username");
			this.password = (String) data.get("password");
			this.fullname = (String) data.get("fullname");
			this.phoneNumber = (String) data.get("phoneNumber");
			this.email = (String) data.get("email");
			this.avatar = (String) data.get("avatar");

			if (data.get("numFollowers") != null)
				this.numFollowers = (Long) data.get("numFollowers");
			else
				this.numFollowers = 0L;
			if (data.get("numFollowing") != null)
				this.numFollowing = (Long) data.get("numFollowing");
			else
				this.numFollowing = 0L;
			if (data.get("numLikes") != null)
				this.numLikes = (Long) data.get("numLikes");
			else
				this.numLikes = 0L;
			if (data.get("followings") != null)
				this.followings = (HashMap<String, Boolean>) data.get("followings");
			else
				this.followings = new HashMap<>();
			if (data.get("followers") != null)
				this.followers = (HashMap<String, Boolean>) data.get("followers");
			else
				this.followers = new HashMap<>();
		}

		// Prepare data
		boolean hasChanged = false;
		if (numFollowing != followings.size()) {
			this.numFollowing = (long) followings.size();
			hasChanged = true;
		}
		if (numFollowers != followers.size()) {
			this.numFollowers = (long) followers.size();
			hasChanged = true;
		}
		if (numLikes == null) {
			numLikes = 0L;
			hasChanged = true;
		}
		if (hasChanged) {
			// TODO: delete log in User class
			Log.w(TAG, "User: " + username + " has changed");
			UserFirebase.updateUser(this);
		}

		// Update numLikes if changed
		Query query = FirebaseUtil.getVideosByUsername(username);
		query.get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				long newNumLikes = 0;
				DataSnapshot snapshot = task.getResult();
				if (snapshot.exists())
					for (DataSnapshot child : snapshot.getChildren()) {
						Video video = new Video(child);
						if (video.getNumLikes() != null)
							newNumLikes = newNumLikes + video.getNumLikes();
					}
				if (newNumLikes != numLikes) {
					this.numLikes = newNumLikes;
					UserFirebase.updateUser(this);
				}
			}
		});
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (this.username == null) this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPhoneNumber() {
		return phoneNumber == null ? "" : phoneNumber;
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

	public void setPassword(String password) {
		if (this.password == null) this.password = password;
	}

	@Exclude
	public boolean isFollowing(String username) {
		return followings != null && followings.containsKey(username);
	}

	public void follow(String username) {
		// Add to followings
		if (followings == null) followings = new HashMap<>();
		followings.put(username, true);

		// Update numFollowing
		numFollowing++;
	}

	public void unfollow(String username) {
		// Remove from followings
		if (followings != null) followings.remove(username);

		// Update numFollowing
		numFollowing--;
	}

	public void addFollower(String username) {
		// Add to followers
		if (followers == null) followers = new HashMap<>();
		followers.put(username, true);

		// Update numFollowers
		numFollowers++;
	}

	public void removeFollower(String username) {
		// Remove from followers
		if (followers != null) followers.remove(username);

		// Update numFollowers
		numFollowers--;
	}

	public boolean hasChangedInfo(User user) {
		return !fullname.equals(user.fullname) ||
				!phoneNumber.equals(user.phoneNumber) ||
				!email.equals(user.email) ||
				!avatar.equals(user.avatar) ||
				!numLikes.equals(user.numLikes) ||
				!numFollowers.equals(user.numFollowers) ||
				!numFollowing.equals(user.numFollowing);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			return username.equals(user.username);
		}
		return false;
	}
}
