package com.toptop.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.R;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.List;


public class SearchFragmentVideoAdapter extends RecyclerView.Adapter<SearchFragmentVideoAdapter.SearchViewHolder> {
	private static final String TAG = "SearchFragementAdapter";
	private static final String DEF_AVATAR = "https://firebasestorage.googleapis.com/v0/b/toptop-4d369.appspot.com/o/user-default.png?alt=media&token=6a578948-c61e-4aef-873b-9b2ecc39f15e";

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
		holder.img_video.setImageBitmap(MyUtil.getBitmapFromURL(video.getPreview()));
		//load image priview
		Glide.with(context).load(video.getPreview()).into(holder.img_video);
		holder.txt_username.setText(video.getUsername());
		// Load avatar
		Query query = FirebaseUtil.getUserByUsername(video.getUsername());
		query.get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				User author = new User(documentSnapshot.getChildren().iterator().next());
				if (author.getAvatar() != null) {
					Glide.with(context).load(author.getAvatar()).into(holder.img_avatar);
				} else {
					Glide.with(context).load(DEF_AVATAR).into(holder.img_avatar);
				}
			}
		});
		holder.itemView.setOnClickListener(v -> MyUtil.goToVideo((Activity) context, video));
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
			img_video = itemView.findViewById(R.id.img_PriveVideo);
		}
	}
}
