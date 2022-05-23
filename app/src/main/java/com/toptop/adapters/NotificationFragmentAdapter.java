package com.toptop.adapters;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NotificationFragementAdapter";
    private static final String DEF_AVATAR = "https://firebasestorage.googleapis.com/v0/b/toptop-4d369.appspot.com/o/user-default.png?alt=media&token=6a578948-c61e-4aef-873b-9b2ecc39f15e";

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
        Notification notification = this.notifications.get(position);
        Log.d(TAG, Notification.TYPE_FOLLOW + "---" + notification.getType());
        if(notification.getType().equals(Notification.TYPE_FOLLOW)){
            return VIEW_TYPE_ITEM_2;
        }else{
            return VIEW_TYPE_ITEM_1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == VIEW_TYPE_ITEM_2) {
            Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_ITEM_2");
            View view = inflater.inflate(R.layout.item_followinform, parent, false);
            return new FollowViewHolder(view);
        } else if (viewType == VIEW_TYPE_ITEM_1) {
                Log.d(TAG, "onCreateViewHolder: VIEW_TYPE_ITEM_1");
                View view = inflater.inflate(R.layout.item_listinform, parent, false);
                return new NotificationViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = this.notifications.get(position);

        if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;
            notificationViewHolder.txt_content.setText(notification.getContent());
            notificationViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));

            if(notification.getType().equals(Notification.TYPE_COMMENT)) {
                //get video from commentID
                VideoFirebase.getVideoFromCommentId(video -> {
                    if (video != null) {
                        Glide.with(context)
                                .load(video.getLinkVideo())
                                .error(R.drawable.avatar)
                                .into(((NotificationViewHolder) holder).privew_img);
                    }
                }, notification.getRedirectTo());

                //get user from notification
                UserFirebase.getUsernameByCommentId(notification.getRedirectTo(), value -> {
                    if (value != null) {
                        Log.d(TAG, "onCallback User: " + value);
                        UserFirebase.getUserByUsername(user -> {
                            if (user != null) {
                                Log.d(TAG, "onCallback User: " + user.getUsername());
                                notificationViewHolder.txt_username.setText(user.getFullname());
                            }
                            Glide.with(context)
                                        .load(user.getAvatar())
                                        .error(R.drawable.default_avatar)
                                        .into(((NotificationViewHolder) holder).img_profile);
                        }, value);
                    }
                });
            } else if(notification.getType().equals(Notification.TYPE_LIKE)) {
                //get video from likeID
                VideoFirebase.getVideoFromVideoId(value -> {
                    if (value != null) {
                        Glide.with(context)
                                .load(value.getLinkVideo())
                                .error(R.drawable.avatar)
                                .into(((NotificationViewHolder) holder).privew_img);
                    }
                }, notification.getRedirectTo());

                //hide username
                notificationViewHolder.txt_username.setVisibility(View.GONE);
                //indecrease size  txt_content
                notificationViewHolder.txt_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

            }

            //set onclick listener for item
            holder.itemView.setOnClickListener(v -> {
                Log.d(TAG, "onClick: " + notification.getRedirectTo());
                if(notification.getType().equals(Notification.TYPE_COMMENT)) {
                    VideoFirebase.getVideoFromCommentId(value -> {
                        if (value != null) {
                            Log.d(TAG, "onCallback Video: " + value);
                            MyUtil.goToVideo((Activity) context, value);
                            //go to comment in video

                        }
                    }, notification.getRedirectTo());
                }else{
                    VideoFirebase.getVideoFromVideoId(value -> {
                        if (value != null) {
                            Log.d(TAG, "onCallback Video: " + value);
                            MyUtil.goToVideo((Activity) context, value);
                        }
                    }, notification.getRedirectTo());
                }
            });


        } else if(holder instanceof FollowViewHolder) {
            FollowViewHolder followViewHolder = (FollowViewHolder) holder;

            followViewHolder.txt_content.setText(notification.getContent());
            followViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
            //get user from username
            Query query = FirebaseUtil.getUserByUsername(notification.getRedirectTo());
            query.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User author = new User(documentSnapshot.getChildren().iterator().next());
                    followViewHolder.txt_username.setText(author.getFullname());
                    Glide.with(context)
                            .load(author.getAvatar())
                            .error(R.drawable.default_avatar)
                            .into(((FollowViewHolder) holder).img_profile);
                }
            });
            if (MainActivity.getCurrentUser().isFollowing(notification.getRedirectTo())) {
                followViewHolder.btn_follow.setVisibility(View.GONE);
            } else {
                followViewHolder.btn_follow.setVisibility(View.VISIBLE);
            }
            //handle follow button
            ((FollowViewHolder) holder).btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserFirebase.followUser(notification.getRedirectTo());
                    //Add notification for user
                    Notification notification = new Notification();
                    notification.setUsername(notification.getRedirectTo());
                    notification.setContent(MainActivity.getCurrentUser().getUsername() + " đã theo dõi bạn");
                    notification.setType(Notification.TYPE_FOLLOW);
                    notification.setTime(MyUtil.getCurrentTime());
                    notification.setRedirectTo(MainActivity.getCurrentUser().getUsername());
                    NotificationFirebase.addNotification(notification);

                    //hide follow button
                    ((FollowViewHolder) holder).btn_follow.setVisibility(View.GONE);
                    Toast.makeText(context, "Đã theo dõi " + notification.getRedirectTo(), Toast.LENGTH_SHORT).show();
                }
            });
            //set onclick listener for item
            holder.itemView.setOnClickListener(v -> {
                Log.d(TAG, "forward: " + notification.getRedirectTo());
                MyUtil.goToUser((Activity)context, notification.getRedirectTo());
            });
            // handle horizontal swipe to delete item
//            holder.itemView.setOnTouchListener(new SwipeToDelete(context, position, holder));

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

    private class SwipeToDelete implements View.OnTouchListener {
        public SwipeToDelete(Context context, int position, RecyclerView.ViewHolder holder) {
            //get notification
            Notification notification = NotificationFragmentAdapter.this.notifications.get(position);
            //delete notification
            NotificationFirebase.deleteNotification(notification);

        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //remove item
                 int position = view.getId();
                 notifications.remove(position);
                 notifyItemRemoved(position);
                 notifyItemRangeChanged(position, notifications.size());

                 return true;
            }
            return false;
        }
    }
}

