package com.toptop.broadcastReceiver;

import static com.toptop.service.NotificationService.NOTIFICATION_REPLY;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.RemoteInput;

import com.toptop.MainActivity;
import com.toptop.models.Comment;
import com.toptop.service.NotificationService;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.VideoFirebase;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final int NOTIFICATION_ID = 1;
    private static final String COMMENT_NOTIFICATION = "comment_notification";
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //getting the remote input bundle from intent
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            String reply = remoteInput.getCharSequence(NOTIFICATION_REPLY).toString();
            if (reply != null && !reply.isEmpty()) {
                Bundle extras = intent.getExtras();
                String commentId = extras.getString(COMMENT_NOTIFICATION);
//				Toast.makeText(this, "CommentId: " + commentId, Toast.LENGTH_SHORT).show();
                if (commentId != null && !commentId.isEmpty()) {
                    //Get video by commentId
                    VideoFirebase.getVideoFromCommentIdOneTime(commentId,
                            video -> {
                                //create new comment
                                Comment commentReply = new Comment();
                                commentReply.setContent(reply);
                                commentReply.setUsername(MainActivity.getCurrentUser().getUsername()); // ? or currentUser.getUsername()
                                commentReply.setVideoId(video.getVideoId());

                                //add comment to database
                                CommentFirebase.addCommentToVideo(commentReply, video);
                                Toast.makeText(context, "Reply: " + reply, Toast.LENGTH_SHORT).show();
                            }, error -> {
                                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCreate: " + error.getMessage());
                            }
                    );
                }

            }
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NOTIFICATION_ID);
        }

        //if Delete button is clicked
        if (intent.getIntExtra(NotificationService.KEY_INTENT_HELP, -1) == NotificationService.REQUEST_CODE_HELP) {
            Toast.makeText(context, "Delete notification", Toast.LENGTH_LONG).show();
            //delete notification
            String notificationId = intent.getStringExtra(NotificationService.NOTIFICATION_ID);
            if (notificationId != null && !notificationId.isEmpty()) {
                NotificationFirebase.deleteNotificationByNotificationId(notificationId);
            }
            //cancel notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NotificationService.NOTIFICATION_COMMENT_ID);
        }

        //if like button is clicked
        if (intent.getIntExtra(NotificationService.KEY_INTENT_LIKE, -1) == NotificationService.REQUEST_CODE_LIKE) {
            Toast.makeText(context, "You Clicked Like", Toast.LENGTH_LONG).show();
            //get value from intent
            String commentId = intent.getStringExtra(COMMENT_NOTIFICATION);
            if (commentId != null && !commentId.isEmpty()) {
                //Get comment by commentId
                CommentFirebase.getCommentByCommentIdOneTime(commentId,
                        comment -> {
                    //update comment
                        CommentFirebase.likeComment(comment);
                        Toast.makeText(context, "Like: " + comment.getContent(), Toast.LENGTH_SHORT).show();
                    }, error -> {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }
    }
}
