package com.toptop.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.models.Comment;
import com.toptop.utils.ItemClickListener;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;

public class CommentFragmentAdapter extends RecyclerView.Adapter<CommentFragmentAdapter.ViewHolder> {
	// tag'
	private static final String TAG = "CommentFragmentAdapter";
	Context context;
	private final List<Comment> comments;
	PopupMenu popupMenu;

	public CommentFragmentAdapter(List<Comment> comments, Context context) {
		this.comments = comments;
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.comment_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (!payloads.isEmpty()) {
			if (position >= comments.size())
				notifyItemChanged(position);
			else if (payloads.get(0) instanceof Comment) {
				// Log
				Log.i(TAG, "Comment is updated at position " + position);

				Comment comment = (Comment) payloads.get(0);
				holder.txt_content.setText(comment.getContent());
			}
		} else
			super.onBindViewHolder(holder, position, payloads);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Comment comment = comments.get(position);
		holder.txt_content.setText(comment.getContent());
		holder.txt_username.setText(comment.getUsername());
		holder.txt_time_comment.setText(MyUtil.getTimeAgo(comment.getTime()));
		holder.txt_num_likes_comment.setText(String.valueOf(comment.getNumLikes()));

		UserFirebase.getUserByUsernameOneTime(comment.getUsername(),
				user -> {
					try {
						Glide.with(context)
								.load(user.getAvatar())
								.error(R.drawable.default_avatar)
								.into(holder.img_avatar);
					} catch (Exception e) {
						Log.w(TAG, "Glide error: " + e.getMessage());
					}
				}, databaseError -> {
					try {
						Glide.with(context)
								.load(R.drawable.default_avatar)
								.into(holder.img_avatar);
					} catch (Exception e) {
						Log.w(TAG, "Glide error: " + e.getMessage());
					}
				}
		);

		RecyclerView recycler_reply_comment = holder.recycler_reply_comment;
		recycler_reply_comment.setLayoutManager(new LinearLayoutManager(context));

		// TODO: get replies, do it if have enough time
		// getReplies(comment, recycler_reply_comment);

		holder.setItemClickListener((view, position1, isLongClick) -> {
			if (isLongClick) {
				popupMenu = new PopupMenu(holder.txt_content.getContext(), view);
				popupMenu.inflate(R.menu.popup_menu_comment);
				popupMenu.setGravity(Gravity.CENTER);

				// Check owner
				if (MainActivity.isLoggedIn()
						&& (MainActivity.getCurrentUser().getUsername().equals(comment.getUsername())
						|| MainActivity.getCurrentUser().isAdmin()))
					popupMenu.getMenu().findItem(R.id.menu_comment_delete).setVisible(true);

				popupMenu.setOnMenuItemClickListener(item -> {
					switch (item.getItemId()) {
						case R.id.menu_comment_delete:
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setMessage("B???n c?? ch???c ch???n mu???n x??a b??nh lu???n n??y?");
							builder
									.setPositiveButton("X??a", (dialog, which) -> {
										VideoFirebase.getVideoByVideoIdOneTime(comment.getVideoId(),
												video -> {
													CommentFirebase.deleteCommentFromVideo(comment, video);
													comments.remove(comment);
													notifyItemRemoved(position);
													notifyItemRangeChanged(0, comments.size());
													Toast.makeText(context, "X??a b??nh lu???n th??nh c??ng!", Toast.LENGTH_SHORT).show();
												}, error -> {
													Toast.makeText(context, "X??a b??nh lu???n th???t b???i!", Toast.LENGTH_SHORT).show();
													Log.e(TAG, "onBindViewHolder: " + error.getMessage());
												});
									})
									.setNegativeButton("H???y", (dialog, which) -> dialog.dismiss());
							builder.show();
							break;

						case R.id.menu_comment_reply:

						case R.id.menu_comment_report:
							Toast.makeText(context, "Ch???c n??ng ??ang ???????c ph??t tri???n", Toast.LENGTH_SHORT).show();
							break;

						case R.id.menu_comment_copy:
							ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText("content", comment.getContent());
							clipboard.setPrimaryClip(clip);
							Toast.makeText(context, "???? copy!", Toast.LENGTH_SHORT).show();
							break;
					}
					return true;
				});

				popupMenu.show();
			}
		});

		holder.txt_reply_comment.setOnClickListener(view ->
						Toast.makeText(context, "Ch???c n??ng ??ang ???????c ph??t tri???n", Toast.LENGTH_SHORT).show()
//				handleReplyComment(comment)
		);

		holder.ic_like_comment.setOnClickListener(view -> handleLikeComment(comment));

		if (comment.isLiked()) {
			holder.ic_like_comment.setImageResource(R.drawable.ic_liked);
		} else {
			holder.ic_like_comment.setImageResource(R.drawable.ic_like_outline);
		}

		holder.img_avatar.setOnClickListener(view -> {
			MainActivity mainActivity = (MainActivity) context;
			mainActivity.hideCommentFragment();
			MyUtil.goToUser(context, comment.getUsername());
		});
	}

