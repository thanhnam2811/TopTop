package com.toptop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.models.Comment;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentFragmentAdapter extends RecyclerView.Adapter<CommentFragmentAdapter.ViewHolder> {
	// tag'
	private static final String TAG = "CommentFragmentAdapter";
	private final DatabaseReference mDB_comment;
	Context context;
	private final List<Comment> comments;
	private List<Comment> replies = new ArrayList<>();

	public CommentFragmentAdapter(List<Comment> comments, Context context) {
		mDB_comment = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
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

		RecyclerView recycler_reply_comment = holder.recycler_reply_comment;
		recycler_reply_comment.setLayoutManager(new LinearLayoutManager(context));

		// TODO: get replies, do it if have enough time
		// getReplies(comment, recycler_reply_comment);

		holder.txt_reply_comment.setOnClickListener(view -> handleReplyComment(comment));

		holder.ic_like_comment.setOnClickListener(view -> handleLikeComment(comment));

		if (comment.isLiked()) {
			holder.ic_like_comment.setImageResource(R.drawable.ic_liked);
		} else {
			holder.ic_like_comment.setImageResource(R.drawable.ic_like_outline);
		}
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
			Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
	}

	private void handleReplyComment(Comment comment) {
		// Set reply to comment id
		CommentFragment.newComment.setReplyToCommentId(comment.getCommentId());

		// Show reply comment title in input
		TextView title = ((MainActivity) context).findViewById(R.id.txt_reply_comment_title);
		String htmlText = "Trả lời bình luận của <b>@" + comment.getUsername() + "</b>";
		title.setText(HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY));
		ConstraintLayout layout_reply_comment_title = ((MainActivity) context).findViewById(R.id.layout_reply_comment_title);
		layout_reply_comment_title.setVisibility(View.VISIBLE);
	}

	private void getReplies(Comment comment, RecyclerView recycler_reply_comment) {
		// Get replies
		Query query = mDB_comment.orderByChild("replyToCommentId").equalTo(comment.getCommentId());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (recycler_reply_comment.getAdapter() == null) {
					// Log
					Log.i(TAG, "First time load reply comment");
					// Get all replies from firebase
					replies.clear();
					for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
						Comment reply = new Comment(dataSnapshot);

						// Log
						Log.i(TAG, "Reply comment for commentId " + comment.getCommentId() + " is " + reply.getContent());

						replies.add(reply);
					}

					// Set adapter for recycler view
					if (replies.size() > 0) Comment.sortByTimeNewsest(replies);
					ReplyAdapter adapter = new ReplyAdapter(replies, comment, context);
					recycler_reply_comment.setAdapter(adapter);

					// Set visibility
					recycler_reply_comment.setVisibility(View.VISIBLE);
				} else {
					ReplyAdapter adapter = (ReplyAdapter) recycler_reply_comment.getAdapter();
					replies = adapter.getReplies();

					// Get all replies from firebase
					for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
						Comment reply = new Comment(dataSnapshot);
						// Add reply to list if it is not in list
						if (!replies.contains(reply))
							replies.add(reply);
							// if reply is in list, update it if changed
						else {
							int index = replies.indexOf(reply);
							if (!replies.get(index).isEqual(reply)) {
								replies.set(index, reply);
								adapter.notifyItemChanged(index, reply);
							}
						}
					}
					// Sort by time
					Comment.sortByTimeNewsest(comments);
					// Notify adapter
					recycler_reply_comment.getAdapter().notifyItemRangeChanged(0, comments.size());
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "onCancelled: ", error.toException());
			}
		});
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

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_time_comment, txt_reply_comment, txt_num_likes_comment;
		ImageView img_avatar, ic_like_comment;
		RecyclerView recycler_reply_comment;

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
		}
	}
}
