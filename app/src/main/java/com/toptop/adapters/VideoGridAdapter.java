package com.toptop.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.List;

public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.ViewHolder> {
	private static final String TAG = "VideoGridAdapter";

	List<Video> videos;
	Context context;

	public VideoGridAdapter(List<Video> videos, Context context) {
		this.videos = videos;
		this.context = context;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.video_grid_item, null);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Video video = videos.get(position);

		updateView(holder, video);

		//hide delete button if user is not author
		if (MainActivity.getCurrentUser().getUsername().equals(video.getUsername()) || MainActivity.getCurrentUser().isAdmin()) {
			holder.txt_delete_video.setOnClickListener(v -> handleDeleteVideo(video, position));
		} else {
			holder.txt_delete_video.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads.isEmpty())
			super.onBindViewHolder(holder, position, payloads);
		else if (position >= videos.size())
			notifyItemInserted(position);
		else {
			if (payloads.get(0) instanceof Video) {
				Video video = (Video) payloads.get(0);
				updateView(holder, video);
			}
		}
	}

	private void updateView(ViewHolder holder, Video video) {
		Log.i(TAG, "updateView: " + video.getContent());
		holder.txt_num_likes.setText(String.valueOf(video.getNumLikes()));
		holder.txt_num_views.setText(String.valueOf(video.getNumViews()));
		try {
			Glide.with(context)
					.load(video.getLinkVideo())
					.error(R.drawable.img_404)
					.into(holder.img_preview);
		} catch (Exception e) {
			Log.w(TAG, "Glide error: " + e.getMessage());
		}


		holder.img_preview.setOnClickListener(v -> handleImageClick(video));
	}

	private void handleDeleteVideo(Video video, int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("X??a video")
				.setMessage("B???n c?? ch???c ch???n mu???n x??a video n??y?")
				.setPositiveButton("C??", (dialog, which) -> {
					VideoFirebase.deleteVideo(video);
					Toast.makeText(context, "X??a video th??nh c??ng", Toast.LENGTH_SHORT).show();
					videos.remove(position);
					notifyItemRemoved(position);
				})
				.setNegativeButton("H???y", (dialog, which) -> dialog.dismiss())
				.show();
	}

	private void handleImageClick(Video video) {
		MyUtil.goToVideo((Activity) context, video.getVideoId());
	}

	@Override
	public int getItemCount() {
		return videos.size();
	}

	public void setVideos(List<Video> newVideos) {
		for (Video video : newVideos) {
			if (!videos.contains(video)) {
				videos.add(video);
				notifyItemInserted(videos.size() - 1);
			} else {
				int index = videos.indexOf(video);
				notifyItemChanged(index, video);
			}
		}

		for (Video video : videos) {
			if (!newVideos.contains(video)) {
				int index = videos.indexOf(video);
				videos.remove(video);
				notifyItemRemoved(index);
			}
		}
	}

	public List<Video> getVideos() {
		return videos;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		TextView txt_num_likes, txt_num_views, txt_delete_video;
		ImageView img_preview;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
			txt_num_views = itemView.findViewById(R.id.txt_num_views);
			img_preview = itemView.findViewById(R.id.img_preview);
			txt_delete_video = itemView.findViewById(R.id.txt_delete_video);
		}
	}
}
