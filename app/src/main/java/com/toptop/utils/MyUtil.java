package com.toptop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

	// check valid dateString
	public static boolean isValidDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		try {
			sdf.parse(dateString);
		} catch (ParseException e) {
			return false;
		}
		return true;
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
		return getTimeAgo(date);
	}
}
