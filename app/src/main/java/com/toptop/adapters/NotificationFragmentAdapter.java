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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.LoginActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.fragment.VideoFragment;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NotificationFragementAdapter";
    private final int VIEW_TYPE_ITEM_1 = 1;
    private final int VIEW_TYPE_ITEM_2 = 2;
    private final LayoutInflater inflater;


    public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    private List<Notification> notifications;
    Context context;

    public NotificationFragmentAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = notifications.get(position);
        if(notification.getType() == Notification.TYPE_FOLLOW){
            return VIEW_TYPE_ITEM_2;
        }else{
            return VIEW_TYPE_ITEM_1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_ITEM_1) {
            View view = inflater.inflate(R.layout.item_listinform, parent, false);
            return new NotificationViewHolder(view);
        } else if(viewType == VIEW_TYPE_ITEM_2) {
            View view = inflater.inflate(R.layout.item_followinform, parent, false);
            return new FollowViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;
            UserFirebase.readDataUser(new UserFirebase.MyCallback() {
                @Override
                public void onCallback(String value) {
                    if (value != null) {
                        Log.d(TAG, "onCallback: " + value);
                        notificationViewHolder.txt_username.setText(notification.getUsername());
                        notificationViewHolder.txt_content.setText(notification.getContent());
                        notificationViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
                        // Set image profile
                        if (MyUtil.getBitmapFromURL(value) != null) {
                            notificationViewHolder.img_profile.setImageBitmap(MyUtil.getBitmapFromURL(value));
                        } else {
                            notificationViewHolder.img_profile.setImageResource(R.drawable.demo_avatar);
                        }
                        // Set image preview ... will handle later
                        VideoFirebase.getPreviewVideo(new VideoFirebase.MyCallback() {
                            @Override
                            public void onCallback(String value) {
                                if (value != null) {
                                    Log.d(TAG, "onCallback Video: " + value);
                                    notificationViewHolder.privew_img.setImageBitmap(MyUtil.getBitmapFromURL(value));
                                }
                            }
                        }, notification.getRedirectTo());
                    }
                }
            }, notification.getUsername());
            //set onclick listener for item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + notification.getRedirectTo());
                    VideoFirebase.getVideoFromCommentId(new VideoFirebase.VideoCallback() {
                        @Override
                        public void onCallback(Video value) {
                            if (value != null) {
                                Log.d(TAG, "onCallback Video: " + value);
                                ((MainActivity) context).goToVideo(value);
                            }
                        }
                    }, notification.getRedirectTo());
                }
            });


        } else if(holder instanceof FollowViewHolder) {
            FollowViewHolder followViewHolder = (FollowViewHolder) holder;
            UserFirebase.readDataUser(new UserFirebase.MyCallback() {
                @Override
                public void onCallback(String value) {
                    if (value != null) {
                        Log.d(TAG, "onCallback: " + value);
                        followViewHolder.txt_username.setText(notification.getUsername());
                        followViewHolder.txt_content.setText(notification.getContent());
                        followViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
                        // Set image profile
                        if (MyUtil.getBitmapFromURL(value) != null) {
                            followViewHolder.img_profile.setImageBitmap(MyUtil.getBitmapFromURL(value));
                        } else {
                            followViewHolder.img_profile.setImageResource(R.drawable.demo_avatar);
                        }
                        // Set image preview ... will handle later
                        if (MainActivity.getCurrentUser().isFollowing(notification.getUsername())) {
                            followViewHolder.btn_follow.setVisibility(View.GONE);
                        } else {
                            followViewHolder.btn_follow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }, notification.getUsername());
            //set onclick listener for item
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserFirebase.getUserByUsername(new UserFirebase.UserCallback() {
                        @Override
                        public void onCallBack(User user) {
                            if (user != null) {
//                                ((MainActivity) context).goToUser(user);
                            }
                        }
                    }, notification.getUsername());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {
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

    class FollowViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, tx_time;
        CircularImageView img_profile;
        Button btn_follow;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_usernameNotification);
            txt_content = itemView.findViewById(R.id.txt_contentNotification);
            tx_time = itemView.findViewById(R.id.txt_timeNotification);
            img_profile = itemView.findViewById(R.id.img_avatarNotification);
            btn_follow = itemView.findViewById(R.id.btn_followBack);

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


    }
}

