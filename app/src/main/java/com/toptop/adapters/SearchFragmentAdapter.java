package com.toptop.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.R;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.List;


public class SearchFragmentAdapter extends  RecyclerView.Adapter<SearchFragmentAdapter.SearchViewHolder>  {
	private static final String TAG = "SearchFragementAdapter";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	private List<User> users;
	Context context;

	@Override
	public void onViewAttachedToWindow(@NonNull SearchFragmentAdapter.SearchViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	public SearchFragmentAdapter(List<User> users, Context context) {
		this.users = users;
		this.context = context;
	}

	@NonNull
	@Override
	public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.item_account, parent, false);
		return new SearchFragmentAdapter.SearchViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
		User user = users.get(position);
		System.out.println("position: " + position);
		holder.txt_username.setText(user.getUsername());
		holder.txt_fullname.setText(user.getFullname());
		if(user.getNumFollowers()==null) {
			holder.txt_number_follow.setText("0");
		}
		else{
			holder.txt_number_follow.setText(String.valueOf(user.getNumFollowers()));
			if(Integer.valueOf(String.valueOf(user.getNumFollowers()))>0 && FirebaseUtil.checkUserIsFollowingUser(user.getUsername(),"hoaitan")) {
//				holder.btn_follow.setText("Following");
//				//set backgroundTint to button
//				holder.btn_follow.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.teal_200)));
//				holder.btn_follow.setEnabled(false);
				//hide button
				holder.btn_follow.setVisibility(View.GONE);
			}
		}
		if(user.getAvatar() != null && MyUtil.getBitmapFromURL(user.getAvatar()) != null) {
			holder.img_avatar.setImageBitmap(MyUtil.getBitmapFromURL(user.getAvatar()));
		}
		else{
			holder.img_avatar.setImageResource(R.drawable.demo_avatar);
		}

	}

	@Override
	public int getItemCount() {
		return users.size();
	}

	static class SearchViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username,txt_fullname,txt_number_follow;
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