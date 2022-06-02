package com.toptop.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.R;
import com.toptop.models.Comment;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;

import java.util.List;
import java.util.stream.Collectors;

public class CommentsManagementAdapter extends RecyclerView.Adapter<CommentsManagementAdapter.CommentsManagementViewHolder> {
	// Tag
	private static final String TAG = "CommentsManagementAdapter";

	List<Comment> allComments, filteredComments;
	Context context;

	public CommentsManagementAdapter(List<Comment> comments, Context context) {
		this.allComments = comments;
		filteredComments = comments;
		this.context = context;
	}

	@Override
	public void onBindViewHolder(@NonNull CommentsManagementViewHolder holder, int position, @NonNull List<Object> payloads) {
		super.onBindViewHolder(holder, position, payloads);
	}

	@NonNull
	@Override
	public CommentsManagementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.comments_item, viewGroup, false);
		return new CommentsManagementViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CommentsManagementViewHolder holder, int i) {
		Comment comment = filteredComments.get(i);
		holder.setData(comment);

		holder.txtDelete.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("DELETE VIDEO?");
			builder.setMessage("Are you sure you want to delete this comment?");
			builder.setPositiveButton("Yes", (dialog, which) -> {
				CommentFirebase.deleteComment(comment);
				Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
				allComments.remove(comment);
				filteredComments.remove(comment);
				notifyItemRemoved(i);
				notifyItemRangeChanged(i, filteredComments.size());
			});
			builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
			builder.show();
		});
	}

	@Override
	public int getItemCount() {
		return filteredComments.size();
	}

	public void filter(String filterNumberOfLikes, String searchText) {
		Long minLikes = MyUtil.getMin(filterNumberOfLikes);
		Long maxLikes = MyUtil.getMax(filterNumberOfLikes);

		filteredComments = allComments.stream()
				.filter(comment -> comment.getNumLikes() >= minLikes && comment.getNumLikes() <= maxLikes)
				.filter(comment -> comment.contains(searchText))
				.collect(Collectors.toList());

		notifyItemRangeChanged(0, allComments.size());
	}

	public static class CommentsManagementViewHolder extends RecyclerView.ViewHolder {
		TextView txtUsername, txtTime, txtContent, txtDelete, txtNumLikes;

		public CommentsManagementViewHolder(View view) {
			super(view);
			txtUsername = view.findViewById(R.id.txt_username);
			txtTime = view.findViewById(R.id.txt_time);
			txtContent = view.findViewById(R.id.txt_content);
			txtDelete = view.findViewById(R.id.txt_delete);
			txtNumLikes = view.findViewById(R.id.txt_num_likes);
		}

		public void setData(Comment comment) {
			txtUsername.setText(comment.getUsername());
			txtContent.setText(comment.getContent());
			txtNumLikes.setText(String.valueOf(comment.getNumLikes()));
			txtTime.setText(comment.getTime());
		}
	}
}