package com.toptop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.R;
import com.toptop.utils.RecyclerViewDisabler;

public class SearchNotFoundAdapter extends RecyclerView.Adapter<SearchNotFoundAdapter.SearchNotFoundViewHolder> {
	private static final String TAG = "SearchNotFoundAdapter";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	Context context;
	private String output;

	public SearchNotFoundAdapter(Context context, String output) {
		this.output = output;
		this.context = context;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@NonNull
	@Override
	public SearchNotFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_search_not_found, parent, false);
		return new SearchNotFoundViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SearchNotFoundViewHolder holder, int position) {
		holder.bind(output);
	}

	@Override
	public int getItemCount() {
		return 1;
	}

	static class SearchNotFoundViewHolder extends RecyclerView.ViewHolder {
		TextView textView;

		public SearchNotFoundViewHolder(@NonNull View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.txt_search_not_found);
		}

		public void bind(String output) {
			textView.setText(output);
		}
	}

}
