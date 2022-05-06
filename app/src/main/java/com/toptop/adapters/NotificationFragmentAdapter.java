package com.toptop.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.LoginActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.Notification;
import com.toptop.models.Video;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.MyUtil;

import java.util.Date;
import java.util.List;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<NotificationFragmentAdapter.NotificationViewHolder> {
    private static final String TAG = "NotificationFragementAdapter";
    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    private List<Notification> notifications;
    Context context;

    @Override
    public void onViewAttachedToWindow(@NonNull NotificationViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public NotificationFragmentAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_listinform, parent, false);
        return new NotificationViewHolder(view);
    }


    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, tx_time;
        CircularImageView img_profile;
        ImageView privew_img;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_usernameNotification);
            txt_content = itemView.findViewById(R.id.txt_contentNotification);
            tx_time = itemView.findViewById(R.id.txt_timeNotification);
            img_profile = itemView.findViewById(R.id.img_avatarNotification);
            privew_img = itemView.findViewById(R.id.img_Notification);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        // Set info notification
        Notification notification = notifications.get(position);
        FirebaseUtil.readData(new FirebaseUtil.MyCallback() {
            @Override
            public void onCallback(String value) {
                if(value != null){
                    Log.d(TAG, "onCallback: " + value);
                    holder.txt_username.setText(notification.getUsername());
                    holder.txt_content.setText(notification.getContent());
                    holder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
                    // Set image profile
                    if(MyUtil.getBitmapFromURL(value) != null) {
                        holder.img_profile.setImageBitmap(MyUtil.getBitmapFromURL(value));
                    }else{
                        holder.img_profile.setImageResource(R.drawable.demo_avatar);
                    }
                    // Set image preview ... will handle later
                    FirebaseUtil.getPreviewVideo(new FirebaseUtil.MyCallback() {
                        @Override
                        public void onCallback(String value) {
                            if(value != null){
                                Log.d(TAG, "onCallback Video: " + value);
                                holder.privew_img.setImageBitmap(MyUtil.getBitmapFromURL(value));
                            }
                        }
                    }, notification.getRedirectTo());
                }
            }
        },notification.getUsername());
        //set onclick listener for item
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, MainActivity.class);
                   intent.putExtra("video_id", notification.getRedirectTo());
                   Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                   context.startActivity(intent);
//                   FirebaseUtil.getVideoFromCommentId(new FirebaseUtil.VideoCallback() {
//                       @Override
//                       public void onCallback(Video video) {
//                           ((MainActivity) context)
//                                   .getSupportFragmentManager().beginTransaction().replace(R.id.fragment_comment_container, new CommentFragment(video, context)).commit();
//                           ((MainActivity) context).findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
//                       }
//                   }, notification.getRedirectTo());
               }
           });
    }
    // Get position of video by video ID
    public int getPosition(String notificationId) {
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getNotificationId().equals(notificationId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
