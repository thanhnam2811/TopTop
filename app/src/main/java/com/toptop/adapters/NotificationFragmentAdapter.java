package com.toptop.adapters;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.NotificationFragment;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.ItemClickListener;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnTouchListener {
    private static final String TAG = "NotificationFragementAdapter";
    private static final String DEF_AVATAR = "https://firebasestorage.googleapis.com/v0/b/toptop-4d369.appspot.com/o/user-default.png?alt=media&token=6a578948-c61e-4aef-873b-9b2ecc39f15e";

    private final int VIEW_TYPE_ITEM_1 = 1;
    private final int VIEW_TYPE_ITEM_2 = 2;
    private final LayoutInflater inflater;
    PopupMenu popupMenu;

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
            //set onclick listener for item
            ((NotificationViewHolder) holder).setItemClickListener((view, position1, isLongClick) -> {
                if(isLongClick) {
                    //change background color for item
                    ((NotificationViewHolder) holder).itemView.setBackgroundColor(Color.parseColor("#E0E0E0"));

                    popupMenu = new PopupMenu(((NotificationViewHolder) holder).txt_content.getContext(), view);
                    popupMenu.inflate(R.menu.popup_menu_comment);
                    popupMenu.setGravity(Gravity.CENTER);

                    //edit text item in popup menu
                    popupMenu.getMenu().findItem(R.id.menu_comment_reply).setTitle("Tắt thông báo cho Video này");

                    //remove item in popup menu
                    popupMenu.getMenu().removeItem(R.id.menu_comment_copy);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_comment_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bạn có chắc chắn muốn xóa bình luận này?");
                                builder
                                        .setPositiveButton("Xóa", (dialog, which) -> {
                                            //delete notification
                                            NotificationFirebase.deleteNotification(notification);
                                            //remove notification from list
                                            notifications.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            notifyItemRangeChanged(holder.getAdapterPosition(), notifications.size());
                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();

                                        })
                                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                                builder.show();
                                break;
                            case R.id.menu_comment_reply:
                                Toast.makeText(context, "chức năng đang phát triển", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.menu_comment_report:
                                Toast.makeText(context, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    });

                    popupMenu.show();
                }
            });

        } else if(holder instanceof FollowViewHolder) {
            FollowViewHolder followViewHolder = (FollowViewHolder) holder;

            followViewHolder.txt_content.setText(notification.getContent());
            followViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
            //get user from username
            UserFirebase.getUserByUsername(user -> {
                if (user != null) {
                    Log.d(TAG, "onCallback User: " + user.getUsername());
                    followViewHolder.txt_username.setText(user.getFullname());
                    Glide.with(context)
                        .load(user.getAvatar())
                        .error(R.drawable.default_avatar)
                        .into(((FollowViewHolder) holder).img_profile);
                }
            }, notification.getRedirectTo());
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

            //set onclick listener for item
            ((FollowViewHolder) holder).setItemClickListener((view, position1, isLongClick) -> {
                if(isLongClick) {
                    //change background color for item
                    ((FollowViewHolder) holder).itemView.setBackgroundColor(Color.parseColor("#E0E0E0"));

                    popupMenu = new PopupMenu(((FollowViewHolder) holder).txt_content.getContext(), view);
                    popupMenu.inflate(R.menu.popup_menu_comment);
                    popupMenu.setGravity(Gravity.CENTER);

                    //remove item in popup menu
                    popupMenu.getMenu().removeItem(R.id.menu_comment_copy);
                    popupMenu.getMenu().removeItem(R.id.menu_comment_reply);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_comment_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Bạn có chắc chắn muốn xóa bình luận này?");
                                builder
                                        .setPositiveButton("Xóa", (dialog, which) -> {
                                            //delete notification
                                            NotificationFirebase.deleteNotification(notification);
                                            //remove notification from list
                                            notifications.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            notifyItemRangeChanged(holder.getAdapterPosition(), notifications.size());
                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();

                                        })
                                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                                builder.show();
                                break;

                            case R.id.menu_comment_report:
                                Toast.makeText(context, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView txt_username, txt_content, tx_time;
        CircularImageView img_profile;
        ImageView privew_img;
        private ItemClickListener itemClickListener;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_usernameNotification);
            txt_content = itemView.findViewById(R.id.txt_contentNotification);
            tx_time = itemView.findViewById(R.id.txt_timeNotification);
            img_profile = itemView.findViewById(R.id.img_avatarNotification);
            privew_img = itemView.findViewById(R.id.img_Notification);

            itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
            itemView.setOnLongClickListener(this); // Mấu chốt ở đây , set sự kiên onLongClick cho View
        }
        //Tạo setter cho biến itemClickListenenr
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    class FollowViewHolder extends RecyclerView.ViewHolder   implements View.OnClickListener, View.OnLongClickListener{
        TextView txt_username, txt_content, tx_time;
        CircularImageView img_profile;
        Button btn_follow;
        private ItemClickListener itemClickListener;


        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_usernameNotification);
            txt_content = itemView.findViewById(R.id.txt_contentNotification);
            tx_time = itemView.findViewById(R.id.txt_timeNotification);
            img_profile = itemView.findViewById(R.id.img_avatarNotification);
            btn_follow = itemView.findViewById(R.id.btn_followBack);

            itemView.setOnClickListener(this); // Mấu chốt ở đây , set sự kiên onClick cho View
            itemView.setOnLongClickListener(this); // Mấu chốt ở đây , set sự kiên onLongClick cho View
        }

        //Tạo setter cho biến itemClickListenenr
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
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

}

