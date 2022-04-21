package com.toptop.models;

import android.util.Log;

import com.toptop.utils.MyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {
    private static final String TAG = "Comment Model";
    private String comment_id, username, video_id, content;
    private String time;
    private List<Comment> reply_to = new ArrayList<>();

    public Comment() {
    }

    public Comment(String comment_id, String username, String video_id, String content, Date time) {
        this.comment_id = comment_id;
        this.username = username;
        this.video_id = video_id;
        this.content = content;
        this.time = MyUtil.getFormattedDate(time);
    }

    public Comment(String comment_id, String username, String video_id, String content, String formattedTime) {
        this.comment_id = comment_id;
        this.username = username;
        this.video_id = video_id;
        this.content = content;
        if (MyUtil.isValidDate(formattedTime)) {
            this.time = formattedTime;
        } else {
            Log.i(TAG, "formattedTime is not valid, set to current time");
            this.time = MyUtil.getFormattedDate(new Date());
        }
    }

    public Comment(String comment_id, String username, String video_id, String content, Date time, List<Comment> reply_to) {
        this.comment_id = comment_id;
        this.username = username;
        this.video_id = video_id;
        this.content = content;
        this.time = MyUtil.getFormattedDate(time);
        this.reply_to = reply_to;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = MyUtil.getFormattedDate(time);
    }

    public List<Comment> getReply_to() {
        return reply_to;
    }

    public void setReply_to(List<Comment> reply_to) {
        this.reply_to = reply_to;
    }

    public void addReply_to(Comment reply_to) {
        this.reply_to.add(reply_to);
    }

    public void removeReply_to(Comment reply_to) {
        this.reply_to.remove(reply_to);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id='" + comment_id + '\'' +
                ", username='" + username + '\'' +
                ", video_id='" + video_id + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", reply_to=" + reply_to +
                '}';
    }
}
