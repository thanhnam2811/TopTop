package com.toptop.models;

import com.google.firebase.database.DataSnapshot;
import com.toptop.utils.MyUtil;

import java.util.Date;
import java.util.HashMap;

public class Notification {
    private String notificationId, username, content, type, time, redirectTo;
    private Boolean seen;

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
    public Notification(String notificationId, String username, String content, String type, Date time, String redirectTo, Boolean seen) {
        this.notificationId = notificationId;
        this.username = username;
        this.content = content;
        this.type = type;
        this.time = MyUtil.getFormattedDateStringFromDate(time);
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
