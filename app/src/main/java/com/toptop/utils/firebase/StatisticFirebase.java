package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.models.Statistic;
import com.toptop.utils.MyUtil;

import java.util.Date;

public class StatisticFirebase {
	// Tag
	public static final String TAG = "StatisticFirebase";
	// Database Reference
	public static final DatabaseReference statisticRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_STATISTICS);

	// Update
	public static void updateStatistic(Statistic statistic) {
		statisticRef.child(statistic.getDate()).setValue(statistic);
		Log.i(TAG, "updateStatistic: " + statistic);
	}

	// Add view
	public static void addView() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			Statistic statistic = null;
			if (dataSnapshot.exists()) {
				statistic = dataSnapshot.getValue(Statistic.class);
			}
			if (statistic == null) {
				statistic = new Statistic();
			}
			statistic.setNumberOfNewViews(statistic.getNumberOfNewViews() + 1);
			updateStatistic(statistic);
		});
	}

	// Add new user
	public static void addNewUser() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			if (dataSnapshot.exists()) {
				Statistic statistic = dataSnapshot.getValue(Statistic.class);
				if (statistic == null) {
					statistic = new Statistic();
				}
				statistic.setNumberOfNewUsers(statistic.getNumberOfNewUsers() + 1);
				updateStatistic(statistic);
			}
		});
	}

	// Add new comment
	public static void addNewComment() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			if (dataSnapshot.exists()) {
				Statistic statistic = dataSnapshot.getValue(Statistic.class);
				if (statistic == null) {
					statistic = new Statistic();
				}
				statistic.setNumberOfNewComments(statistic.getNumberOfNewComments() + 1);
				updateStatistic(statistic);
			}
		});
	}

	// Add new like
	public static void addNewLike() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			if (dataSnapshot.exists()) {
				Statistic statistic = dataSnapshot.getValue(Statistic.class);
				if (statistic == null) {
					statistic = new Statistic();
				}
				statistic.setNumberOfNewLikes(statistic.getNumberOfNewLikes() + 1);
				updateStatistic(statistic);
			}
		});
	}

	// Add new report
	public static void addNewReport() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			if (dataSnapshot.exists()) {
				Statistic statistic = dataSnapshot.getValue(Statistic.class);
				if (statistic == null) {
					statistic = new Statistic();
				}
				statistic.setNumberOfNewReports(statistic.getNumberOfNewReports() + 1);
				updateStatistic(statistic);
			}
		});
	}

	// Add new video
	public static void addNewVideo() {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).get().addOnSuccessListener(dataSnapshot -> {
			if (dataSnapshot.exists()) {
				Statistic statistic = dataSnapshot.getValue(Statistic.class);
				if (statistic == null) {
					statistic = new Statistic();
				}
				statistic.setNumberOfNewVideos(statistic.getNumberOfNewVideos() + 1);
				updateStatistic(statistic);
			}
		});
	}
}
