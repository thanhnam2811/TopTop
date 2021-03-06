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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
	static String commentId;

	private static final List<CommentFragment> instances = new ArrayList<>();

	private CommentFragment(Video video, Context context) {
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

	public static CommentFragment getInstance(Video video, Context context, String cID) {
		commentId = cID;
		return getInstance(video, context);
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
		recycler_view_comments.setLayoutManager(new LinearLayoutManager(context));// Decorate the list
		recycler_view_comments.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

		// Get comments from firebase by video id
		getCommentsFromFirebase();

		// Avatar
		ImageView avatar = view.findViewById(R.id.img_avatar);

		try {
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
		} catch (Exception e) {
			Log.w(TAG, "Glide error: " + e.getMessage());
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
			txt_comment_input.setHint("Ch??a ????ng nh???p");
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
		Toast.makeText(context, "B??nh lu???n th??nh c??ng!", Toast.LENGTH_SHORT).show();

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
		comments.clear();
		if (video.getComments() != null) {
			video.getComments().forEach((commentId, value) -> {
				CommentFirebase.getCommentByCommentIdOneTime(commentId,
						comment -> {
							comments.add(comment);
							if (comments.size() == video.getComments().size()) {
								comments.removeIf(c -> !c.isValid());
								// Sort comments by time
								Comment.sortByTimeNewsest(comments);

								CommentFragmentAdapter adapter = (CommentFragmentAdapter) recycler_view_comments.getAdapter();
								if (adapter != null)
									adapter.setComments(comments);
								else {
									// Set adapter for recycler view
									adapter = new CommentFragmentAdapter(comments, context);
									recycler_view_comments.setAdapter(adapter);
								}
								if (CommentFragment.commentId != null) {
									// Scroll to comment
									scrollToComment(commentId);
								}
							}
						}, error -> {
							Toast.makeText(context, "L???i khi l???y b??nh lu???n!", Toast.LENGTH_SHORT).show();
						}
				);
			});
		}
	}

	public void scrollToComment(String commentId) {
		boolean found = false;
		// Scroll to comment
		for (int i = 0; i < comments.size(); i++) {
			if (comments.get(i).getCommentId().equals(commentId)) {
				recycler_view_comments.scrollToPosition(i);
				found = true;
				break;
			}
		}
		if (!found) {
			Toast.makeText(context, "Kh??ng t??m th???y b??nh lu???n!", Toast.LENGTH_SHORT).show();
		}
	}
}