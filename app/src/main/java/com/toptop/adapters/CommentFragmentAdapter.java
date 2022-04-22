package com.toptop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.toptop.R;
import com.toptop.models.Comment;
import com.toptop.utils.FirebaseUtil;
import com.toptop.utils.MyUtil;

import java.util.List;

public class CommentFragmentAdapter extends RecyclerView.Adapter<CommentFragmentAdapter.ViewHolder> {

	private final DatabaseReference mDB_comment;
	private final List<Comment> comments;
	Context context;

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
	public void onBindViewHolder(ViewHolder holder, int position) {
		Comment comment = comments.get(position);
		holder.txt_content.setText(comment.getContent());
		holder.txt_username.setText(comment.getUsername());
		holder.txt_time_comment.setText(MyUtil.getTimeAgo(comment.getTime()));
	}

	@Override
	public int getItemCount() {
		return comments.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_time_comment;
		ImageView img_avatar;

		public ViewHolder(View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			img_avatar = itemView.findViewById(R.id.img_avatar);
			txt_time_comment = itemView.findViewById(R.id.txt_time_comment);
		}
	}
}
