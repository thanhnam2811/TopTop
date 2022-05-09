package com.toptop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.List;


public class SearchFragmentVideoAdapter extends RecyclerView.Adapter<SearchFragmentVideoAdapter.SearchViewHolder> {
	private static final String TAG = "SearchFragementAdapter";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();

	private List<Video> videos;
	Context context;

	@Override
	public void onViewAttachedToWindow(@NonNull SearchViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	public SearchFragmentVideoAdapter(List<Video> videos, Context context) {
		this.videos = videos;
		this.context = context;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public List<Video> getVideos() {
		return videos;
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
		Query query = FirebaseUtil.getUserByUsername(video.getUsername());
		query.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				holder.txt_timePost.setText(MyUtil.getTimeAgo(video.getDateUploaded()));
				holder.txt_content.setText(video.getContent());
				holder.img_video.setImageBitmap(MyUtil.getBitmapFromURL(video.getPreview()));
				holder.txt_username.setText(video.getUsername());
				User user = new User();

				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					user = new User(snapshot);
				}
				if (user != null) {
					System.out.println("avatar: " + user.getAvatar());
					if (user.getAvatar() != null && MyUtil.getBitmapFromURL(user.getAvatar()) != null) {
						holder.img_avatar.setImageBitmap(MyUtil.getBitmapFromURL(user.getAvatar()));
					} else {
						holder.img_avatar.setImageResource(R.drawable.demo_avatar);
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.d(TAG, "onCancelled: " + error.getMessage());
			}
		});
		holder.itemView.setOnClickListener(v -> ((MainActivity) context).goToVideo(video));
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
