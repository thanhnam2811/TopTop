package com.toptop.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.R;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.UserFirebase;

import java.util.List;


public class SearchFragmentVideoAdapter extends RecyclerView.Adapter<SearchFragmentVideoAdapter.SearchViewHolder> {
	private static final String TAG = "SearchFragementAdapter";

	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	Context context;
	private List<Video> videos;

	public SearchFragmentVideoAdapter(List<Video> videos, Context context) {
		this.videos = videos;
		this.context = context;
	}

	@Override
	public void onViewAttachedToWindow(@NonNull SearchViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	@NonNull
	@Override
	public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.items_video, parent, false);
		return new SearchViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
		Video video = videos.get(position);
		System.out.println("video: " + video);
		holder.txt_timePost.setText(MyUtil.getTimeAgo(video.getDateUploaded()));
		holder.txt_content.setText(video.getContent());
		//load image priview
		try {
			Glide.with(context)
					.load(video.getLinkVideo())
					.error(R.drawable.bg)
					.into(holder.img_video);
		} catch (Exception e) {
			Log.w(TAG, "Glide error: " + e.getMessage());
		}

		holder.txt_username.setText(video.getUsername());
		// Load avatar
		UserFirebase.getAvatar(value -> {
			try {
				Glide.with(context)
						.load(value)
						.error(R.drawable.default_avatar)
						.into(holder.img_avatar);
			} catch (Exception e) {
				Log.w(TAG, "Glide error: " + e.getMessage());
			}
		}, video.getUsername());

		holder.itemView.setOnClickListener(v -> MyUtil.goToVideo((Activity) context, video.getVideoId()));
	}

	@Override
	public int getItemCount() {
		return videos.size();
	}

	static class SearchViewHolder extends RecyclerView.ViewHolder {
		TextView txt_timePost, txt_username, txt_content;
		CircularImageView img_avatar;
		ImageView img_video;

		public SearchViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_timePost = itemView.findViewById(R.id.txt_timePost);
			txt_username = itemView.findViewById(R.id.txt_usernameVideo);
			txt_content = itemView.findViewById(R.id.txt_contentVideo);
			img_avatar = itemView.findViewById(R.id.img_avatarUser);
			img_video = itemView.findViewById(R.id.img_previewVideo);
		}
	}
}
