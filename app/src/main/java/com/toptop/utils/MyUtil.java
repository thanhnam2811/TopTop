package com.toptop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.toptop.WatchProfileActivity;
import com.toptop.WatchVideoActivity;
import com.toptop.models.User;
import com.toptop.models.Video;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("SimpleDateFormat")
public class MyUtil {
	public static final String
			STATUS_BAR_LIGHT_MODE = "status_bar_light_mode",
			STATUS_BAR_DARK_MODE = "status_bar_dark_mode";

	public static Date getDateFromFormattedDateString(String date) {
		String pattern = "dd-MM-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			return simpleDateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getFormattedDateStringFromDate(Date date) {
		String pattern = "dd-MM-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

	// get time ago
	public static String getTimeAgo(Date date) {
		Date now = new Date();
		long diff = now.getTime() - date.getTime();
		// if less than 1 minute
		if (diff < TimeUnit.MINUTES.toMillis(1)) {
			return "just now";
		}
		// if less than 1 hour
		else if (diff < TimeUnit.HOURS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toMinutes(diff) + "m";
		}
		// if less than 1 day
		else if (diff < TimeUnit.DAYS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toHours(diff) + "h";
		}
		// if less than 1 month
		else if (diff < TimeUnit.DAYS.toMillis(30)) {
			return TimeUnit.MILLISECONDS.toDays(diff) + "d";
		}
		// if less than 1 year
		else if (diff < TimeUnit.DAYS.toMillis(365)) {
			return TimeUnit.MILLISECONDS.toDays(diff) / 7 + "w";
		}
		// if more than 1 year
		else {
			return TimeUnit.MILLISECONDS.toDays(diff) / 365 + "y";
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
		Date date = getDateFromFormattedDateString(dateString);
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

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date now = new Date();
		return sdf.format(now);
	}


	public static void goToVideo(Activity activity, Video video) {
		Intent intent = new Intent(activity, WatchVideoActivity.class);
		intent.putExtra(Video.TAG, video);
		activity.startActivity(intent);
	}
	public static void goToUser(Activity activity, String username) {
		Intent intent = new Intent(activity, WatchProfileActivity.class);
		Log.d("forward to profile ", username);
		intent.putExtra(User.TAG, username);
		activity.startActivity(intent);
	}
}
