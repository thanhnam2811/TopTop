package com.toptop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.toptop.R;
import com.toptop.models.Comment;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
	// tag'
	private static final String TAG = "ReplyAdapter";
	private final DatabaseReference mDB_comment;
	private final List<Comment> replies = new ArrayList<>();
	private final Comment comment;
	Context context;

	public ReplyAdapter(List<Comment> replies, Comment comment, Context context) {
		mDB_comment = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		this.comment = comment;
		this.context = context;

		// Filter replies
		for (Comment reply : replies) {
			if (reply.getReplyToCommentId().equals(comment.getCommentId())) {
				this.replies.add(reply);
			}
		}
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
		if (payloads.isEmpty()) {
			super.onBindViewHolder(holder, position, payloads);
		} else {
			if (payloads.get(0) instanceof Comment) {
				// Log
				Log.i(TAG, "Comment is updated at position " + position);

				Comment comment = (Comment) payloads.get(0);
				holder.txt_content.setText(comment.getContent());
			}
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Comment reply = replies.get(position);
		holder.txt_content.setText(reply.getContent());
		holder.txt_username.setText(reply.getUsername());
		holder.txt_time_comment.setText(MyUtil.getTimeAgo(reply.getTime()));

		// Set txt reply to
		String html = "Trả lời <font color='black'><b>@" + reply.getUsername() + "</b></font>";
		holder.txt_reply_comment.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
		holder.txt_reply_comment.setTextSize(10);
		holder.txt_reply_comment.setAlpha(0.6f);
	}

	@Override
	public int getItemCount() {
		return replies.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_time_comment, txt_reply_comment;
		ImageView img_avatar;

		public ViewHolder(View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			img_avatar = itemView.findViewById(R.id.img_avatar);
			txt_time_comment = itemView.findViewById(R.id.txt_time_comment);
			txt_reply_comment = itemView.findViewById(R.id.txt_reply_comment);
		}
	}

	public List<Comment> getReplies() {
		return replies;
	}

	public int getPosition(Comment comment) {
		return replies.indexOf(comment);
	}
}
