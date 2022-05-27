package com.toptop.adapters;

import android.app.Activity;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Notification;
import com.toptop.utils.ItemClickListener;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.NotificationFirebase;
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
		Notification notification = this.notifications.get(position);

		if (holder instanceof NotificationViewHolder) {
			NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;
			notificationViewHolder.txt_content.setText(notification.getContent());
			notificationViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));

			if (notification.getType().equals(Notification.TYPE_COMMENT)) {
				//get video from commentID
				VideoFirebase.getVideoFromCommentIdOneTime(notification.getRedirectTo(),
						video -> {
							Glide.with(context)
									.load(video.getLinkVideo())
									.error(R.drawable.bg)
									.into(notificationViewHolder.preview_img);
						}, error -> {
							Log.e(TAG, "onBindViewHolder: " + error.getMessage());
							Glide.with(context)
									.load(R.drawable.bg)
									.into(notificationViewHolder.preview_img);
						}
				);

				//get user from notification
				UserFirebase.getUsernameByCommentId(notification.getRedirectTo(), value -> {
					if (value != null) {
						UserFirebase.getUserByUsernameOneTime(value,
								user -> {
									Glide.with(context)
											.load(user.getAvatar())
											.error(R.drawable.default_avatar)
											.into(((NotificationViewHolder) holder).img_profile);
									((NotificationViewHolder) holder).txt_username.setText(user.getFullname());
								}, databaseError -> {
									Log.e(TAG, "onBindViewHolder: " + databaseError.getMessage());
									Glide.with(context)
											.load(R.drawable.default_avatar)
											.into(((NotificationViewHolder) holder).img_profile);
								}
						);
					}
				});
			} else if (notification.getType().equals(Notification.TYPE_LIKE)) {
				//get video from likeID
				VideoFirebase.getVideoByVideoIdOneTime(notification.getRedirectTo(),
						video -> {
							Glide.with(context)
									.load(video.getLinkVideo())
									.error(R.drawable.bg)
									.into(notificationViewHolder.preview_img);
						}, error -> {
							Log.e(TAG, "onBindViewHolder: " + error.getMessage());
							Glide.with(context)
									.load(R.drawable.bg)
									.into(notificationViewHolder.preview_img);
						}
				);

				//hide username
				notificationViewHolder.txt_username.setVisibility(View.GONE);
				//indecrease size  txt_content
				notificationViewHolder.txt_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

			}

			//set onclick listener for item
			holder.itemView.setOnClickListener(v -> {
				if (notification.getType().equals(Notification.TYPE_COMMENT)) {
					VideoFirebase.getVideoFromCommentIdOneTime(notification.getRedirectTo(),
							video -> {
								MainActivity mainActivity = (MainActivity) context;
								MyUtil.goToVideo(mainActivity, video);
							}, error -> {
								Log.e(TAG, "onBindViewHolder: " + error.getMessage());
							}
					);
				} else {
					VideoFirebase.getVideoByVideoIdOneTime(notification.getRedirectTo(),
							video -> {
								MainActivity mainActivity = (MainActivity) context;
								MyUtil.goToVideo(mainActivity, video);
							}, error -> {
								Log.e(TAG, "onBindViewHolder: " + error.getMessage());
							}
					);
				}
			});
			//set onclick listener for item
			((NotificationViewHolder) holder).setItemClickListener((view, position1, isLongClick) -> {
				if (isLongClick) {
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

		} else if (holder instanceof FollowViewHolder) {
			FollowViewHolder followViewHolder = (FollowViewHolder) holder;

			followViewHolder.txt_content.setText(notification.getContent());
			followViewHolder.tx_time.setText(MyUtil.getTimeAgo(notification.getTime()));
			//get user from username
			UserFirebase.getUserByUsernameOneTime(notification.getUsername(),
					user -> {
						Glide.with(context)
								.load(user.getAvatar())
								.error(R.drawable.default_avatar)
								.into(followViewHolder.img_profile);
						((FollowViewHolder) holder).txt_username.setText(user.getUsername());
					}, databaseError -> {
						Log.e(TAG, "onBindViewHolder: " + databaseError.getMessage());
						Glide.with(context)
								.load(R.drawable.default_avatar)
								.into(followViewHolder.img_profile);
					}
			);
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
				MyUtil.goToUser((Activity) context, notification.getRedirectTo());
			});

			//set onclick listener for item
			((FollowViewHolder) holder).setItemClickListener((view, position1, isLongClick) -> {
				if (isLongClick) {
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

	class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		TextView txt_username, txt_content, tx_time;
		CircularImageView img_profile;
		ImageView preview_img;
		private ItemClickListener itemClickListener;


		public NotificationViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_usernameNotification);
			txt_content = itemView.findViewById(R.id.txt_contentNotification);
			tx_time = itemView.findViewById(R.id.txt_timeNotification);
			img_profile = itemView.findViewById(R.id.img_avatarNotification);
			preview_img = itemView.findViewById(R.id.img_Notification);

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

	class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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

