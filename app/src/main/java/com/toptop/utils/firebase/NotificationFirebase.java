package com.toptop.utils.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationFirebase {
	// Tag
	public static final String TAG = "NotificationFirebase";
	public static final DatabaseReference notificationRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_NOTIFICATIONS);

	// Callback
	public interface NotificationCallback {
		void onCallback(Notification notification);
	}

	public interface ListNotificationCallback {
		void onCallback(List<Notification> notifications);
	}

	public interface FailureCallback {
		void onCallback(DatabaseError error);
	}

	// Add notification to firebase
	public static void addNotification(Notification notification) {
		if (notification.getNotificationId() == null)
			notification.setNotificationId(notificationRef.push().getKey());
		notificationRef.child(notification.getNotificationId()).setValue(notification);
		Log.i(TAG, "addNotification: " + notification.getNotificationId() + " added to firebase");
	}

	// Update notification to firebase
	public static void updateNotification(Notification notification) {
		notificationRef.child(notification.getNotificationId()).setValue(notification);
		Log.i(TAG, "updateNotification: " + notification.getNotificationId() + " updated to firebase");
	}

	// Delete notification to firebase
	public static void deleteNotification(Notification notification) {
		notificationRef.child(notification.getNotificationId()).removeValue();
		Log.i(TAG, "deleteNotification: " + notification.getNotificationId() + " deleted from firebase");
	}
	//delete notification by redirectTo
	public static void deleteNotificationByRedirectTo(String redirectTo) {
		notificationRef.orderByChild("redirectTo").equalTo(redirectTo).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					postSnapshot.getRef().removeValue();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	// Get notification by username
	public static void getNotificationByUsername(String username, final ListNotificationCallback listNotificationCallback, final FailureCallback failureCallback) {
		notificationRef.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				List<Notification> notifications = new ArrayList<>();
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					Notification notification = new Notification(postSnapshot);
					notifications.add(notification);
				}
				listNotificationCallback.onCallback(notifications);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				failureCallback.onCallback(databaseError);
			}
		});
	}
}
