package com.toptop.utils.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.Statistic;
import com.toptop.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticFirebase {
	// Tag
	public static final String TAG = "StatisticFirebase";
	// Database Reference
	public static final DatabaseReference statisticRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_STATISTICS);

	// Callback
	public interface StatisticCallback {
		void onCallback(Statistic statistic);
	}
	public interface ListStatisticCallback {
		void onCallback(List<Statistic> statistics);
	}
	public interface FailureCallback {
		void onCallback(DatabaseError error);
	}

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

	// Get statistic in week
	public static Query getStatisticInWeek() {
		return statisticRef.orderByChild("date").limitToLast(7);
	}

	// Get statistic today
	public static void getStatisticToday(StatisticCallback callback, FailureCallback failureCallback) {
		String date = MyUtil.dateToString(new Date());
		statisticRef.child(date).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Statistic statistic = new Statistic();
				if (snapshot.exists()) {
					statistic = snapshot.getValue(Statistic.class);
				}
				callback.onCallback(statistic);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				failureCallback.onCallback(error);
			}
		});
	}

	public static void getStatistic(String period, ListStatisticCallback callback, FailureCallback failureCallback) {
		statisticRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				List<Statistic> statistics = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					statistics.add(dataSnapshot.getValue(Statistic.class));
				}
				if (period.equals(Statistic.MONTH)) {
					statistics.removeIf(statistic -> !MyUtil.isInMonth(statistic.getDate()));
				} else if (period.equals(Statistic.YEAR)) {
					statistics.removeIf(statistic -> !MyUtil.isInYear(statistic.getDate()));
				}
				callback.onCallback(statistics);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				failureCallback.onCallback(error);
			}
		});
	}
}
