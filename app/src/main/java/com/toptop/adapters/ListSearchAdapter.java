package com.toptop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toptop.R;
import com.toptop.models.Video;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ListSearchAdapter extends BaseAdapter {
	// Declare Variables

	Context mContext;
	LayoutInflater inflater;
	private List<Video> videoList = null;
	private ArrayList<Video> arraylist;

	public ListSearchAdapter(Context context, List<Video> videoList) {
		mContext = context;
		this.videoList = videoList;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<Video>();
		this.arraylist.addAll(videoList);
	}

	public class ViewHolder {
		TextView usename;
		TextView content;
		ImageView image;
		TextView time;
	}


	@Override
	public int getCount() {
		return videoList.size();
	}

	@Override
	public Video getItem(int position) {
		return videoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;

		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.items_listsearch, null);

			holder.usename = (TextView) view.findViewById(R.id.txt_usernameSearch);
			holder.content = (TextView) view.findViewById(R.id.txt_contentSearch);
			holder.image = (ImageView) view.findViewById(R.id.img_Search);
			holder.time = (TextView) view.findViewById(R.id.txt_timePost);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.usename.setText(videoList.get(position).getUsername());
		holder.content.setText(videoList.get(position).getContent());
		holder.time.setText(videoList.get(position).getDateUploaded());

		return view;
	}

	//    function covert date to string
	public String convertDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = dateFormat.format(date);
		return strDate;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		videoList.clear();
		if (charText.length() == 0) {
			videoList.addAll(arraylist);
		} else {
			for (Video wp : arraylist) {
				if (wp.getContent().toLowerCase(Locale.getDefault()).contains(charText)) {
					videoList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
}
