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

import java.util.List;

public class CommentFragmentAdapter extends RecyclerView.Adapter<CommentFragmentAdapter.ViewHolder> {

	private DatabaseReference mDB_comment;
	private List<Comment> comments;
	Context context;

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
	public void onBindViewHolder(ViewHolder holder, int position) {
		Comment comment = comments.get(position);
		holder.txt_content.setText(comment.getContent());
		holder.txt_username.setText(comment.getUsername());
	}

	@Override
	public int getItemCount() {
		return comments.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content;
		ImageView img_avatar, ic_send_comment;

		public ViewHolder(View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			img_avatar = itemView.findViewById(R.id.img_avatar);
			ic_send_comment = itemView.findViewById(R.id.ic_send_comment);
		}
	}
}
