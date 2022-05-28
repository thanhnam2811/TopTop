package com.toptop.service;

import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.WatchProfileActivity;
import com.toptop.WatchVideoActivity;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {

	private static final String TAG = "NotificationService";
	private static final String KEY_TEXT_REPLY = "key_text_reply";
	private static final int NOTIFICATION_COMMENT_ID = 1;
	private static final int NOTIFICATION_LIKE_ID = 2;
	private static final int NOTIFICATION_FOLLOW_ID = 3;
	private static final String COMMENT_NOTIFICATION = "comment_notification";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service onStartCommand");

		if (MainActivity.getCurrentUser() != null) {
			notificationUnseen(MainActivity.getCurrentUser());
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			startMyOwnForeground();
		else
			startForeground(1, new Notification());

		return START_NOT_STICKY;
	}

	private void startMyOwnForeground() {
		String NOTIFICATION_CHANNEL_ID = "com.toptop.service.notification";
		String channelName = "My Background Service";
		NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;
		manager.createNotificationChannel(chan);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		Notification notification = notificationBuilder.setOngoing(true)
				.setSmallIcon(R.drawable.logo_toptop)
				.setContentTitle("App is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();

		startForeground(2, notification);
	}

	private void notificationUnseen(User user) {
		NotificationFirebase.getNotificationByUsername(user.getUsername(), notifications -> {
			for (com.toptop.models.Notification notification : notifications) {
				if (notification.getSeen() == false) {
					sendNotification(notification);
					notification.setSeen(true);
					NotificationFirebase.updateNotification(notification);
				}
			}
		}, error -> Log.e(TAG, "Failed to get notification"));
	}

	private void sendNotification(com.toptop.models.Notification notification) {
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		create channel notification
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			@SuppressLint("WrongConstant")
			NotificationChannel notificationChannel = new NotificationChannel("my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX);
			notificationChannel.setDescription("description");
			notificationChannel.setName("Channel Name");
			assert notificationManager != null;
			notificationManager.createNotificationChannel(notificationChannel);
		}

//		create notification
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "my_notification");
		if (notification.getType().equals(com.toptop.models.Notification.TYPE_LIKE)) {
			//	notification for like
			VideoFirebase.getVideoByVideoIdOneTime(notification.getRedirectTo(), value -> {
						if (value != null) {
							Log.d(TAG, "onCallback Video: " + value);
							// event click notification to open activity
							Intent intent = new Intent(this, WatchVideoActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
							intent.putExtra(Video.TAG, value);

							PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
							Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							NotificationCompat.Builder notificationBuilderVideo = new NotificationCompat.Builder(this)
									.setSmallIcon(R.drawable.logo_toptop)
									.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_liked))
									.setContentTitle("Bạn có một lượt thích mới")
									.setContentText(notification.getContent())
									.setAutoCancel(true)
									.setSound(soundUri)
									.setContentIntent(pendingIntent)
									.setVisibility(VISIBILITY_PUBLIC)
									.setDefaults(android.app.Notification.DEFAULT_ALL)
									.setOnlyAlertOnce(true)
									.setChannelId("my_notification")
									.setColor(Color.parseColor("#3F5996"));
							assert notificationManager != null;
							notificationManager.notify(NOTIFICATION_LIKE_ID, notificationBuilderVideo.build());
						}
					}, error -> {
						Log.e(TAG, "sendNotification: " + error.getMessage());
					}
			);
		} else if (notification.getType().equals(com.toptop.models.Notification.TYPE_FOLLOW)) {
			// event click notification to open activity
			Intent intent = new Intent(this, WatchProfileActivity.class);
			intent.putExtra(User.TAG, notification.getRedirectTo());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			//notification for follow
			notificationBuilder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.logo_toptop)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_following))
					.setContentTitle("Bạn có một người theo dõi mới")
					.setContentText(notification.getContent())
					.setAutoCancel(true)
					.setSound(soundUri)
					.setContentIntent(pendingIntent)
					.setDefaults(android.app.Notification.DEFAULT_ALL)
					.setOnlyAlertOnce(true)
					.setVisibility(VISIBILITY_PUBLIC)
					.setChannelId("my_notification")
					.setColor(Color.parseColor("#3F5996"));
			//.setProgress(100,50,false);
			assert notificationManager != null;
			notificationManager.notify(NOTIFICATION_FOLLOW_ID, notificationBuilder.build());
		} else {
			// event click notification to open activity
			Intent intent = new Intent(this, MainActivity.class);
			System.out.println("notification.getRedirectTo()" + notification.getRedirectTo());
			intent.putExtra(COMMENT_NOTIFICATION, notification.getRedirectTo());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//			notification for comment
			System.out.println("notification.getType() = " + notification.getType());
			//Create notification builder
			notificationBuilder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.logo_toptop)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_comment))
					.setContentTitle("Bạn có một bình luận mới")
					.setContentText(notification.getContent())
					.setStyle(new NotificationCompat.BigTextStyle()
							.bigText(notification.getContent()))
					.setPriority(NotificationCompat.PRIORITY_DEFAULT)
					.setAutoCancel(true)
					.setSound(soundUri)
					.setContentIntent(pendingIntent)
					.setVisibility(VISIBILITY_PUBLIC)
					.setDefaults(android.app.Notification.DEFAULT_ALL)
					.setOnlyAlertOnce(true)
					.setChannelId("my_notification")
					.setColor(Color.parseColor("#3F5996"));

			String replyLabel = "Nhập để trả lời...";
			//Initialise RemoteInput
			RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build();
			Intent resultIntent = new Intent(this, MainActivity.class);
			resultIntent.putExtra("notificationId", notification.getNotificationId());
			resultIntent.putExtra(COMMENT_NOTIFICATION, notification.getRedirectTo());

			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent resultPendingIntent = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
				resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_MUTABLE);
			} else {
				resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			}

			NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_comment, "REPLY", resultPendingIntent)
					.addRemoteInput(remoteInput)
					.setAllowGeneratedReplies(true)
					.build();

			notificationBuilder.addAction(replyAction);

			//.setProgress(100,50,false);
			assert notificationManager != null;
			notificationManager.notify(NOTIFICATION_COMMENT_ID, notificationBuilder.build());
		}

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
