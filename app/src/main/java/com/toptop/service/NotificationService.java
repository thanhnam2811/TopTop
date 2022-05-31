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
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.WatchProfileActivity;
import com.toptop.WatchVideoActivity;
import com.toptop.broadcastReceiver.NotificationReceiver;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.VideoFirebase;

public class NotificationService extends Service {

	private static final String TAG = "NotificationService";
	private static final String KEY_TEXT_REPLY = "key_text_reply";
	public static final int NOTIFICATION_COMMENT_ID = 1;
	private static final int NOTIFICATION_LIKE_ID = 2;
	private static final int NOTIFICATION_FOLLOW_ID = 3;
	private static final String COMMENT_NOTIFICATION = "comment_notification";

//
	public static final String NOTIFICATION_REPLY = "NotificationReply";
	public static final String CHANNNEL_ID = "SimplifiedCodingChannel";
	public static final String CHANNEL_NAME = "SimplifiedCodingChannel";
	public static final String CHANNEL_DESC = "This is a channel for Simplified Coding Notifications";

	public static final String KEY_INTENT_LIKE = "keyintentlike";
	public static final String KEY_INTENT_HELP = "keyintenthelp";
	public static final String KEY_INTENT_DELETE = "keyintentdelete";
	public static final String NOTIFICATION_ID = "notification_id";


	public static final int REQUEST_CODE_HELP = 101;
	public static final int REQUEST_CODE_DELETE = 200;
	public static final int REQUEST_CODE_LIKE = 300;

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

		startMyOwnForeground();

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
				.setSmallIcon(R.drawable.ic_logo)
				.setContentTitle("App is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();

		startForeground(999, notification);
	}

	private void notificationUnseen(User user) {
		NotificationFirebase.getNotificationByUsername(user.getUsername(), notifications -> {
			for (com.toptop.models.Notification notification : notifications) {
				if (!notification.getSeen()) {
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
		@SuppressLint("WrongConstant")
		NotificationChannel notificationChannel = new NotificationChannel("my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX);
		notificationChannel.setDescription("description");
		notificationChannel.setName("Channel Name");
		assert notificationManager != null;
		notificationManager.createNotificationChannel(notificationChannel);

//		create notification
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
		if (notification.getType().equals(com.toptop.models.Notification.TYPE_LIKE)) {
			//	notification for like
			VideoFirebase.getVideoByVideoIdOneTime(notification.getRedirectTo(), value -> {
						if (value != null) {
							Log.d(TAG, "onCallback Video: " + value);
							// event click notification to open activity
							Intent intent = new Intent(this, WatchVideoActivity.class);
							intent.putExtra(Video.TAG, value);
							intent.putExtra(Video.VIDEO_ID, value.getVideoId());
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);

							PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
							NotificationCompat.Builder notificationBuilderVideo = new NotificationCompat.Builder(this, notificationChannel.getId())
									.setSmallIcon(R.drawable.ic_logo)
									.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_liked))
									.setContentTitle("Bạn có một lượt thích mới")
									.setContentText(notification.getContent())
									.setAutoCancel(true)
									.setContentIntent(pendingIntent)
									.setVisibility(VISIBILITY_PUBLIC)
									.setDefaults(android.app.Notification.DEFAULT_ALL)
									.setOnlyAlertOnce(true)
									.setChannelId("my_notification")
									.setColor(Color.parseColor("#3F5996"));
							NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
							notificationManagerCompat.notify(NOTIFICATION_LIKE_ID, notificationBuilderVideo.build());
//							notificationManager.notify(NOTIFICATION_LIKE_ID, notificationBuilderVideo.build());
						}
					}, error -> {
						Log.e(TAG, "sendNotification: " + error.getMessage());
					}
			);
		} else if (notification.getType().equals(com.toptop.models.Notification.TYPE_FOLLOW))
		{
			// event click notification to open activity
			Intent intent = new Intent(this, WatchProfileActivity.class);
			Log.d(TAG, "sendNotification: " + notification.getRedirectTo());
			intent.putExtra(User.TAG, notification.getRedirectTo());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			//notification for follow
			notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId())
					.setSmallIcon(R.drawable.ic_logo)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_following))
					.setContentTitle("Bạn có một người theo dõi mới")
					.setContentText(notification.getContent())
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setDefaults(android.app.Notification.DEFAULT_ALL)
					.setOnlyAlertOnce(true)
					.setVisibility(VISIBILITY_PUBLIC)
					.setChannelId("my_notification")
					.setColor(Color.parseColor("#3F5996"));

			NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
			notificationManagerCompat.notify(NOTIFICATION_FOLLOW_ID, notificationBuilder.build());
//			notificationManager.notify(NOTIFICATION_FOLLOW_ID, notificationBuilder.build());
		} else {
			//Pending intent for a notification button LIKE
			PendingIntent likePendingIntent = PendingIntent.getBroadcast(
					this,
					REQUEST_CODE_LIKE,
					new Intent(this, NotificationReceiver.class)
							.putExtra(KEY_INTENT_LIKE, REQUEST_CODE_LIKE)
							.putExtra(COMMENT_NOTIFICATION, notification.getRedirectTo()),
					PendingIntent.FLAG_UPDATE_CURRENT
			);

			//Pending intent for a notification button REPLY
			PendingIntent helpPendingIntent = PendingIntent.getBroadcast(
					this,
					REQUEST_CODE_HELP,
					new Intent(this, NotificationReceiver.class)
							.putExtra(KEY_INTENT_HELP, REQUEST_CODE_HELP)
							.putExtra(COMMENT_NOTIFICATION, notification.getRedirectTo())
							.putExtra(NOTIFICATION_ID, notification.getNotificationId()),
					PendingIntent.FLAG_UPDATE_CURRENT
			);

			//We need this object for getting direct input from notification
			RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
					.setLabel("Nhập để trả lời...")
					.build();


			//For the remote input we need this action object
			NotificationCompat.Action action =
					new NotificationCompat.Action.Builder(R.drawable.ic_send_comment,
							"Reply Now...", helpPendingIntent)
							.addRemoteInput(remoteInput)
							.build();

			//Creating the notifiction builder object
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, notificationChannel.getId())
					.setSmallIcon(R.drawable.ic_logo)
					.setContentTitle("Bạn có một bình luận mới")
					.setContentText(notification.getContent())
					.setAutoCancel(true)
					.setContentIntent(helpPendingIntent)
					.addAction(action)
					.addAction(R.drawable.ic_liked, "Like", likePendingIntent)
					.addAction(R.drawable.ic_delete, "Delete", helpPendingIntent);


			//finally displaying the notification
			NotificationManager notificationManagerComment = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManagerComment.notify(NOTIFICATION_COMMENT_ID, mBuilder.build());
		}

	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
