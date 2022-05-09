package com.toptop.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
		// if less than 1 month
		else if (diff < TimeUnit.DAYS.toMillis(30)) {
			return TimeUnit.MILLISECONDS.toDays(diff) + " ngày trước";
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

    public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		return sdf.format(now);
    }
}
