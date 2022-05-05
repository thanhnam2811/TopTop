package com.toptop.utils.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.toptop.models.Notification;

public class NotificationFirebase {
	// Tag
	public static final String TAG = "NotificationFirebase";
	public static final DatabaseReference notificationRef = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_NOTIFICATIONS);

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
}
