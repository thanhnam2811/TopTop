package com.toptop.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.UserFirebase;

import java.util.List;


public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.SearchViewHolder> {
	private static final String TAG = "SearchFragementAdapter";

	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	Context context;
	private List<User> users;

	public SearchFragmentAdapter(List<User> users, Context context) {
		this.users = users;
		this.context = context;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public void onViewAttachedToWindow(@NonNull SearchViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	@NonNull
	@Override
	public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.item_account, parent, false);
		return new SearchViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
		User user = users.get(position);
		System.out.println("position: " + position);
		holder.txt_username.setText(user.getUsername());
		holder.txt_fullname.setText(user.getFullname());
		if (user.getNumFollowers() == null) {
			holder.txt_number_follow.setText("0");
		} else {
			holder.txt_number_follow.setText(String.valueOf(user.getNumFollowers()));
			//get user current
			User currentUser = MainActivity.getCurrentUser();
			if ((Integer.valueOf(String.valueOf(user.getNumFollowers())) > 0 && currentUser.isFollowing(user.getUsername())) || user.getUsername().equals(currentUser.getUsername())) {
//				holder.btn_follow.setText("Following");
//				//set backgroundTint to button
//				holder.btn_follow.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.teal_200)));
//				holder.btn_follow.setEnabled(false);
				//hide button
				holder.btn_follow.setVisibility(View.GONE);
			}
		}
		// Load avatar
		UserFirebase.getAvatar(new UserFirebase.MyCallback() {
			@Override
			public void onCallback(String value) {
				Glide.with(context)
					.load(value)
						.error(R.drawable.default_avatar)
						.into(holder.img_avatar);
			}
		}, user.getUsername());
		//handle follow button
		holder.btn_follow.setOnClickListener(v -> {
			UserFirebase.followUser(user.getUsername());
//				//hide button
			holder.btn_follow.setVisibility(View.GONE);
			Toast.makeText(context, "Đã theo dõi " + user.getUsername(), Toast.LENGTH_SHORT).show();
		});
		//handle click item
		holder.itemView.setOnClickListener(v -> {
			Log.d(TAG, "onClick on Search: " + user.getUsername());
			MyUtil.goToUser((Activity) context, user.getUsername());
		});

	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	static class SearchViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_fullname, txt_number_follow;
		Button btn_follow;
		CircularImageView img_avatar;
		SearchView searchView;

		public SearchViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_usernameSearch);
			txt_fullname = itemView.findViewById(R.id.txt_fullnameSearch);
			txt_number_follow = itemView.findViewById(R.id.number_followerSearch);
			img_avatar = itemView.findViewById(R.id.img_avatarUser);
			searchView = itemView.findViewById(R.id.searchView);
			btn_follow = itemView.findViewById(R.id.btn_followSearch);
		}
	}
}