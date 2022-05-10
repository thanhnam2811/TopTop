package com.toptop.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;

public class NotificationFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = "NotificationFragementAdapter";
	private static final String DEF_AVATAR = "https://firebasestorage.googleapis.com/v0/b/toptop-4d369.appspot.com/o/user-default.png?alt=media&token=6a578948-c61e-4aef-873b-9b2ecc39f15e";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	private final int VIEW_TYPE_ITEM_1 = 1;
	private final int VIEW_TYPE_ITEM_2 = 2;
	private final LayoutInflater inflater;
	Context context;
	private List<Notification> notifications;

	public NotificationFragmentAdapter(List<Notification> notifications, Context context) {
		this.notifications = notifications;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public int getItemViewType(int position) {
		Notification notification = notifications.get(position);
		Log.d(TAG, Notification.TYPE_FOLLOW + "---" + notification.getType());
		if (notification.getType().equals(Notification.TYPE_FOLLOW)) {
			return VIEW_TYPE_ITEM_2;
		} else {
			return VIEW_TYPE_ITEM_1;
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (viewType == VIEW_TYPE_ITEM_2) {
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
		Notification notification = notifications.get(position);

		if (holder instanceof NotificationViewHolder) {
			NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;

			notificationViewHolder.txt_content.setText(notification.getContent());
			notificationViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
			//get video from video id
			VideoFirebase.getPreviewVideo(value -> {
				if (value != null) {
					Log.d(TAG, "onCallback Video: " + value);
					Glide.with(context).load(value).into(((NotificationViewHolder) holder).privew_img);
				}
			}, notification.getRedirectTo());

			//get user from notification
			UserFirebase.getUsernameByCommentId(notification.getRedirectTo(), value -> {
				if (value != null) {
					Log.d(TAG, "onCallback User: " + value);
					notificationViewHolder.txt_username.setText(value);
					Query query = FirebaseUtil.getUserByUsername(value);
					query.get().addOnSuccessListener(documentSnapshot -> {
						if (documentSnapshot.exists()) {
							User author = new User(documentSnapshot.getChildren().iterator().next());
							if (author.getAvatar() != null) {
								Glide.with(context).load(author.getAvatar()).into(((NotificationViewHolder) holder).img_profile);
							} else {
								Glide.with(context).load(DEF_AVATAR).into(((NotificationViewHolder) holder).img_profile);
							}
						}
					});
				}
			});

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
								MyUtil.goToVideo((Activity) context, value);
							}
						}
					}, notification.getRedirectTo());
				}
			});


		} else if (holder instanceof FollowViewHolder) {
			FollowViewHolder followViewHolder = (FollowViewHolder) holder;

			followViewHolder.txt_username.setText(notification.getRedirectTo());
			followViewHolder.txt_content.setText(notification.getContent());
			followViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
			//get user from username
			Query query = FirebaseUtil.getUserByUsername(notification.getRedirectTo());
			query.get().addOnSuccessListener(documentSnapshot -> {
				if (documentSnapshot.exists()) {
					User author = new User(documentSnapshot.getChildren().iterator().next());
					if (author.getAvatar() != null) {
						Glide.with(context).load(author.getAvatar()).into(((FollowViewHolder) holder).img_profile);
					} else {
						Glide.with(context).load(DEF_AVATAR).into(((FollowViewHolder) holder).img_profile);
					}
				}
			});
			if (MainActivity.getCurrentUser().isFollowing(notification.getUsername())) {
				followViewHolder.btn_follow.setVisibility(View.GONE);
			} else {
				followViewHolder.btn_follow.setVisibility(View.VISIBLE);
			}
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