	private void handleLikeComment(Comment comment) {
		if (MainActivity.isLoggedIn()) {
			if (comment.isLiked()) {
				CommentFirebase.unlikeComment(comment);
			} else {
				CommentFirebase.likeComment(comment);
			}
			notifyItemChanged(comments.indexOf(comment));
		} else
			Toast.makeText(context, "B???n c???n ????ng nh???p ????? th???c hi???n ch???c n??ng n??y", Toast.LENGTH_SHORT).show();
	}

	private void handleReplyComment(Comment comment) {
		// Set reply to comment id
		CommentFragment.newComment.setReplyToCommentId(comment.getCommentId());

		// Show reply comment title in input
		TextView title = ((MainActivity) context).findViewById(R.id.txt_reply_comment_title);
		String htmlText = "Tr??? l???i b??nh lu???n c???a <b>@" + comment.getUsername() + "</b>";
		title.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY));
		ConstraintLayout layout_reply_comment_title = ((MainActivity) context).findViewById(R.id.layout_reply_comment_title);
		layout_reply_comment_title.setVisibility(View.VISIBLE);

	}


	@Override
	public int getItemCount() {
		return comments.size();
	}

	public List<Comment> getComments() {
		return comments;
	}

	public int getPosition(Comment comment) {
		return comments.indexOf(comment);
	}

	public void setComments(List<Comment> newComments) {
		for (Comment comment : comments) {
			if (!comments.contains(comment)) {
				comments.add(comment);
				notifyItemInserted(comments.size() - 1);
			} else {
				int index = comments.indexOf(comment);
				notifyItemChanged(index, comment);
			}
		}

		comments.removeIf(comment -> {
			if (!newComments.contains(comment)) {
				notifyItemRemoved(comments.indexOf(comment));
				return true;
			}
			return false;
		});
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		TextView txt_username, txt_content, txt_time_comment, txt_reply_comment, txt_num_likes_comment;
		ImageView img_avatar, ic_like_comment;
		RecyclerView recycler_reply_comment;
		private ItemClickListener itemClickListener;

		public ViewHolder(View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_email);
			txt_content = itemView.findViewById(R.id.txt_content);
			img_avatar = itemView.findViewById(R.id.img_avatar);
			txt_time_comment = itemView.findViewById(R.id.txt_time_comment);
			recycler_reply_comment = itemView.findViewById(R.id.recycler_reply_comment);
			txt_reply_comment = itemView.findViewById(R.id.txt_reply_comment);
			ic_like_comment = itemView.findViewById(R.id.ic_like_comment);
			txt_num_likes_comment = itemView.findViewById(R.id.txt_num_likes_comment);

			itemView.setOnClickListener(this); // M???u ch???t ??? ????y , set s??? ki??n onClick cho View
			itemView.setOnLongClickListener(this); // M???u ch???t ??? ????y , set s??? ki??n onLongClick cho View
		}

		//T???o setter cho bi???n itemClickListenenr
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
}
