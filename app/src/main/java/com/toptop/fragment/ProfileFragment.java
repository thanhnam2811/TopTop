package com.toptop.fragment;

import static android.app.Activity.RESULT_OK;
import static com.toptop.MainActivity.REQUEST_ADD_VIDEO;
import static com.toptop.MainActivity.REQUEST_CHANGE_AVATAR;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.toptop.EditProfileActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.VideoGridAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFragment extends Fragment {
	// Tag
	public static final String TAG = "ProfileFragment";

	private static final int HEADER_HEIGHT = 8 * 2 + 32 + 1;

	TextView fullname, numFollowers, numFollowing, numLikes, username, txt_post_video;
	ImageView avatar, ic_menu, ic_edit, ic_edit_avatar, ic_add_video;
	RecyclerView recyclerView;
	Context context;
	NestedScrollView scrollView;
	ConstraintLayout layoutProfile;
	NavigationView navigationView;
	EditText edt_video_content;

	Boolean isLogin = false;
	StorageReference imageRef, videoRef;
	Uri videoUri;

	public ProfileFragment() {
	}

	@SuppressLint("SetTextI18n")
	public void updateUI(User user) {
		if (user != null) {
			Log.i(TAG, "updateUI: " + user.toString());
			fullname.setText(user.getFullname());
			numFollowers.setText(user.getNumFollowers() + "");
			numFollowing.setText(user.getNumFollowing() + "");
			numLikes.setText(user.getNumLikes() + "");
			username.setText(user.getUsername());
			Glide.with(context)
					.load(user.getAvatar())
					.error(R.drawable.default_avatar)
					.into(avatar);
			prepareRecyclerView(user);
		} else {

		}
	}

	@SuppressLint("SetTextI18n")
	public void updateUI() {
		if (isLogin) {
			User user = MainActivity.getCurrentUser();
			updateUI(user);
		} else {
			Log.e(TAG, "updateUI: User is null");
		}
	}

	private void prepareRecyclerView(User user) {
		Query query = FirebaseUtil.getVideoByUsername(user.getUsername());

		// First time load
		query.get().addOnSuccessListener(snapshot -> {
			if (snapshot.exists()) {
				List<Video> videos = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					Video video = new Video(dataSnapshot);
					videos.add(video);
				}
				VideoGridAdapter adapter = new VideoGridAdapter(videos, context);
				recyclerView.setAdapter(adapter);

				recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
			}
		});

		// Update when any video is changed
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				VideoGridAdapter videoGridAdapter = (VideoGridAdapter) recyclerView.getAdapter();
				if (videoGridAdapter != null) {
					List<Video> videos = videoGridAdapter.getVideos();
					for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
						Video video = new Video(dataSnapshot);
						if (videos.contains(video)) {
							videos.set(videos.indexOf(video), video);
						} else {
							videos.add(video);
						}
					}
					videoGridAdapter.setVideos(videos);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		if (MainActivity.isLoggedIn()) {
			isLogin = true;
			View view = inflater.inflate(R.layout.fragment_profile, container, false);
			this.context = view.getContext();

			// Bind view
			fullname = view.findViewById(R.id.txt_fullname);
			numFollowers = view.findViewById(R.id.txt_num_followers);
			numFollowing = view.findViewById(R.id.txt_num_following);
			numLikes = view.findViewById(R.id.txt_num_likes);
			username = view.findViewById(R.id.txt_email);
			avatar = view.findViewById(R.id.img_avatar);
			recyclerView = view.findViewById(R.id.recycler_view_videos_grid);
			scrollView = view.findViewById(R.id.scroll_view);
			layoutProfile = view.findViewById(R.id.layout_profile);
			ic_menu = view.findViewById(R.id.ic_menu);
			ic_edit = view.findViewById(R.id.ic_edit);
			ic_edit_avatar = view.findViewById(R.id.ic_edit_avatar);
			navigationView = view.findViewById(R.id.nav_view);
			DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
			imageRef = FirebaseUtil.getImageStorageReference();
			videoRef = FirebaseUtil.getVideoStorageReference();
			txt_post_video = view.findViewById(R.id.txt_post_video);
			edt_video_content = view.findViewById(R.id.edt_video_content);
			ic_add_video = view.findViewById(R.id.ic_add_video);

			ic_add_video.setOnClickListener(v -> handleAddVideo());

			txt_post_video.setOnClickListener(v -> handlePostVideo());

			ic_menu.setOnClickListener(v -> drawer.open());

			ic_edit.setOnClickListener(v -> openEditProfileActivity());

			ic_edit_avatar.setOnClickListener(v -> handleEditAvatar());

			navigationView.setNavigationItemSelectedListener(v -> {
				switch (v.getItemId()) {
					case R.id.log_out:
						MainActivity mainActivity = (MainActivity) requireActivity();
						mainActivity.logOut();
						Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
				}

				drawer.close();
				return false;
			});


			updateUI();

			return view;
		} else {
			isLogin = false;
			View view = inflater.inflate(R.layout.fragment_profile_no_login, container, false);
			this.context = view.getContext();

			// Bind view
			Button btnLogin = view.findViewById(R.id.btn_login);
			btnLogin.setOnClickListener(v -> ((MainActivity) requireActivity()).openLoginActivity());

			Button btnRegister = view.findViewById(R.id.btn_register);
			btnRegister.setOnClickListener(v -> ((MainActivity) requireActivity()).openRegisterActivity());

			return view;
		}
	}

	private void handlePostVideo() {
		KeyboardUtils.hideKeyboard(requireActivity());
		if (videoUri != null) {
			String content = edt_video_content.getText().toString().trim();
			if (content.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Đăng video không có nội dung?");
				builder.setMessage("Bạn có chắc chắn muốn đăng video mà không có nội dung?");
				builder.setPositiveButton("Đồng ý", (dialog, which) -> {
					dialog.dismiss();
					uploadVideo(content);
				});
				builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
				builder.show();
			} else {
				uploadVideo(content);
			}
		} else {
			Toast.makeText(context, "Bạn chưa chọn video", Toast.LENGTH_SHORT).show();
		}
	}

	private void uploadVideo(String content) {
		if (videoUri != null) {
			// Progress bar
			final ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Đang đăng video");
			progressDialog.setMessage("Vui lòng chờ...");
			progressDialog.show();

			String videoName = UUID.randomUUID().toString();
			StorageReference videoRef = FirebaseUtil.getVideoStorageReference().child(videoName);
			videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
				videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
					String videoUrl = uri.toString();
					Video video = new Video();
					video.setLinkVideo(videoUrl);
					video.setContent(content);
					video.setUsername(MainActivity.getCurrentUser().getUsername());
					VideoFirebase.addVideo(video);
					Toast.makeText(context, "Đăng video thành công", Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					Glide.with(context).load(R.drawable.ic_add_video).into(ic_add_video);
					edt_video_content.setText("");
				});
			}).addOnFailureListener(e -> {
				Toast.makeText(context, "Đăng video thất bại", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}).addOnProgressListener(taskSnapshot -> {
				double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
				progressDialog.setMessage("Đang đăng video... " + (int) progress + "%");
			});
		}
	}

	private void handleAddVideo() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		startActivityForResult(intent, REQUEST_ADD_VIDEO);
	}

	private void handleEditAvatar() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CHANGE_AVATAR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CHANGE_AVATAR && resultCode == RESULT_OK && data != null) {
			Uri uri = data.getData();
			uploadImage(uri);
		} else if (requestCode == REQUEST_ADD_VIDEO && resultCode == RESULT_OK && data != null) {
			videoUri = data.getData();
			Glide.with(context).load(videoUri).into(ic_add_video);
		}
	}

	private void uploadImage(Uri uri) {
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Đang tải ảnh lên");
		progressDialog.show();

		User user = MainActivity.getCurrentUser();

		String imageName = user.getUid() + ".jpg";
		final StorageReference imageRef = FirebaseUtil.getImageStorageReference().child(imageName);
		imageRef.putFile(uri)
				.addOnSuccessListener(taskSnapshot -> {
					imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
						user.setAvatar(downloadUrl.toString());
						UserFirebase.updateUser(user);
						progressDialog.dismiss();
						Toast.makeText(context, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
					});
				})
				.addOnFailureListener(e -> {
					progressDialog.dismiss();
					Toast.makeText(context, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
				})
				.addOnProgressListener(taskSnapshot -> {
					double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
					progressDialog.setMessage("Đang tải lên " + (int) progress + "%");
				});
	}

	private void openEditProfileActivity() {
		Intent intent = new Intent(context, EditProfileActivity.class);
		intent.putExtra(User.TAG, MainActivity.getCurrentUser());
		startActivity(intent);
	}

}