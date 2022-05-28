package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.WatchVideoActivity;
import com.toptop.adapters.CommentFragmentAdapter;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.models.Comment;
import com.toptop.models.Video;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentFragment extends Fragment {
	public static final String TAG = "CommentFragment";
	public static Comment newComment = new Comment();
	private final Video video;
	Context context;
	RecyclerView recycler_view_comments, recycler_view_videos;
	List<Comment> comments = new ArrayList<>();
	private final DatabaseReference mDB_comment;

	private static List<CommentFragment> instances = new ArrayList<>();

	private CommentFragment(Video video, Context context) {
		mDB_comment = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		this.video = video;
		this.context = context;
	}

	// New instance
	public static CommentFragment getInstance(Video video, Context context) {
		for (CommentFragment instance : instances) {
			if (instance.video.getVideoId().equals(video.getVideoId())) {
				return instance;
			}
		}
		CommentFragment instance = new CommentFragment(video, context);
		instances.add(instance);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_comment, container, false);

		view.setVisibility(View.VISIBLE);

		// Reset new comment
		newComment = new Comment();

		// Disable scrollable when layout comment is visible
		recycler_view_comments = view.findViewById(R.id.recycler_view_comments);
		recycler_view_videos = requireActivity().findViewById(R.id.recycler_view_videos);
		recycler_view_videos.addOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
		recycler_view_comments.setLayoutManager(new LinearLayoutManager(context));

		// Get comments from firebase by video id
		getCommentsFromFirebase();

		// Avatar
		ImageView avatar = view.findViewById(R.id.img_avatar);
		if (MainActivity.getCurrentUser() != null) {
			Glide.with(context)
					.load(MainActivity.getCurrentUser().getAvatar())
					.error(R.drawable.default_avatar)
					.into(avatar);
		} else {
			Glide.with(context)
					.load(R.drawable.default_avatar)
					.into(avatar);
		}

		EditText txt_comment_input = view.findViewById(R.id.txt_comment_input);
		ImageView ic_send_comment = view.findViewById(R.id.ic_send_comment);
		txt_comment_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
				String content = charSequence.toString().trim();
				if (content.length() > 0)
					ic_send_comment.setVisibility(View.VISIBLE);
				else ic_send_comment.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		// Set onClickListener for ic_send_comment
		ic_send_comment.setOnClickListener(v -> handleSendComment(txt_comment_input));

		View layout_comment_header = view.findViewById(R.id.layout_comment_header);
		layout_comment_header.setOnClickListener(v -> {
			// Hide this fragment
			if (requireActivity() instanceof MainActivity) {
				((MainActivity) requireActivity()).hideCommentFragment();
			} else if (requireActivity() instanceof WatchVideoActivity) {
				((WatchVideoActivity) requireActivity()).hideCommentFragment();
			}
		});

		// Disable comment input if user is not logged in
		if (!MainActivity.isLoggedIn()) {
			txt_comment_input.setEnabled(false);
			txt_comment_input.setHint("Chưa đăng nhập");
		}

		// Refresh image
		ImageView ic_refresh = view.findViewById(R.id.ic_refresh);
		ic_refresh.setOnClickListener(v -> {
			// Rotate refresh icon
			RotateAnimation r =
					new RotateAnimation(0f, 360f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
			r.setDuration((long) 2 * 500);
			r.setRepeatCount(0);
			ic_refresh.startAnimation(r);

			// Refresh comment
			getCommentsFromFirebase();
		});

		ImageView ic_cancel_reply = view.findViewById(R.id.ic_cancel_reply);
		ic_cancel_reply.setOnClickListener(v -> {
			// Set reply to comment id
			CommentFragment.newComment.setReplyToCommentId(null);

			// Hide reply comment title in input
			TextView title = ((MainActivity) context).findViewById(R.id.txt_reply_comment_title);
			title.setText("");
			ConstraintLayout layout_reply_comment_title = ((MainActivity) context).findViewById(R.id.layout_reply_comment_title);
			layout_reply_comment_title.setVisibility(View.GONE);
		});

		return view;
	}

	private void handleSendComment(EditText txt_comment_input) {
		// Get comment content
		String content = txt_comment_input.getText().toString().trim();
		newComment.setContent(content);
		newComment.setTime(MyUtil.dateTimeToString(new Date()));
		newComment.setUsername(MainActivity.getCurrentUser().getUsername());
		newComment.setVideoId(video.getVideoId());

		// Add comment to firebase
		CommentFirebase.addCommentToVideo(newComment, video);

		// Toast message
		Toast.makeText(context, "Bình luận thành công!", Toast.LENGTH_SHORT).show();

		// Refresh comments
		getCommentsFromFirebase();

		// Scroll to top
		recycler_view_comments.scrollToPosition(0);

		// Clear comment input
		txt_comment_input.setText("");

		// Reset new comment
		newComment = new Comment();

		// Hide keyboard
		KeyboardUtils.hideKeyboard(requireActivity());
	}

	private void getCommentsFromFirebase() {
		CommentFirebase.getCommentByVideoId(video.getVideoId(), listComments -> {
			comments.clear();
			comments.addAll(listComments);
			Comment.sortByTimeNewsest(comments);
			if (recycler_view_comments.getAdapter() != null)
				recycler_view_comments.getAdapter()
						.notifyItemRangeChanged(0, comments.size());
			else {
				// Set adapter for recycler view
				CommentFragmentAdapter adapter = new CommentFragmentAdapter(comments, context);
				recycler_view_comments.setAdapter(adapter);
			}
		}, error -> {
			Toast.makeText(context, "Lỗi khi lấy bình luận!", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "getCommentsFromFirebase: " + error);
		});
	}
}