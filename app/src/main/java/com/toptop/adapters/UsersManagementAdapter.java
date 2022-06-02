package com.toptop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.R;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsersManagementAdapter extends RecyclerView.Adapter<UsersManagementAdapter.UsersManagementViewHolder> {
	// Tag
	private static final String TAG = "UsersManagementAdapter";

	List<User> allUsers, filteredUsers;
	Context context;

	public UsersManagementAdapter(List<User> users, Context context) {
		this.allUsers = users;
		filteredUsers = users;
		this.context = context;
	}

	@Override
	public void onBindViewHolder(@NonNull UsersManagementViewHolder holder, int position, @NonNull List<Object> payloads) {
		super.onBindViewHolder(holder, position, payloads);
	}

	@NonNull
	@Override
	public UsersManagementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.users_item, viewGroup, false);
		return new UsersManagementViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull UsersManagementViewHolder holder, int i) {
		User user = filteredUsers.get(i);
		holder.setData(user);

		holder.txtDelete.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("DELETE USER?");
			builder.setMessage("Are you sure you want to delete user '" + user.getUsername() + "'?");
			builder.setPositiveButton("Yes", (dialog, which) -> {
				UserFirebase.deleteUser(user);
				Toast.makeText(context, "User: '" + user.getUsername() + "' deleted!", Toast.LENGTH_SHORT).show();
				allUsers.remove(user);
				filteredUsers.remove(user);
				notifyItemRemoved(i);
			});
			builder.setNegativeButton("No", (dialog, which) -> {
				dialog.dismiss();
			});
			builder.show();
		});
	}

	@Override
	public int getItemCount() {
		return filteredUsers.size();
	}

	public void setUsers(List<User> newUsers) {
		this.filteredUsers = newUsers;
		notifyItemRangeChanged(0, allUsers.size());
	}

	public void setFilter(String filterNumberOfFollowers, String filterNumberOfFollowing, String filterNumberOfLikes, String search, String role) {
		Long minFollowers = MyUtil.getMin(filterNumberOfFollowers);
		Long maxFollowers = MyUtil.getMax(filterNumberOfFollowers);
		Long minFollowing = MyUtil.getMin(filterNumberOfFollowing);
		Long maxFollowing = MyUtil.getMax(filterNumberOfFollowing);
		Long minLikes = MyUtil.getMin(filterNumberOfLikes);
		Long maxLikes = MyUtil.getMax(filterNumberOfLikes);

		List<User> newUsers = allUsers.stream()
				.filter(user -> user.getNumFollowers() >= minFollowers && user.getNumFollowers() <= maxFollowers)
				.filter(user -> user.getNumFollowing() >= minFollowing && user.getNumFollowing() <= maxFollowing)
				.filter(user -> user.getNumLikes() >= minLikes && user.getNumLikes() <= maxLikes)
				.filter(user -> user.contains(search))
				.filter(user -> role == null || user.getRole().equals(role))
				.collect(Collectors.toList());
		setUsers(newUsers);
	}

	public static class UsersManagementViewHolder extends RecyclerView.ViewHolder {
		TextView username, fullname, email, phonenumber, numFollowers, numFollowing, numLikes, role, txtDelete;
		ImageView avatar;


		public UsersManagementViewHolder(android.view.View itemView) {
			super(itemView);
			username = itemView.findViewById(R.id.txt_username);
			fullname = itemView.findViewById(R.id.txt_fullname);
			email = itemView.findViewById(R.id.txt_email);
			phonenumber = itemView.findViewById(R.id.txt_phonenumber);
			numFollowers = itemView.findViewById(R.id.txt_num_followers);
			numFollowing = itemView.findViewById(R.id.txt_num_following);
			numLikes = itemView.findViewById(R.id.txt_num_likes);
			avatar = itemView.findViewById(R.id.img_avatar);
			role = itemView.findViewById(R.id.txt_role);
			txtDelete = itemView.findViewById(R.id.txt_delete);
		}

		public void setData(User user) {
			username.setText(user.getUsername());
			fullname.setText(user.getFullname());
			email.setText(user.getEmail());
			phonenumber.setText(user.getPhoneNumber());
			numFollowers.setText(MyUtil.getNumberToText(user.getNumFollowers(), 0));
			numFollowing.setText(MyUtil.getNumberToText(user.getNumFollowing(), 0));
			numLikes.setText(MyUtil.getNumberToText(user.getNumLikes(), 0));
			role.setText(user.getRole());
			try {
				Glide.with(itemView.getContext())
						.load(user.getAvatar())
						.error(R.drawable.default_avatar)
						.into(avatar);
			} catch (Exception e) {
				Log.e(TAG, "setData error: " + e.getMessage());
			}
		}
	}
}
