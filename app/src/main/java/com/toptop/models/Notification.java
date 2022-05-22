package com.toptop.models;

import com.google.firebase.database.DataSnapshot;
import com.toptop.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Notification {
    public static final String TYPE_FOLLOW = "follow";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_LIKE = "like";
    private String notificationId, username, content, type, time = MyUtil.getCurrentTime(), redirectTo;
    private Boolean seen = false;

    public Notification() {
    }

    public Notification(String notificationId, String username, String content, String type, String time, String redirectTo, Boolean seen) {
        this.notificationId = notificationId;
        this.username = username;
        this.content = content;
        this.type = type;
        this.time = time;
        this.redirectTo = redirectTo;
        this.seen = seen;
    }

    public Notification(DataSnapshot dataSnapshot) {
        HashMap<String, Object> data = (HashMap<String, Object>) dataSnapshot.getValue();
        this.notificationId = dataSnapshot.getKey();
        this.username = (String) data.get("username");
        this.content = (String) data.get("content");
        this.type = (String) data.get("type");
        this.time = (String) data.get("time");
        this.redirectTo = (String) data.get("redirectTo");
        this.seen = (Boolean) data.get("seen");
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    //sort by time
    public static void sortByTimeNewest(List<Notification> notifications) {
        notifications.sort((o1, o2) -> {
            Date time1 = MyUtil.getDateFromFormattedDateString(o1.getTime());
            Date time2 = MyUtil.getDateFromFormattedDateString(o2.getTime());
            if (time1 != null && time2 != null) {
                return time2.compareTo(time1);
            }
            return 0;
        });
    }

    //set seen to true
    public static void setSeen(List<Notification> notifications) {
        for (Notification notification : notifications) {
            notification.setSeen(true);
        }
    }
    // get notification unseen
    public static ArrayList<Notification> getNotificationUnseen(ArrayList<Notification> notifications) {
        ArrayList<Notification> notificationUnseen = new ArrayList<>();
        for (Notification notification : notifications) {
            if (!notification.getSeen()) {
                notificationUnseen.add(notification);
            }
        }
        return notificationUnseen;
    }

    //counnt notifications not seen
    public static int countNotSeen(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.getSeen()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "Notification {" +
                "notificationId=" + notificationId +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", redirectTo='" + redirectTo + '\'' +
                ", seen=" + seen +
                '}';
    }
}
