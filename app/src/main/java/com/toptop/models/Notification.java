package com.toptop.models;

public class Notification {
//notification_id,avatar, username, content, time_notification, image_notification
    private int notification_id;
    private int avatar;
    private String username;
    private String content;
    private String time_notification;
    private int image_notification;

    public Notification(int notification_id, int avatar, String username, String content, String time_notification, int image_notification) {
        this.notification_id = notification_id;
        this.avatar = avatar;
        this.username = username;
        this.content = content;
        this.time_notification = time_notification;
        this.image_notification = image_notification;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
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

    public String getTime_notification() {
        return time_notification;
    }

    public void setTime_notification(String time_notification) {
        this.time_notification = time_notification;
    }

    public int getImage_notification() {
        return image_notification;
    }

    public void setImage_notification(int image_notification) {
        this.image_notification = image_notification;
    }


}
