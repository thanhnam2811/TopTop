package com.toptop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.toptop.MainActivity;
import com.toptop.WatchProfileActivity;
import com.toptop.WatchVideoActivity;
import com.toptop.models.Comment;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.CommentFirebase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("SimpleDateFormat")
public class MyUtil {
	public static final String
			STATUS_BAR_LIGHT_MODE = "status_bar_light_mode",
			STATUS_BAR_DARK_MODE = "status_bar_dark_mode";

	// get time ago
	public static String getTimeAgo(Date date) {
		Date now = new Date();
		long diff = now.getTime() - date.getTime();
		// if less than 1 minute
		if (diff < TimeUnit.MINUTES.toMillis(1)) {
			return "Vừa xong";
		}
		// if less than 1 hour
		else if (diff < TimeUnit.HOURS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toMinutes(diff) + " phút trước";
		}
		// if less than 1 day
		else if (diff < TimeUnit.DAYS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toHours(diff) + " giờ trước";
		}
		// if less than 1 week
		else if (diff < TimeUnit.DAYS.toMillis(7)) {
			return TimeUnit.MILLISECONDS.toDays(diff) + " ngày trước";
		}
		// if less than 1 month
		else if (diff < TimeUnit.DAYS.toMillis(30)) {
			return TimeUnit.MILLISECONDS.toDays(diff) / 7 + " tuần trước";
		}
		// if less than 1 year
		else if (diff < TimeUnit.DAYS.toMillis(365)) {
			return TimeUnit.MILLISECONDS.toDays(diff) / 30 + " tháng trước";
		}
		// if more than 1 year
		else {
			return TimeUnit.MILLISECONDS.toDays(diff) / 365 + " năm trước";
		}
	}

	// get number to text if large
	@SuppressLint("DefaultLocale")
	public static String getNumberToText(long number) {
		if (number > 999 && number < 1000000) {
			return String.format("%.2f", number / 1000.0) + "K";
		} else if (number > 999999) {
			return String.format("%.2f", number / 1000000.0) + "M";
		} else if (number > 9999999) {
			return String.format("%.2f", number / 1000000000.0) + "B";
		} else {
			return String.valueOf(number);
		}
	}

	// get time ago
	public static String getTimeAgo(String dateString) {
		Date date = stringToDateTime(dateString);
		if (date != null) {
			return getTimeAgo(date);
		} else {
			return "Date is null";
		}
	}

	public static void downloadFile(String url, File outputFile) {
		// Log
		System.out.println("Downloading file from " + url);
		try {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			int contentLength = conn.getContentLength();

			DataInputStream stream = new DataInputStream(u.openStream());

			byte[] buffer = new byte[contentLength];
			stream.readFully(buffer);
			stream.close();

			DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
			fos.write(buffer);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getBitmapFromURL(String linkImage) {
		System.out.println("linkImage: " + linkImage);
		try {
			URL url = new URL(linkImage);
			URLConnection connection = url.openConnection();
			connection.connect();
			return BitmapFactory.decodeStream(connection.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Set status bar color mode
	public static void setStatusBarColor(String mode, Activity activity) {
		if (mode.equals(STATUS_BAR_DARK_MODE)) {
			activity.getWindow().setStatusBarColor(Color.BLACK);
			activity.getWindow().getDecorView().setSystemUiVisibility(0);
		} else if (mode.equals(STATUS_BAR_LIGHT_MODE)) {
			activity.getWindow().setStatusBarColor(Color.WHITE);
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	public static void setLightStatusBar(Activity activity) {
		setStatusBarColor(STATUS_BAR_LIGHT_MODE, activity);
	}

	public static void setDarkStatusBar(Activity activity) {
		setStatusBarColor(STATUS_BAR_DARK_MODE, activity);
	}

	public static void goToVideo(Activity activity, String videoId) {
		Intent intent = new Intent(activity, WatchVideoActivity.class);
		intent.putExtra(Video.VIDEO_ID, videoId);
		activity.startActivity(intent);
	}

	public static void goToComment(Activity activity, String commentId) {
		CommentFirebase.getCommentByCommentIdOneTime(commentId,
				comment -> {
					Intent intent = new Intent(activity, WatchVideoActivity.class);
					intent.putExtra(Video.VIDEO_ID, comment.getVideoId());
					intent.putExtra(Comment.COMMENT_ID, comment.getCommentId());
					activity.startActivity(intent);
				}, databaseError -> {
					Toast.makeText(activity, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
				}
		);
	}

	public static void goToUser(Activity activity, String username) {
		MainActivity mainActivity = (MainActivity) activity;
		if (MainActivity.getCurrentUser().getUsername().equals(username)) {
			mainActivity.changeNavItem(3);
		} else {
			Intent intent = new Intent(activity, WatchProfileActivity.class);
			Log.d("forward to profile ", username);
			intent.putExtra(User.TAG, username);
			activity.startActivity(intent);
		}
	}

	// Date to string

	// String to date

	// Format date
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss";


	// Date to string
	public static String dateToStringFormat(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String dateTimeToString(Date date) {
		return dateToStringFormat(date, DD_MM_YYYY_HH_MM_SS);
	}

	public static String dateToString(Date date) {
		return dateToStringFormat(date, YYYY_MM_DD);
	}

	// String to date
	public static Date stringToDateFormat(String dateString, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date stringToDateTime(String date) {
		return stringToDateFormat(date, DD_MM_YYYY_HH_MM_SS);
	}

	public static Date stringToDate(String dateString) {
		return stringToDateFormat(dateString, YYYY_MM_DD);
	}

	public static boolean isInMonth(String dateString) {
		String now = dateToString(new Date());
		if (isInYear(dateString)) {
			String month = dateString.substring(5, 7);
			String nowMonth = now.substring(5, 7);
			return month.equals(nowMonth);
		}
		return false;
	}

	public static boolean isInYear(String dateString) {
		String now = dateToString(new Date());
		String year = dateString.substring(0, 4);
		String nowYear = now.substring(0, 4);
		return year.equals(nowYear);
	}
}
