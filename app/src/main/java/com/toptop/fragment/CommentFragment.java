package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.CommentFragmentAdapter;
import com.toptop.adapters.VideoFragementAdapter;
import com.toptop.models.Comment;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentFragment extends Fragment {
	private DatabaseReference mDB_comment;
	public static final String TAG = "CommentFragment";

	private final Video video;
	Context context;

	public CommentFragment(Video video, Context context) {
		mDB_comment = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_COMMENTS);
		this.video = video;
		this.context = context;
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

		// Disable scrollable when layout comment is visible
		RecyclerView recycler_view_comments = view.findViewById(R.id.recycler_view_comments);
		RecyclerView recycler_view_videos = requireActivity().findViewById(R.id.recycler_view_videos);
		recycler_view_videos.addOnItemTouchListener(VideoFragementAdapter.disableTouchListener);

		// Get comments from firebase by video id
		Query mDB_comment_query = FirebaseUtil.getCommentsByVideoId(video.getVideoId());
		mDB_comment_query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Log.i(TAG, "onDataChange: RELOAD COMMENT FOR VIDEO: " + video.getVideoId());

				// Get all comments from firebase
				List<Comment> comments = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren())
					comments.add(new Comment(dataSnapshot));

				// Set adapter for recycler view
				Comment.sortByTimeNewsest(comments);
				CommentFragmentAdapter adapter = new CommentFragmentAdapter(comments, context);
				recycler_view_comments.setAdapter(adapter);
				recycler_view_comments.setLayoutManager(new LinearLayoutManager(context));

				// Set number of comment
				VideoFragementAdapter videoFragementAdapter = (VideoFragementAdapter) recycler_view_videos.getAdapter();
				int position = videoFragementAdapter.getPosition(video.getVideoId());
				long current_number_of_comment = videoFragementAdapter.getNumberOfComment(position);

				if (current_number_of_comment != comments.size()) {
					videoFragementAdapter.notifyItemChanged(position, video);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "onCancelled: ", error.toException());
			}
		});

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
		ic_send_comment.setOnClickListener(v -> {
			// Get comment content
			String content = txt_comment_input.getText().toString().trim();
			Comment newComment = new Comment();
			newComment.setContent(content);
			newComment.setTime(MyUtil.getFormattedDateStringFromDate(new Date()));
			newComment.setUsername("thanhnam");
			newComment.setVideoId(video.getVideoId());

			// Add comment to firebase
			CommentFirebase.addCommentToVideo(newComment, video);

			// Toast message
			Toast.makeText(context, "Comment success", Toast.LENGTH_SHORT).show();

			// Clear comment input
			txt_comment_input.setText("");
		});

		View layout_comment_header = view.findViewById(R.id.layout_comment_header);
		layout_comment_header.setOnClickListener(v -> {
			Log.i(TAG, "CLICK COMMENT HEADER");
			if (((MainActivity) context).findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE)
				((MainActivity) context).onBackPressed();
		});

		return view;
	}
}