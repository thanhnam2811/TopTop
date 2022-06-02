package com.toptop.adapters;

import static android.os.Looper.getMainLooper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.R;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VideosManagementAdapter extends RecyclerView.Adapter<VideosManagementAdapter.VideosManagementViewHolder> {
	// Tag
	private static final String TAG = "VideosManagementAdapter";

	List<Video> allVideos, filteredVideos;
	Context context;

	public VideosManagementAdapter(List<Video> videos, Context context) {
		this.allVideos = videos;
		filteredVideos = videos;
		this.context = context;
	}

	@Override
	public void onBindViewHolder(@NonNull VideosManagementViewHolder holder, int position, @NonNull List<Object> payloads) {
		super.onBindViewHolder(holder, position, payloads);
	}

	@NonNull
	@Override
	public VideosManagementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.videos_item, viewGroup, false);
		return new VideosManagementViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull VideosManagementViewHolder holder, int i) {
		Video video = filteredVideos.get(i);
		holder.setData(video);

		holder.txtDelete.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("DELETE VIDEO?");
			builder.setMessage("Are you sure you want to delete this video?");
			builder.setPositiveButton("Yes", (dialog, which) -> {
				VideoFirebase.deleteVideo(video);
				Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();
				allVideos.remove(video);
				filteredVideos.remove(video);
				notifyItemRemoved(i);
			});
			builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
			builder.show();
		});
	}

	@Override
	public int getItemCount() {
		return filteredVideos.size();
	}

	public void filter(String filterNumberOfViews, String filterNumberOfLikes, String filterNumberOfComments, String searchText) {
		Long minViews = MyUtil.getMin(filterNumberOfViews);
		Long maxViews = MyUtil.getMax(filterNumberOfViews);
		Long minLikes = MyUtil.getMin(filterNumberOfLikes);
		Long maxLikes = MyUtil.getMax(filterNumberOfLikes);
		Long minComments = MyUtil.getMin(filterNumberOfComments);
		Long maxComments = MyUtil.getMax(filterNumberOfComments);

		filteredVideos = allVideos.stream()
				.filter(video -> video.getNumViews() >= minViews && video.getNumViews() <= maxViews)
				.filter(video -> video.getNumLikes() >= minLikes && video.getNumLikes() <= maxLikes)
				.filter(video -> video.getNumComments() >= minComments && video.getNumComments() <= maxComments)
				.filter(video -> video.contains(searchText))
				.collect(Collectors.toList());

		notifyItemRangeChanged(0, allVideos.size());
	}

	public static class VideosManagementViewHolder extends RecyclerView.ViewHolder {
		TextView txtUsername, txtDateUploaded, txtContent, txtDelete, txtNumViews, txtNumLikes, txtNumComments;
		ImageView imgPreview;

		public VideosManagementViewHolder(View view) {
			super(view);
			txtUsername = view.findViewById(R.id.txt_username);
			txtDateUploaded = view.findViewById(R.id.txt_date_uploaded);
			txtContent = view.findViewById(R.id.txt_content);
			txtDelete = view.findViewById(R.id.txt_delete);
			txtNumViews = view.findViewById(R.id.txt_num_views);
			txtNumLikes = view.findViewById(R.id.txt_num_likes);
			txtNumComments = view.findViewById(R.id.txt_num_comments);
			imgPreview = view.findViewById(R.id.img_preview);
		}

		public void setData(Video video) {
			txtUsername.setText(video.getUsername());
			txtDateUploaded.setText(video.getDateUploaded());
			txtContent.setText(video.getContent());
			txtNumViews.setText(String.valueOf(video.getNumViews()));
			txtNumLikes.setText(String.valueOf(video.getNumLikes()));
			txtNumComments.setText(String.valueOf(video.getNumComments()));
			try {
				Glide.with(itemView.getContext())
						.load(video.getLinkVideo())
						.error(R.drawable.bg)
						.into(imgPreview);
			} catch (Exception e) {
				Log.e(TAG, "setData: error: " + e.getMessage());
			}
		}
	}
}